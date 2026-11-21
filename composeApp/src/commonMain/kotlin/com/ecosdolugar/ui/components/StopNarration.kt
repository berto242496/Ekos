package com.ecosdolugar.ui.components

import androidx.compose.runtime.Composable
import com.ecosdolugar.model.NarrationVoice

interface VoicePreviewer {
    val isSpeaking: Boolean
    fun speak(text: String, voice: NarrationVoice)
    fun stop()
}

@Composable
expect fun rememberVoicePreviewer(): VoicePreviewer

interface VoiceRecorderController {
    val isRecording: Boolean
    fun toggle()
}

/** Devuelve la grabación como data URL (audio/mp4) mediante [onRecorded]. */
@Composable
expect fun rememberVoiceRecorder(
    onRecorded: (String) -> Unit,
    onError: (String) -> Unit
): VoiceRecorderController
