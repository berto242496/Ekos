package com.ecosdolugar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** Retrato circular de un personaje; ocupa todo el contenedor que le den. */
@Composable
fun CharacterAvatarImage(image: DrawableResource, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(image),
        contentDescription = null,
        modifier = modifier.fillMaxSize().clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
