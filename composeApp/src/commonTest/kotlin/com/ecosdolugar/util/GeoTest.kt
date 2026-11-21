package com.ecosdolugar.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeoTest {
    @Test
    fun oneDegreeOfLatitudeIsAbout111Km() {
        assertEquals(111_195.0, distanceMeters(0.0, 0.0, 1.0, 0.0), 10.0)
    }

    @Test
    fun samePointHasZeroDistance() {
        assertEquals(0.0, distanceMeters(42.04, -8.64, 42.04, -8.64))
    }

    @Test
    fun pathSumsConsecutiveLegsOnly() {
        val a = 42.0 to -8.0
        val b = 42.0 to -7.99
        val c = 42.0 to -7.98
        assertEquals(distanceMeters(42.0, -8.0, 42.0, -7.98), pathDistanceMeters(listOf(a, b, c)), 0.5)
        assertEquals(0.0, pathDistanceMeters(listOf(a)))
        assertEquals(0.0, pathDistanceMeters(emptyList()))
    }

    @Test
    fun formatsKilometersWithCommaAndTwoDecimals() {
        assertEquals("0,00 km", formatKilometers(0.0))
        assertEquals("1,25 km", formatKilometers(1250.0))
        assertEquals("0,05 km", formatKilometers(50.0))
        assertTrue(formatKilometers(12_345.0).startsWith("12,"))
    }
}
