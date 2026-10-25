package com.ecosdolugar.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.StopEditorStrings
import com.ecosdolugar.localization.stopEditorStrings
import com.ecosdolugar.model.NarrationVoice
import com.ecosdolugar.model.StopAudioMode
import com.ecosdolugar.model.TrialType
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.components.RouteCoverImagePicker
import com.ecosdolugar.ui.components.RouteMapView
import com.ecosdolugar.ui.components.rememberAudioClipPlayer
import com.ecosdolugar.ui.components.rememberVoicePreviewer
import com.ecosdolugar.ui.components.rememberVoiceRecorder
import com.ecosdolugar.ui.theme.MossContainer
import com.ecosdolugar.ui.theme.PineContainer
import com.ecosdolugar.ui.theme.SurfaceCream
import com.ecosdolugar.ui.theme.TerracottaGold

@Composable
internal fun StopEditorForm(
    form: StopFormState,
    existingCoordinates: List<Pair<Double, Double>>,
    language: AppLanguage,
    onMessage: (String?) -> Unit,
    onAddStop: () -> Unit,
    addLabel: String
) {
    val strings = stopEditorStrings(language)
    val trialTypeOptions = listOf(
        TrialType.ACERTIJO to strings.trialTypeQuiz,
        TrialType.PUZLE_IMAGEN to strings.trialTypePuzzle,
        TrialType.ORDENAR_PALABRA to strings.trialTypeWord,
        TrialType.COMPLETAR_FRASE to strings.trialTypePhrase
    )
    val previewer = rememberVoicePreviewer()
    val clipPlayer = rememberAudioClipPlayer()
    val recorder = rememberVoiceRecorder(
        onRecorded = { form.recordedAudioUrl = it; onMessage(null) },
        onError = onMessage
    )

    FieldLabel(strings.stopDataSection)
    RouteEditorTextField(
        value = form.title,
        onValueChange = { form.title = it },
        label = strings.stopNameLabel,
        singleLine = true
    )
    RouteEditorTextField(
        value = form.location,
        onValueChange = { form.location = it },
        label = strings.placeLabel,
        singleLine = true
    )
    RouteEditorTextField(
        value = form.story,
        onValueChange = { form.story = it },
        label = strings.storyLabel,
        minLines = 2
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel(strings.stopImageSection)
        RouteEditorTextField(
            value = form.imageUrl.takeUnless { it.startsWith("data:") }.orEmpty(),
            onValueChange = { form.imageUrl = it },
            label = strings.imageUrlLabel,
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
        FieldLabel(strings.narrationSection)
        StyledDropdown(
            selected = form.audioMode,
            options = StopAudioMode.entries.map { it to narrationModeLabel(it, strings) },
            onSelected = { form.audioMode = it }
        )
        when (form.audioMode) {
            StopAudioMode.WRITTEN -> HelperText(strings.narrationWrittenHelper)
            StopAudioMode.TTS -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SelectableChip(
                        text = strings.femaleVoice,
                        selected = form.narrationVoice == NarrationVoice.FEMALE,
                        onClick = { form.narrationVoice = NarrationVoice.FEMALE },
                        modifier = Modifier.weight(1f)
                    )
                    SelectableChip(
                        text = strings.maleVoice,
                        selected = form.narrationVoice == NarrationVoice.MALE,
                        onClick = { form.narrationVoice = NarrationVoice.MALE },
                        modifier = Modifier.weight(1f)
                    )
                }
                HelperText(strings.ttsHelper)
                SecondaryButton(
                    text = if (previewer.isSpeaking) strings.stopExample else strings.listenExample,
                    onClick = {
                        when {
                            previewer.isSpeaking -> previewer.stop()
                            form.story.isBlank() -> onMessage(strings.writeStoryForExample)
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
                        strings.recordingInProgress
                    } else if (form.recordedAudioUrl != null) {
                        strings.recordingSaved
                    } else {
                        strings.recordHelper
                    }
                )
                SecondaryButton(
                    text = when {
                        recorder.isRecording -> strings.stopRecording
                        form.recordedAudioUrl != null -> strings.recordAgain
                        else -> strings.recordStory
                    },
                    onClick = {
                        onMessage(null)
                        clipPlayer.stop()
                        recorder.toggle()
                    }
                )
                if (form.recordedAudioUrl != null && !recorder.isRecording) {
                    SecondaryButton(
                        text = if (clipPlayer.isPlaying) strings.stopPlayback else strings.listenRecording,
                        onClick = {
                            if (clipPlayer.isPlaying) {
                                clipPlayer.stop()
                            } else {
                                form.recordedAudioUrl?.let { clipPlayer.play(it) }
                            }
                        }
                    )
                    SecondaryButton(
                        text = strings.deleteRecording,
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
        FieldLabel(strings.locationSection)
        HelperText(strings.locationHelper)
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
                label = strings.latitudeLabel,
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardType = KeyboardType.Number
            )
            RouteEditorTextField(
                value = form.longitude,
                onValueChange = { form.longitude = it },
                label = strings.longitudeLabel,
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardType = KeyboardType.Number
            )
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel(strings.trialTypeSection)
        StyledDropdown(
            selected = form.trialType,
            options = trialTypeOptions,
            onSelected = { form.trialType = it }
        )
    }

    when (form.trialType) {
        TrialType.ACERTIJO -> {
            RouteEditorTextField(
                value = form.question,
                onValueChange = { form.question = it },
                label = strings.questionLabel,
                minLines = 2
            )
            RouteEditorTextField(
                value = form.hint,
                onValueChange = { form.hint = it },
                label = strings.hintLabel,
                singleLine = true
            )
            RouteEditorTextField(
                value = form.answer,
                onValueChange = { form.answer = it },
                label = strings.correctAnswerLabel,
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
                        label = strings.wrongAnswerLabel(index + 1),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    RemoveAnswerButton(
                        enabled = form.wrongAnswers.size > 1,
                        description = strings.removeWrongAnswer,
                        onClick = { if (form.wrongAnswers.size > 1) form.wrongAnswers.removeAt(index) }
                    )
                }
            }
            SecondaryButton(
                text = strings.addWrongAnswer,
                onClick = { form.wrongAnswers += "" }
            )
        }
        TrialType.PUZLE_IMAGEN -> {
            HelperText(strings.puzzleHelper)
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
                        text = strings.puzzleGridLabel(size),
                        selected = form.puzzleGridSize == size,
                        onClick = { form.puzzleGridSize = size },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        TrialType.ORDENAR_PALABRA -> {
            HelperText(strings.wordHelper)
            RouteEditorTextField(
                value = form.word,
                onValueChange = { form.word = it },
                label = strings.wordLabel,
                singleLine = true
            )
            RouteEditorTextField(
                value = form.hint,
                onValueChange = { form.hint = it },
                label = strings.optionalHintLabel,
                singleLine = true
            )
        }
        TrialType.COMPLETAR_FRASE -> {
            HelperText(strings.phraseHelper)
            RouteEditorTextField(
                value = form.sentence,
                onValueChange = { form.sentence = it },
                label = strings.fullSentenceLabel,
                minLines = 2
            )
            RouteEditorTextField(
                value = form.missingWord,
                onValueChange = { form.missingWord = it },
                label = strings.missingWordLabel,
                singleLine = true
            )
            RouteEditorTextField(
                value = form.hint,
                onValueChange = { form.hint = it },
                label = strings.optionalHintLabel,
                singleLine = true
            )
        }
        else -> Unit
    }

    SecondaryButton(
        text = addLabel,
        onClick = onAddStop
    )
}

@Composable
internal fun <T> StyledDropdown(selected: T, options: List<Pair<T, String>>, onSelected: (T) -> Unit) {
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
                options.firstOrNull { it.first == selected }?.second.orEmpty(),
                modifier = Modifier.weight(1f),
                color = InkDeep,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = InkMuted)
        }
        if (expanded) {
            Popup(
                alignment = Alignment.BottomStart,
                offset = IntOffset(0, 0),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true)
            ) {
                Surface(
                    shape = shape,
                    color = SurfaceCream,
                    border = BorderStroke(1.dp, FieldBorder),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        options.forEach { (value, label) ->
                            val isSelected = value == selected
                            Text(
                                label,
                                color = if (isSelected) PineContainer else InkDeep,
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelected(value)
                                        expanded = false
                                    }
                                    .background(if (isSelected) MossContainer.copy(alpha = 0.45f) else SurfaceCream)
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun narrationModeLabel(mode: StopAudioMode, strings: StopEditorStrings): String = when (mode) {
    StopAudioMode.WRITTEN -> strings.narrationWrittenLabel
    StopAudioMode.TTS -> strings.narrationTtsLabel
    StopAudioMode.RECORDED -> strings.narrationRecordedLabel
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
private fun RemoveAnswerButton(enabled: Boolean, description: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(Icons.Default.Delete, contentDescription = description, tint = TerracottaGold)
    }
}
