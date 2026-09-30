package com.ecosdolugar.audio

interface AudioPlayerService {
    fun speak(text: String, rate: Float = 1.0f)
    fun stop()
    fun isSpeaking(): Boolean
}

class CommonAudioPlayerService : AudioPlayerService {
    private var speaking: Boolean = false

    override fun speak(text: String, rate: Float) {
        speaking = true
        // Multiplatform audio speech hook
    }

    override fun stop() {
        speaking = false
    }

    override fun isSpeaking(): Boolean = speaking
}
