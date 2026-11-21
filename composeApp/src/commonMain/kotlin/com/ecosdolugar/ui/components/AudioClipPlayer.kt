package com.ecosdolugar.ui.components

import androidx.compose.runtime.Composable

interface AudioClipPlayer {
    val isPlaying: Boolean

    /** Reproduce un clip codificado como data URL (data:audio/...;base64,...). */
    fun play(dataUrl: String)
    fun stop()
}

@Composable
expect fun rememberAudioClipPlayer(): AudioClipPlayer
