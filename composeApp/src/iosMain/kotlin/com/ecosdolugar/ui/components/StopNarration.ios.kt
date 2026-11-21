package com.ecosdolugar.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.ecosdolugar.model.NarrationVoice

private object UnsupportedVoicePreviewer : VoicePreviewer {
    override val isSpeaking: Boolean = false
    override fun speak(text: String, voice: NarrationVoice) = Unit
    override fun stop() = Unit
}

@Composable
actual fun rememberVoicePreviewer(): VoicePreviewer = remember { UnsupportedVoicePreviewer }

private class UnsupportedVoiceRecorder(private val onError: (String) -> Unit) : VoiceRecorderController {
    override val isRecording: Boolean = false
    override fun toggle() = onError("La grabación de voz solo está disponible en Android.")
}

@Composable
actual fun rememberVoiceRecorder(
    onRecorded: (String) -> Unit,
    onError: (String) -> Unit
): VoiceRecorderController {
    val currentOnError by rememberUpdatedState(onError)
    return remember { UnsupportedVoiceRecorder { currentOnError(it) } }
}
