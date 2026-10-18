package com.ecosdolugar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ecosdolugar.ui.theme.TerracottaGold

/** Estado de carga de una imagen de ruta o parada. */
sealed interface RouteImageState {
    data object Loading : RouteImageState
    data object Failed : RouteImageState
    class Ready(val bitmap: ImageBitmap) : RouteImageState
}

/** Empieza con la imagen cacheada si existe, para no mostrar spinner innecesario. */
@Composable
fun rememberRouteImage(url: String): State<RouteImageState> =
    produceState(initialValue = initialRouteImageState(url), key1 = url) {
        if (value is RouteImageState.Ready) return@produceState
        value = loadRouteImage(url)?.let { RouteImageState.Ready(it) } ?: RouteImageState.Failed
    }

private fun initialRouteImageState(url: String): RouteImageState =
    cachedRouteImage(url)?.let { RouteImageState.Ready(it) } ?: RouteImageState.Loading

@Composable
fun RouteCoverImage(url: String, modifier: Modifier = Modifier) {
    val state by rememberRouteImage(url)
    when (val current = state) {
        is RouteImageState.Ready -> Image(
            bitmap = current.bitmap,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
        RouteImageState.Loading -> Box(
            modifier = modifier.background(Color(0xFF183321)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TerracottaGold, strokeWidth = 2.dp)
        }
        RouteImageState.Failed -> Box(modifier = modifier.background(Color(0xFF183321)).fillMaxSize())
    }
}
