package com.ecosdolugar.payment

/** Compra confirmada por Google Play, pendiente de verificación en el servidor. */
data class RoutePurchaseToken(val productId: String, val purchaseToken: String)

/**
 * Cobro de rutas de pago. Devuelve null si el usuario cancela la compra.
 * Lanza excepción si el pago no está disponible o falla.
 */
interface RoutePaymentService {
    suspend fun purchase(productId: String): RoutePurchaseToken?
}

/** Plataformas sin pagos (escritorio, iOS): cualquier intento de compra falla de forma explícita. */
object UnavailableRoutePaymentService : RoutePaymentService {
    override suspend fun purchase(productId: String): RoutePurchaseToken? =
        throw UnsupportedOperationException("Los pagos de rutas solo están disponibles en Android.")
}
