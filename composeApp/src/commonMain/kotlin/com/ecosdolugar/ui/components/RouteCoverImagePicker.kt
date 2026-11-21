package com.ecosdolugar.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun RouteCoverImagePicker(
    onImageSelected: (String) -> Unit,
    onError: (String) -> Unit
)
