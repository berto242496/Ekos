package com.ecosdolugar.payment

/** Compra de Google Play asociada a su ruta, pendiente de verificación en el servidor. */
data class RoutePurchaseToken(val routeId: String, val productId: String, val purchaseToken: String)

/**
 * Cobro de rutas de pago. Devuelve null si el usuario cancela la compra.
 * Lanza excepción si el pago no está disponible o falla.
 * [accountId] identifica al comprador en Google Play; el servidor comprueba que coincida con su sesión.
 */
interface RoutePaymentService {
    suspend fun purchase(routeId: String, productId: String, accountId: String): RoutePurchaseToken?

    /**
     * Compras confirmadas por Google Play que aún no constan en el servidor.
     * Permite reintentar la verificación tras un fallo de red o un cierre de la app.
     */
    suspend fun unverifiedPurchases(): List<RoutePurchaseToken> = emptyList()

    /** Quita una compra de la lista de pendientes una vez verificada en el servidor. */
    fun markVerified(purchaseToken: String) = Unit
}

/** Plataformas sin pagos (escritorio, iOS): cualquier intento de compra falla de forma explícita. */
object UnavailableRoutePaymentService : RoutePaymentService {
    override suspend fun purchase(routeId: String, productId: String, accountId: String): RoutePurchaseToken? =
        throw UnsupportedOperationException("Los pagos de rutas solo están disponibles en Android.")
}
