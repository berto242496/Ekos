package com.ecosdolugar.ui.components

import androidx.compose.ui.graphics.ImageBitmap

/** Devuelve la imagen ya cargada en caché, o null si todavía no se ha descargado. */
expect fun cachedRouteImage(url: String): ImageBitmap?

/** Descarga y decodifica la imagen (con caché). Devuelve null si no se puede cargar. */
expect suspend fun loadRouteImage(url: String): ImageBitmap?
