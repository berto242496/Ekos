package com.ecosdolugar.ui.components

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.ecosdolugar.model.NarrationVoice
import java.io.File
import java.util.Locale
import java.util.UUID

@Composable
actual fun rememberVoicePreviewer(): VoicePreviewer {
    val context = LocalContext.current.applicationContext
    val previewer = remember { AndroidVoicePreviewer(context) }
    DisposableEffect(previewer) {
        onDispose { previewer.release() }
    }
    return previewer
}

private class AndroidVoicePreviewer(context: Context) : VoicePreviewer {
    private val speaking = mutableStateOf(false)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var ready = false
    private var pending: (() -> Unit)? = null
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            ready = status == TextToSpeech.SUCCESS
            if (ready) pending?.invoke()
            pending = null
        }
    }

    override val isSpeaking: Boolean get() = speaking.value

    override fun speak(text: String, voice: NarrationVoice) {
        val engine = tts ?: return
        if (!ready) {
            pending = { speak(text, voice) }
            return
        }
        engine.stop()
        engine.language = Locale("es", "ES")
        engine.setSpeechRate(0.95f)
        // Como en la web, el género se marca con el tono; si el motor tiene una voz de ese género, se usa.
        engine.setPitch(if (voice == NarrationVoice.FEMALE) 1.15f else 0.8f)
        pickVoice(engine, voice)?.let { engine.voice = it }
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) = finishOnMain()
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) = finishOnMain()
        })
        speaking.value = true
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "preview-${UUID.randomUUID()}")
    }

    override fun stop() {
        tts?.stop()
        speaking.value = false
    }

    fun release() {
        pending = null
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    private fun finishOnMain() {
        mainHandler.post { speaking.value = false }
    }

    private fun pickVoice(engine: TextToSpeech, voice: NarrationVoice): android.speech.tts.Voice? {
        val keyword = if (voice == NarrationVoice.FEMALE) "female" else "male"
        return engine.voices.orEmpty()
            .filter { it.locale.language == "es" && !it.isNetworkConnectionRequired }
            .firstOrNull { candidate ->
                val name = candidate.name.lowercase()
                if (keyword == "male") name.contains("male") && !name.contains("female")
                else name.contains(keyword)
            }
    }
}

@Composable
actual fun rememberVoiceRecorder(
    onRecorded: (String) -> Unit,
    onError: (String) -> Unit
): VoiceRecorderController {
    val context = LocalContext.current.applicationContext
    val currentOnRecorded by rememberUpdatedState(onRecorded)
    val currentOnError by rememberUpdatedState(onError)
    val controller = remember {
        AndroidVoiceRecorder(context, { currentOnRecorded(it) }, { currentOnError(it) })
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            controller.start()
        } else {
            currentOnError("Necesitas permitir el micrófono para grabar el relato.")
        }
    }
    controller.requestPermission = { permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO) }
    DisposableEffect(controller) {
        onDispose { controller.cancel() }
    }
    return controller
}

private class AndroidVoiceRecorder(
    private val context: Context,
    private val onRecorded: (String) -> Unit,
    private val onError: (String) -> Unit
) : VoiceRecorderController {
    private val recording = mutableStateOf(false)
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    var requestPermission: () -> Unit = {}

    override val isRecording: Boolean get() = recording.value

    override fun toggle() {
        if (recording.value) stop() else requestPermission()
    }

    fun start() {
        if (recording.value) return
        val file = File(context.cacheDir, "narration-${UUID.randomUUID()}.m4a")
        try {
            val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder.setAudioEncodingBitRate(96_000)
            mediaRecorder.setAudioSamplingRate(44_100)
            mediaRecorder.setOutputFile(file.absolutePath)
            mediaRecorder.prepare()
            mediaRecorder.start()
            recorder = mediaRecorder
            outputFile = file
            recording.value = true
        } catch (error: Exception) {
            file.delete()
            onError("No se pudo iniciar la grabación: ${error.message ?: "error desconocido"}")
        }
    }

    fun stop() {
        val mediaRecorder = recorder ?: return
        val file = outputFile
        recorder = null
        outputFile = null
        recording.value = false
        try {
            mediaRecorder.stop()
        } catch (error: RuntimeException) {
            onError("La grabación es demasiado corta. Vuelve a intentarlo.")
            file?.delete()
            mediaRecorder.release()
            return
        }
        mediaRecorder.release()
        if (file == null || !file.exists()) {
            onError("No se pudo guardar la grabación.")
            return
        }
        val bytes = file.readBytes()
        file.delete()
        if (bytes.size > 50 * 1024 * 1024) {
            onError("La grabación supera el límite de 50 MB.")
            return
        }
        onRecorded("data:audio/mp4;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP))
    }

    fun cancel() {
        if (recorder != null) {
            runCatching { recorder?.stop() }
            recorder?.release()
            recorder = null
            outputFile?.delete()
            outputFile = null
            recording.value = false
        }
    }
}
