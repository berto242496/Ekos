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
import com.ecosdolugar.auth.CurrentActivity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Cobro de rutas con Google Play Billing. Las compras son consumibles: el servidor
 * verifica el token, registra la compra en la cuenta y la consume.
 */
class GooglePlayRoutePaymentService(context: Context) : RoutePaymentService {

    private val billingClient: BillingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener { result, purchases -> onPurchasesUpdated(result, purchases) }
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    @Volatile
    private var pendingPurchase: CompletableDeferred<RoutePurchaseToken?>? = null

    @Volatile
    private var pendingProductId: String? = null

    override suspend fun purchase(productId: String): RoutePurchaseToken? {
        ensureConnected()
        val details = queryProductDetails(productId)
            ?: throw IllegalStateException("Ruta no disponible para la compra en este momento.")
        val activity = CurrentActivity.get()
            ?: throw IllegalStateException("No se puede abrir la compra sin una pantalla activa.")

        val deferred = CompletableDeferred<RoutePurchaseToken?>()
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
                .build()
            val result = billingClient.launchBillingFlow(activity, flowParams)
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) return null
                throw IllegalStateException(result.debugMessage.ifBlank { "No se pudo iniciar el pago." })
            }
            return deferred.await()
        } finally {
            pendingPurchase = null
            pendingProductId = null
        }
    }

    private fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        val deferred = pendingPurchase ?: return
        val productId = pendingProductId
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val purchase = purchases.orEmpty().firstOrNull { productId != null && productId in it.products }
                when {
                    purchase == null -> deferred.completeExceptionally(
                        IllegalStateException("No se recibió la compra de Google Play.")
                    )
                    purchase.purchaseState == Purchase.PurchaseState.PENDING -> deferred.completeExceptionally(
                        IllegalStateException("El pago está pendiente de confirmación en Google Play.")
                    )
                    else -> deferred.complete(RoutePurchaseToken(productId!!, purchase.purchaseToken))
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> deferred.complete(null)
            else -> deferred.completeExceptionally(
                IllegalStateException(result.debugMessage.ifBlank { "El pago no se completó." })
            )
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
