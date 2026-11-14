package com.ecosdolugar.model

/** Ventas de una ruta de pago y lo generado para el guía (ya descontado el 65%). */
data class RouteSales(
    val salesCount: Int,
    val earnedCents: Long
)

/** Resumen de ventas del guía: lo generado, lo ya transferido y el saldo pendiente de cobro. */
data class GuideSalesSummary(
    val totalCents: Long,
    val paidCents: Long,
    val pendingCents: Long,
    val routes: Map<String, RouteSales>
)
