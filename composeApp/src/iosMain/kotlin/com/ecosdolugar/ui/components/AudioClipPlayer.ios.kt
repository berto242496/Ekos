package com.ecosdolugar.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private object UnsupportedAudioClipPlayer : AudioClipPlayer {
    override val isPlaying: Boolean = false
    override fun play(dataUrl: String) = Unit
    override fun stop() = Unit
}

@Composable
actual fun rememberAudioClipPlayer(): AudioClipPlayer = remember { UnsupportedAudioClipPlayer }
