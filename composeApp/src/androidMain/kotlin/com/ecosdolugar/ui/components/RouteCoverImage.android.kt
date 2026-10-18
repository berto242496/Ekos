package com.ecosdolugar.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
actual fun RouteCoverImage(url: String, modifier: Modifier) {
    val image = remember(url) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    LaunchedEffect(url) {
        image.value = withContext(Dispatchers.IO) {
            runCatching {
                if (url.startsWith("data:")) {
                    val bytes = Base64.decode(url.substringAfter("base64,"), Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                } else {
                    URL(url).openStream().use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
                }
            }.getOrNull()
        }
    }
    val loadedImage = image.value
    if (loadedImage == null) {
        Box(modifier.background(Color(0xFF183321)))
    } else {
        Image(
            bitmap = loadedImage,
            contentDescription = "Imagen de portada de la ruta",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}
