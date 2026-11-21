package com.ecosdolugar.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

private const val MAX_IMAGE_DIMENSION = 2048
private const val JPEG_QUALITY = 85
private const val MAX_UPLOAD_BYTES = 10 * 1024 * 1024

@Composable
actual fun RouteCoverImagePicker(
    onImageSelected: (String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            val resolver = context.contentResolver
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
                throw IllegalArgumentException("No se pudo leer la imagen seleccionada.")
            }
            val sampleSize = generateSequence(1) { it * 2 }
                .takeWhile { maxOf(bounds.outWidth / it, bounds.outHeight / it) > MAX_IMAGE_DIMENSION }
                .lastOrNull() ?: 1
            val bitmap = resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                })
            } ?: throw IllegalArgumentException("No se pudo procesar la imagen seleccionada.")
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
            bitmap.recycle()
            val bytes = output.toByteArray()
            if (bytes.size > MAX_UPLOAD_BYTES) {
                throw IllegalArgumentException("La imagen comprimida supera el límite de 10 MB.")
            }
            onImageSelected("data:image/jpeg;base64,${Base64.encodeToString(bytes, Base64.NO_WRAP)}")
        } catch (error: Exception) {
            onError(error.message ?: "No se pudo procesar la imagen seleccionada.")
        }
    }

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Elegir imagen del dispositivo")
    }
}
