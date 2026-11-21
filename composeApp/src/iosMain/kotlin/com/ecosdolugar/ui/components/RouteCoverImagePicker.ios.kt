package com.ecosdolugar.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
actual fun RouteCoverImagePicker(
    onImageSelected: (String) -> Unit,
    onError: (String) -> Unit
) {
    Text("En iOS, introduce la URL de la imagen en el campo.")
}
