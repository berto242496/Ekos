package com.ecosdolugar.payment

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.ecosdolugar.auth.CurrentActivity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume

/**
 * Cobro de rutas con Google Play Billing. Las compras son consumibles: el servidor
 * verifica el token, registra la compra en la cuenta y la consume.
 *
 * Cada compra recibida de Play se guarda en local (routeId + token) hasta que el servidor
 * la verifica, para poder reintentarla si falla la red o se cierra la app.
 */
class GooglePlayRoutePaymentService(context: Context) : RoutePaymentService {

    private val billingClient: BillingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener { result, purchases -> onPurchasesUpdated(result, purchases) }
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    private val preferences = context.applicationContext
        .getSharedPreferences("route_purchases", Context.MODE_PRIVATE)

    private companion object {
        const val PENDING_KEY = "pending_purchases"
        const val MAX_PROFILE_ID_LENGTH = 64
    }

    @Volatile
    private var pendingPurchase: CompletableDeferred<RoutePurchaseToken?>? = null

    @Volatile
    private var pendingRouteId: String? = null

    @Volatile
    private var pendingProductId: String? = null

    override suspend fun purchase(routeId: String, productId: String, accountId: String): RoutePurchaseToken? {
        ensureConnected()
        val details = queryProductDetails(productId)
            ?: throw IllegalStateException("Ruta no disponible para la compra en este momento.")
        val activity = CurrentActivity.get()
            ?: throw IllegalStateException("No se puede abrir la compra sin una pantalla activa.")

        val deferred = CompletableDeferred<RoutePurchaseToken?>()
        pendingRouteId = routeId
        pendingProductId = productId
        pendingPurchase = deferred
        try {
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(details)
                            .build()
                    )
                )
                .setObfuscatedAccountId(accountId)
                .apply { if (routeId.length <= MAX_PROFILE_ID_LENGTH) setObfuscatedProfileId(routeId) }
                .build()
            val result = billingClient.launchBillingFlow(activity, flowParams)
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) return null
                throw IllegalStateException(result.debugMessage.ifBlank { "No se pudo iniciar el pago." })
            }
            return deferred.await()
        } finally {
            pendingPurchase = null
            pendingRouteId = null
            pendingProductId = null
        }
    }

    override suspend fun unverifiedPurchases(): List<RoutePurchaseToken> {
        val records = readRecords()
        ensureConnected()
        val open = queryInappPurchases()
        val openTokens = open.map { it.purchaseToken }.toSet()
        // Un token que ya no aparece en Play está consumido: el servidor lo verificó antes.
        writeRecords(records.filter { it.purchaseToken in openTokens })
        val recordsByToken = records.associateBy { it.purchaseToken }
        // La ruta viaja en la propia compra (obfuscatedProfileId): permite recuperar compras que la app
        // no llegó a registrar, p. ej. si se cerró durante el pago o Google la confirmó más tarde.
        return open
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            .mapNotNull { purchase ->
                val routeId = purchase.accountIdentifiers?.obfuscatedProfileId?.takeIf { it.isNotBlank() }
                    ?: recordsByToken[purchase.purchaseToken]?.routeId
                val productId = purchase.products.firstOrNull()
                if (routeId == null || productId == null) null
                else RoutePurchaseToken(routeId, productId, purchase.purchaseToken)
            }
    }

    override fun markVerified(purchaseToken: String) {
        writeRecords(readRecords().filterNot { it.purchaseToken == purchaseToken })
    }

    private fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        val deferred = pendingPurchase ?: return
        val routeId = pendingRouteId
        val productId = pendingProductId
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val purchase = purchases.orEmpty().firstOrNull { productId != null && productId in it.products }
                when {
                    purchase == null -> deferred.completeExceptionally(
                        IllegalStateException("No se recibió la compra de Google Play.")
                    )
                    purchase.purchaseState == Purchase.PurchaseState.PENDING -> {
                        // Google la reenviará como PURCHASED; al iniciar la app se verificará.
                        saveRecord(RoutePurchaseToken(routeId!!, productId!!, purchase.purchaseToken))
                        deferred.completeExceptionally(
                            IllegalStateException("El pago está pendiente de confirmación en Google Play.")
                        )
                    }
                    else -> {
                        val record = RoutePurchaseToken(routeId!!, productId!!, purchase.purchaseToken)
                        saveRecord(record)
                        deferred.complete(record)
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> deferred.complete(null)
            else -> deferred.completeExceptionally(
                IllegalStateException(result.debugMessage.ifBlank { "El pago no se completó." })
            )
        }
    }

    private fun readRecords(): List<RoutePurchaseToken> {
        val raw = preferences.getString(PENDING_KEY, null) ?: return emptyList()
        val array = JSONArray(raw)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            RoutePurchaseToken(
                routeId = item.getString("routeId"),
                productId = item.getString("productId"),
                purchaseToken = item.getString("purchaseToken")
            )
        }
    }

    private fun writeRecords(records: List<RoutePurchaseToken>) {
        val array = JSONArray()
        records.forEach { record ->
            array.put(
                JSONObject()
                    .put("routeId", record.routeId)
                    .put("productId", record.productId)
                    .put("purchaseToken", record.purchaseToken)
            )
        }
        preferences.edit().putString(PENDING_KEY, array.toString()).apply()
    }

    private fun saveRecord(record: RoutePurchaseToken) {
        writeRecords(readRecords().filterNot { it.purchaseToken == record.purchaseToken } + record)
    }

    /** Compras no consumidas en Play. Lanza error si la consulta falla. */
    private suspend fun queryInappPurchases(): List<Purchase> {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(params) { result, purchases ->
                if (!continuation.isActive) return@queryPurchasesAsync
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(purchases)
                } else {
                    continuation.resumeWith(Result.failure(IllegalStateException("No se pudieron consultar las compras de Google Play.")))
                }
            }
        }
    }

    private suspend fun ensureConnected() {
        if (billingClient.isReady) return
        suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (!continuation.isActive) return
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWith(Result.failure(IllegalStateException("Google Play no está disponible.")))
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Se reconecta en la siguiente compra.
                }
            })
        }
    }

    private suspend fun queryProductDetails(productId: String): ProductDetails? {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()
        return suspendCancellableCoroutine { continuation ->
            billingClient.queryProductDetailsAsync(params) { result, queryResult ->
                if (continuation.isActive) {
                    continuation.resume(
                        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                            queryResult.productDetailsList.firstOrNull { it.productId == productId }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}
