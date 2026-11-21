package com.ecosdolugar.util

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_METERS = 6_371_000.0

/** Great-circle (haversine) distance between two coordinates. */
fun distanceMeters(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
    fun radians(degrees: Double) = degrees * PI / 180
    val latitudeDelta = radians(latitude2 - latitude1)
    val longitudeDelta = radians(longitude2 - longitude1)
    val a = sin(latitudeDelta / 2) * sin(latitudeDelta / 2) +
        cos(radians(latitude1)) * cos(radians(latitude2)) *
        sin(longitudeDelta / 2) * sin(longitudeDelta / 2)
    return EARTH_RADIUS_METERS * 2 * atan2(sqrt(a), sqrt(1 - a))
}

/** Straight-line length of the path that visits the points in order. */
fun pathDistanceMeters(points: List<Pair<Double, Double>>): Double =
    points.zipWithNext { from, to -> distanceMeters(from.first, from.second, to.first, to.second) }.sum()

/** Formats meters as kilometres with two decimals and a comma, e.g. "1,25 km". */
fun formatKilometers(meters: Double): String {
    val hundredths = (meters / 10).roundToInt()
    return "${hundredths / 100},${(hundredths % 100).toString().padStart(2, '0')} km"
}
