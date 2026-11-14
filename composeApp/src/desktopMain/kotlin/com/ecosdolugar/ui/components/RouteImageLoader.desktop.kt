package com.ecosdolugar.ui.components

import androidx.compose.ui.graphics.ImageBitmap

actual fun cachedRouteImage(url: String): ImageBitmap? = null

actual suspend fun loadRouteImage(url: String): ImageBitmap? = null
