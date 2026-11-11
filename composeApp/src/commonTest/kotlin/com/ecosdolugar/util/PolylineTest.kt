package com.ecosdolugar.util

import kotlin.test.Test
import kotlin.test.assertEquals

class PolylineTest {
    @Test
    fun decodesGooglePolylineExample() {
        // Ejemplo oficial de la documentación de Google Encoded Polyline.
        val points = decodePolyline("_p~iF~ps|U_ulLnnqC_mqNvxq`@")

        assertEquals(3, points.size)
        assertEquals(38.5, points[0].latitude, 1e-6)
        assertEquals(-120.2, points[0].longitude, 1e-6)
        assertEquals(40.7, points[1].latitude, 1e-6)
        assertEquals(-120.95, points[1].longitude, 1e-6)
        assertEquals(43.252, points[2].latitude, 1e-6)
        assertEquals(-126.453, points[2].longitude, 1e-6)
    }

    @Test
    fun emptyStringDecodesToNoPoints() {
        assertEquals(emptyList(), decodePolyline(""))
    }
}
