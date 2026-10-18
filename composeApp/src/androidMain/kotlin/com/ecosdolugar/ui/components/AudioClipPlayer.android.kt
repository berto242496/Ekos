package com.ecosdolugar.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID

@Composable
actual fun rememberAudioClipPlayer(): AudioClipPlayer {
    val context = LocalContext.current.applicationContext
    val player = remember { AndroidAudioClipPlayer(context) }
    DisposableEffect(player) {
        onDispose { player.stop() }
    }
    return player
}

private class AndroidAudioClipPlayer(private val context: Context) : AudioClipPlayer {
    private val playing = mutableStateOf(false)
    private var mediaPlayer: MediaPlayer? = null
    private var tempFile: File? = null

    override val isPlaying: Boolean get() = playing.value

    override fun play(dataUrl: String) {
        stop()
        val base64 = dataUrl.substringAfter("base64,", "")
        if (base64.isEmpty()) return
        val file = File(context.cacheDir, "playback-${UUID.randomUUID()}.m4a")
        file.writeBytes(Base64.decode(base64, Base64.DEFAULT))
        tempFile = file
        val player = MediaPlayer()
        try {
            player.setDataSource(file.absolutePath)
            player.setOnCompletionListener { stop() }
            player.prepare()
            player.start()
            mediaPlayer = player
            playing.value = true
        } catch (error: Exception) {
            player.release()
            stop()
        }
    }

    override fun stop() {
        mediaPlayer?.let { player ->
            runCatching { if (player.isPlaying) player.stop() }
            player.release()
        }
        mediaPlayer = null
        tempFile?.delete()
        tempFile = null
        playing.value = false
    }
}
