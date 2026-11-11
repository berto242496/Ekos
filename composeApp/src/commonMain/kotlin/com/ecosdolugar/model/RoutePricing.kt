package com.ecosdolugar.model

/**
 * Precios de las rutas de pago. Google Play solo cobra productos configurados en Play Console,
 * así que el guía elige uno de estos tramos fijos. Cada tramo es un producto consumible con id
 * [productIdFor], que debe existir en Play Console.
 */
object RoutePricing {
    val TIERS_CENTS: List<Int> = listOf(199, 299, 499, 699, 999)

    /** Porcentaje del importe total que recibe el guía. El resto se queda en la plataforma. */
    const val GUIDE_SHARE_PERCENT = 65

    fun productIdFor(priceCents: Int): String = "route_tier_$priceCents"

    fun guideShareCents(priceCents: Int): Int = priceCents * GUIDE_SHARE_PERCENT / 100

    /** Formato español, por ejemplo 4,99 €. */
    fun formatEuros(cents: Int): String {
        val euros = cents / 100
        val rest = (cents % 100).toString().padStart(2, '0')
        return "$euros,$rest €"
    }
}
