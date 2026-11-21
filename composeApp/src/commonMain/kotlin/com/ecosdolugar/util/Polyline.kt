package com.ecosdolugar.util

import com.ecosdolugar.model.GeoPoint

/** Decodes a Google encoded polyline (precision 5), as returned by OpenRouteService. */
fun decodePolyline(encoded: String): List<GeoPoint> {
    val points = mutableListOf<GeoPoint>()
    var index = 0
    var latitude = 0
    var longitude = 0
    while (index < encoded.length) {
        var result = 0
        var shift = 0
        var byte: Int
        do {
            byte = encoded[index++].code - 63
            result = result or ((byte and 0x1f) shl shift)
            shift += 5
        } while (byte >= 0x20)
        latitude += if (result and 1 != 0) (result shr 1).inv() else result shr 1

        result = 0
        shift = 0
        do {
            byte = encoded[index++].code - 63
            result = result or ((byte and 0x1f) shl shift)
            shift += 5
        } while (byte >= 0x20)
        longitude += if (result and 1 != 0) (result shr 1).inv() else result shr 1

        points += GeoPoint(latitude / 1e5, longitude / 1e5)
    }
    return points
}
