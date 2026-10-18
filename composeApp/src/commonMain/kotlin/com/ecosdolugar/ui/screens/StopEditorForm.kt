package com.ecosdolugar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.model.NarrationVoice
import com.ecosdolugar.model.StopAudioMode
import com.ecosdolugar.model.TrialType
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.components.RouteCoverImagePicker
import com.ecosdolugar.ui.components.RouteMapView
import com.ecosdolugar.ui.components.rememberAudioClipPlayer
import com.ecosdolugar.ui.components.rememberVoicePreviewer
import com.ecosdolugar.ui.components.rememberVoiceRecorder
import com.ecosdolugar.ui.theme.SurfaceCream
import com.ecosdolugar.ui.theme.TerracottaGold

private val trialTypeOptions = listOf(
    TrialType.ACERTIJO to "Test de preguntas",
    TrialType.PUZLE_IMAGEN to "Puzle de imagen",
    TrialType.ORDENAR_PALABRA to "Ordenar palabra",
    TrialType.COMPLETAR_FRASE to "Completar frase"
)

@Composable
internal fun StopEditorForm(
    form: StopFormState,
    existingCoordinates: List<Pair<Double, Double>>,
    language: AppLanguage,
    onMessage: (String?) -> Unit,
    onAddStop: () -> Unit
) {
    val previewer = rememberVoicePreviewer()
    val clipPlayer = rememberAudioClipPlayer()
    val recorder = rememberVoiceRecorder(
        onRecorded = { form.recordedAudioUrl = it; onMessage(null) },
        onError = onMessage
    )

    FieldLabel("Datos de la parada")
    RouteEditorTextField(
        value = form.title,
        onValueChange = { form.title = it },
        label = "Nombre de la parada",
        singleLine = true
    )
    RouteEditorTextField(
        value = form.location,
        onValueChange = { form.location = it },
        label = "Lugar o dirección",
        singleLine = true
    )
    RouteEditorTextField(
        value = form.story,
        onValueChange = { form.story = it },
        label = "Relato de la parada",
        minLines = 2
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel("Imagen de la parada")
        RouteEditorTextField(
            value = form.imageUrl.takeUnless { it.startsWith("data:") }.orEmpty(),
            onValueChange = { form.imageUrl = it },
            label = "URL de la imagen (opcional)",
            singleLine = true
        )
        RouteCoverImagePicker(
            onImageSelected = { form.imageUrl = it },
            onError = onMessage
        )
        if (form.imageUrl.isNotBlank()) {
            RouteCoverImage(
                url = form.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel("Narración del relato")
        NarrationModeDropdown(
            selected = form.audioMode,
            onSelected = { form.audioMode = it }
        )
        when (form.audioMode) {
            StopAudioMode.WRITTEN -> HelperText("El visitante leerá el relato en pantalla.")
            StopAudioMode.TTS -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SelectableChip(
                        text = "Voz femenina",
                        selected = form.narrationVoice == NarrationVoice.FEMALE,
                        onClick = { form.narrationVoice = NarrationVoice.FEMALE },
                        modifier = Modifier.weight(1f)
                    )
                    SelectableChip(
                        text = "Voz masculina",
                        selected = form.narrationVoice == NarrationVoice.MALE,
                        onClick = { form.narrationVoice = NarrationVoice.MALE },
                        modifier = Modifier.weight(1f)
                    )
                }
                HelperText(
                    "La voz la genera el dispositivo a partir del relato. Su calidad depende de las voces instaladas en el móvil."
                )
                SecondaryButton(
                    text = if (previewer.isSpeaking) "Detener ejemplo" else "Escuchar ejemplo",
                    onClick = {
                        when {
                            previewer.isSpeaking -> previewer.stop()
                            form.story.isBlank() -> onMessage("Escribe el relato para escuchar un ejemplo.")
                            else -> {
                                onMessage(null)
                                previewer.speak(form.story, form.narrationVoice)
                            }
                        }
                    }
                )
            }
            StopAudioMode.RECORDED -> {
                HelperText(
                    if (recorder.isRecording) {
                        "Grabando… pulsa de nuevo para terminar."
                    } else if (form.recordedAudioUrl != null) {
                        "Grabación guardada. Se subirá al publicar la ruta."
                    } else {
                        "Graba el relato con tu propia voz."
                    }
                )
                SecondaryButton(
                    text = when {
                        recorder.isRecording -> "Detener grabación"
                        form.recordedAudioUrl != null -> "Grabar de nuevo"
                        else -> "Grabar relato"
                    },
                    onClick = {
                        onMessage(null)
                        clipPlayer.stop()
                        recorder.toggle()
                    }
                )
                if (form.recordedAudioUrl != null && !recorder.isRecording) {
                    SecondaryButton(
                        text = if (clipPlayer.isPlaying) "Detener reproducción" else "Escuchar grabación",
                        onClick = {
                            if (clipPlayer.isPlaying) {
                                clipPlayer.stop()
                            } else {
                                form.recordedAudioUrl?.let { clipPlayer.play(it) }
                            }
                        }
                    )
                    SecondaryButton(
                        text = "Eliminar grabación",
                        onClick = {
                            clipPlayer.stop()
                            form.recordedAudioUrl = null
                        }
                    )
                }
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel("Ubicación")
        HelperText("Marca la ubicación de la parada en el mapa o introduce las coordenadas.")
        RouteMapView(
            routes = emptyList(),
            selectedRouteId = null,
            selectedStopId = null,
            onRouteSelected = {},
            onStopSelected = {},
            language = language,
            modifier = Modifier.fillMaxWidth().height(260.dp),
            onCoordinateSelected = { lat, lng ->
                form.latitude = lat.toString()
                form.longitude = lng.toString()
            },
            selectedCoordinates = form.latitude.toDoubleOrNull()?.let { lat ->
                form.longitude.toDoubleOrNull()?.let { lng -> lat to lng }
            },
            pickerCoordinates = existingCoordinates
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RouteEditorTextField(
                value = form.latitude,
                onValueChange = { form.latitude = it },
                label = "Latitud",
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardType = KeyboardType.Number
            )
            RouteEditorTextField(
                value = form.longitude,
                onValueChange = { form.longitude = it },
                label = "Longitud",
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardType = KeyboardType.Number
            )
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel("Tipo de prueba")
        trialTypeOptions.chunked(2).forEach { rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowOptions.forEach { (type, label) ->
                    SelectableChip(
                        text = label,
                        selected = form.trialType == type,
                        onClick = { form.trialType = type },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    when (form.trialType) {
        TrialType.ACERTIJO -> {
            RouteEditorTextField(
                value = form.question,
                onValueChange = { form.question = it },
                label = "Pregunta del reto",
                minLines = 2
            )
            RouteEditorTextField(
                value = form.hint,
                onValueChange = { form.hint = it },
                label = "Pista",
                singleLine = true
            )
            RouteEditorTextField(
                value = form.answer,
                onValueChange = { form.answer = it },
                label = "Respuesta correcta",
                singleLine = true
            )
            form.wrongAnswers.forEachIndexed { index, wrongAnswer ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    RouteEditorTextField(
                        value = wrongAnswer,
                        onValueChange = { form.wrongAnswers[index] = it },
                        label = "Respuesta incorrecta ${index + 1}",
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    RemoveAnswerButton(
                        enabled = form.wrongAnswers.size > 1,
                        onClick = { if (form.wrongAnswers.size > 1) form.wrongAnswers.removeAt(index) }
                    )
                }
            }
            SecondaryButton(
                text = "Añadir otra respuesta incorrecta",
                onClick = { form.wrongAnswers += "" }
            )
        }
        TrialType.PUZLE_IMAGEN -> {
            HelperText("La imagen se parte en piezas y se desordena para el visitante.")
            if (form.puzzleImageUrl.isNotBlank()) {
                RouteCoverImage(
                    url = form.puzzleImageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            RouteCoverImagePicker(
                onImageSelected = { form.puzzleImageUrl = it },
                onError = onMessage
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(3, 4).forEach { size ->
                    SelectableChip(
                        text = "$size × $size (${size * size} piezas)",
                        selected = form.puzzleGridSize == size,
                        onClick = { form.puzzleGridSize = size },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            RouteEditorTextField(
                value = form.hint,
                onValueChange = { form.hint = it },
                label = "Pista (opcional)",
                singleLine = true
            )
        }
        TrialType.ORDENAR_PALABRA -> {
            HelperText("Al visitante se le mostrarán las letras desordenadas y deberá ordenarlas.")
            RouteEditorTextField(
                value = form.word,
                onValueChange = { form.word = it },
                label = "Palabra a ordenar",
                singleLine = true
            )
            RouteEditorTextField(
                value = form.hint,
                onValueChange = { form.hint = it },
                label = "Pista (opcional)",
                singleLine = true
            )
        }
        TrialType.COMPLETAR_FRASE -> {
            HelperText("Escribe la frase completa y la palabra que el visitante debe deducir.")
            RouteEditorTextField(
                value = form.sentence,
                onValueChange = { form.sentence = it },
                label = "Frase completa",
                minLines = 2
            )
            RouteEditorTextField(
                value = form.missingWord,
                onValueChange = { form.missingWord = it },
                label = "Palabra que falta",
                singleLine = true
            )
            RouteEditorTextField(
                value = form.hint,
                onValueChange = { form.hint = it },
                label = "Pista (opcional)",
                singleLine = true
            )
        }
        else -> Unit
    }

    SecondaryButton(
        text = "Añadir parada",
        onClick = onAddStop
    )
}

@Composable
private fun NarrationModeDropdown(selected: StopAudioMode, onSelected: (StopAudioMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(SurfaceCream)
                .border(1.dp, FieldBorder, shape)
                .clickable { expanded = true }
                .padding(start = 14.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                narrationModeLabel(selected),
                modifier = Modifier.weight(1f),
                color = InkDeep,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = InkMuted)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            StopAudioMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = {
                        Text(
                            narrationModeLabel(mode),
                            color = InkDeep,
                            fontFamily = FontFamily.Serif,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onSelected(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun narrationModeLabel(mode: StopAudioMode): String = when (mode) {
    StopAudioMode.WRITTEN -> "Solo texto"
    StopAudioMode.TTS -> "Voz generada"
    StopAudioMode.RECORDED -> "Mi grabación"
}

@Composable
private fun HelperText(text: String) {
    Text(
        text,
        color = InkMuted,
        fontFamily = FontFamily.Serif,
        fontSize = 12.sp
    )
}

@Composable
private fun RemoveAnswerButton(enabled: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(Icons.Default.Delete, contentDescription = "Eliminar respuesta incorrecta", tint = TerracottaGold)
    }
}
