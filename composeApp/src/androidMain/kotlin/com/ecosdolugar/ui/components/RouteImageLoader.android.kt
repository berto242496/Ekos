package com.ecosdolugar.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

private const val MAX_IMAGE_SIDE = 2048
private const val CACHE_KILOBYTES = 48 * 1024
private const val NETWORK_TIMEOUT_MS = 15_000

private val routeImageCache = object : LruCache<String, ImageBitmap>(CACHE_KILOBYTES) {
    override fun sizeOf(key: String, value: ImageBitmap): Int =
        value.width * value.height * 4 / 1024
}

actual fun cachedRouteImage(url: String): ImageBitmap? = routeImageCache.get(url)

actual suspend fun loadRouteImage(url: String): ImageBitmap? {
    routeImageCache.get(url)?.let { return it }
    return withContext(Dispatchers.IO) {
        runCatching { decodeRouteImage(url) }.getOrNull()?.also { routeImageCache.put(url, it) }
    }
}

private fun decodeRouteImage(url: String): ImageBitmap? {
    val bytes = if (url.startsWith("data:")) {
        Base64.decode(url.substringAfter("base64,"), Base64.DEFAULT)
    } else {
        val connection = URL(url).openConnection().apply {
            connectTimeout = NETWORK_TIMEOUT_MS
            readTimeout = NETWORK_TIMEOUT_MS
        }
        connection.getInputStream().use { it.readBytes() }
    }
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
    val largestSide = maxOf(bounds.outWidth, bounds.outHeight)
    if (largestSide <= 0) return null
    var sampleSize = 1
    while (largestSide / sampleSize > MAX_IMAGE_SIDE) sampleSize *= 2
    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)?.asImageBitmap()
}
