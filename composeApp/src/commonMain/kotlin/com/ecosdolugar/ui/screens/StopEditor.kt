package com.ecosdolugar.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ecosdolugar.localization.StopEditorStrings
import com.ecosdolugar.model.FieldEnigma
import com.ecosdolugar.model.NarrationVoice
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.model.StopAudioMode
import com.ecosdolugar.model.StopTrial
import com.ecosdolugar.model.TrialType

internal data class StopDraft(
    val title: String,
    val location: String,
    val story: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val audioMode: StopAudioMode,
    val narrationVoice: NarrationVoice,
    val recordedAudioUrl: String?,
    val trialType: TrialType,
    val question: String,
    val hint: String,
    val answer: String,
    val wrongAnswers: List<String>,
    val puzzleImageUrl: String,
    val puzzleGridSize: Int,
    val word: String,
    val sentence: String,
    val missingWord: String,
    val source: RouteStop? = null
) {
    companion object {
        fun fromRouteStop(stop: RouteStop, strings: StopEditorStrings): StopDraft {
            val trial = stop.trial
            return StopDraft(
                title = stop.title,
                location = stop.locationName,
                story = stop.story,
                latitude = stop.latitude,
                longitude = stop.longitude,
                imageUrl = stop.imageUrl,
                audioMode = stop.audioMode,
                narrationVoice = stop.narrationVoice,
                recordedAudioUrl = stop.recordedAudioUrl,
                trialType = trial?.type ?: TrialType.ACERTIJO,
                question = trial?.question?.takeIf { it.isNotBlank() } ?: stop.enigma.question,
                hint = trial?.generalHint?.takeIf { it.isNotBlank() } ?: stop.enigma.hint,
                answer = trial?.options?.getOrNull(trial.correctOptionIndex) ?: stop.enigma.answer,
                wrongAnswers = trial?.options
                    ?.filterIndexed { index, _ -> index != trial.correctOptionIndex }
                    ?.takeIf { it.isNotEmpty() }
                    ?: listOf(strings.defaultWrongAnswer),
                puzzleImageUrl = trial?.imageUrl.orEmpty(),
                puzzleGridSize = trial?.puzzleGridSize ?: 3,
                word = trial?.cipherWord.orEmpty(),
                sentence = trial?.sentence.orEmpty(),
                missingWord = trial?.missingWord.orEmpty(),
                source = stop
            )
        }
    }
}

/** Formulario de una nueva parada. Se reinicia tras añadirla. */
internal class StopFormState {
    var title by mutableStateOf("")
    var location by mutableStateOf("")
    var story by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var audioMode by mutableStateOf(StopAudioMode.WRITTEN)
    var narrationVoice by mutableStateOf(NarrationVoice.FEMALE)
    var recordedAudioUrl by mutableStateOf<String?>(null)
    var latitude by mutableStateOf("")
    var longitude by mutableStateOf("")
    var trialType by mutableStateOf(TrialType.ACERTIJO)
    var question by mutableStateOf("")
    var hint by mutableStateOf("")
    var answer by mutableStateOf("")
    val wrongAnswers = mutableStateListOf("")
    var puzzleImageUrl by mutableStateOf("")
    var puzzleGridSize by mutableStateOf(3)
    var word by mutableStateOf("")
    var sentence by mutableStateOf("")
    var missingWord by mutableStateOf("")

    fun validate(strings: StopEditorStrings): String? {
        val lat = latitude.toDoubleOrNull()
        val lng = longitude.toDoubleOrNull()
        val common = when {
            title.isBlank() -> strings.stopNameRequired
            location.isBlank() -> strings.stopLocationRequired
            lat == null || lat !in -90.0..90.0 -> strings.latitudeRange
            lng == null || lng !in -180.0..180.0 -> strings.longitudeRange
            audioMode == StopAudioMode.TTS && story.isBlank() -> strings.storyRequiredForTts
            audioMode == StopAudioMode.RECORDED && recordedAudioUrl == null -> strings.recordingRequired
            else -> null
        }
        if (common != null) return common
        return when (trialType) {
            TrialType.ACERTIJO -> when {
                question.isBlank() -> strings.riddleRequired
                answer.isBlank() || wrongAnswers.any { it.isBlank() } -> strings.answersIncomplete
                (listOf(answer) + wrongAnswers).map { it.trim() }.distinct().size != wrongAnswers.size + 1 ->
                    strings.answersMustDiffer
                else -> null
            }
            TrialType.PUZLE_IMAGEN -> when {
                puzzleImageUrl.isBlank() -> strings.puzzleImageRequired
                else -> null
            }
            TrialType.ORDENAR_PALABRA -> {
                val cleanWord = word.trim()
                when {
                    cleanWord.length !in 4..12 || !cleanWord.all { it.isLetter() } -> strings.wordLengthInvalid
                    cleanWord.toSet().size < 2 -> strings.wordNeedsTwoLetters
                    else -> null
                }
            }
            TrialType.COMPLETAR_FRASE -> {
                val cleanSentence = sentence.trim()
                val cleanWord = missingWord.trim()
                when {
                    cleanSentence.isBlank() -> strings.sentenceRequired
                    cleanWord.isBlank() -> strings.missingWordRequired
                    !Regex("(?iu)(?<!\\p{L})${Regex.escape(cleanWord)}(?!\\p{L})").containsMatchIn(cleanSentence) ->
                        strings.missingWordNotInSentence
                    else -> null
                }
            }
            else -> null
        }
    }

    fun toDraft(): StopDraft = StopDraft(
        title = title.trim(),
        location = location.trim(),
        story = story.trim(),
        latitude = latitude.toDouble(),
        longitude = longitude.toDouble(),
        imageUrl = imageUrl.trim(),
        audioMode = audioMode,
        narrationVoice = narrationVoice,
        recordedAudioUrl = recordedAudioUrl,
        trialType = trialType,
        question = question.trim(),
        hint = hint.trim(),
        answer = answer.trim(),
        wrongAnswers = wrongAnswers.map(String::trim),
        puzzleImageUrl = puzzleImageUrl.trim(),
        puzzleGridSize = puzzleGridSize,
        word = word.trim(),
        sentence = sentence.trim(),
        missingWord = missingWord.trim()
    )

    fun loadFrom(draft: StopDraft) {
        title = draft.title
        location = draft.location
        story = draft.story
        imageUrl = draft.imageUrl
        audioMode = draft.audioMode
        narrationVoice = draft.narrationVoice
        recordedAudioUrl = draft.recordedAudioUrl
        latitude = draft.latitude.toString()
        longitude = draft.longitude.toString()
        trialType = draft.trialType
        question = draft.question
        hint = draft.hint
        answer = draft.answer
        wrongAnswers.clear()
        wrongAnswers += draft.wrongAnswers.ifEmpty { listOf("") }
        puzzleImageUrl = draft.puzzleImageUrl
        puzzleGridSize = draft.puzzleGridSize
        word = draft.word
        sentence = draft.sentence
        missingWord = draft.missingWord
    }

    fun reset() {
        title = ""
        location = ""
        story = ""
        imageUrl = ""
        audioMode = StopAudioMode.WRITTEN
        narrationVoice = NarrationVoice.FEMALE
        recordedAudioUrl = null
        latitude = ""
        longitude = ""
        trialType = TrialType.ACERTIJO
        question = ""
        hint = ""
        answer = ""
        wrongAnswers.clear()
        wrongAnswers += ""
        puzzleImageUrl = ""
        puzzleGridSize = 3
        word = ""
        sentence = ""
        missingWord = ""
    }
}

/** Reordena las letras garantizando que no quede igual a la palabra original. */
internal fun scrambleLetters(word: String): List<String> {
    val letters = word.map(Char::toString)
    repeat(30) {
        val shuffled = letters.shuffled()
        if (shuffled.joinToString("") != word) return shuffled
    }
    return letters.reversed()
}

internal fun StopDraft.toRouteStop(index: Int, routeId: String, strings: StopEditorStrings): RouteStop {
    val mapX = (20 + index * 15).coerceAtMost(85).toFloat()
    val mapY = (30 + index * 12).coerceAtMost(85).toFloat()
    val existing = source
    if (existing != null) {
        // Las paradas existentes conservan su prueba original (también tipos no editables aquí).
        return existing.copy(order = index + 1, mapPositionX = mapX, mapPositionY = mapY)
    }
    val trial = when (trialType) {
        TrialType.ACERTIJO -> StopTrial(
            type = TrialType.ACERTIJO,
            title = strings.enigmaTitle(title),
            loreContext = story,
            question = question,
            options = listOf(answer) + wrongAnswers,
            correctOptionIndex = 0,
            generalHint = hint,
            solutionExplanation = answer,
            magicalReward = strings.magicalReward
        )
        TrialType.PUZLE_IMAGEN -> StopTrial(
            type = TrialType.PUZLE_IMAGEN,
            title = strings.puzzleTitle(title),
            loreContext = story,
            question = strings.puzzleQuestion,
            imageUrl = puzzleImageUrl,
            puzzleGridSize = puzzleGridSize,
            generalHint = hint,
            solutionExplanation = strings.puzzleSolutionNote,
            magicalReward = strings.magicalReward
        )
        TrialType.ORDENAR_PALABRA -> StopTrial(
            type = TrialType.ORDENAR_PALABRA,
            title = strings.wordTitle(title),
            loreContext = story,
            question = strings.wordQuestion,
            cipherWord = word,
            scrambledLetters = scrambleLetters(word),
            cipherClue = hint,
            generalHint = hint,
            solutionExplanation = word,
            magicalReward = strings.magicalReward
        )
        TrialType.COMPLETAR_FRASE -> StopTrial(
            type = TrialType.COMPLETAR_FRASE,
            title = strings.phraseTitle(title),
            loreContext = story,
            question = strings.phraseQuestion,
            sentence = sentence,
            missingWord = missingWord,
            generalHint = hint,
            solutionExplanation = missingWord,
            magicalReward = strings.magicalReward
        )
        else -> error("Tipo de prueba no editable: $trialType")
    }
    val answerText = when (trialType) {
        TrialType.ORDENAR_PALABRA -> word
        TrialType.COMPLETAR_FRASE -> missingWord
        else -> answer
    }
    return RouteStop(
        id = "$routeId-stop-${index + 1}",
        order = index + 1,
        title = title,
        subtitle = location,
        locationName = location,
        story = story,
        historicalNote = "",
        enigma = FieldEnigma(
            question = if (trialType == TrialType.ACERTIJO) question else trial.question,
            hint = hint,
            answer = answerText
        ),
        trial = trial,
        audioDuration = strings.readingDuration,
        audioNarration = story.takeIf { it.isNotBlank() },
        latitude = latitude,
        longitude = longitude,
        mapPositionX = mapX,
        mapPositionY = mapY,
        imageUrl = imageUrl,
        audioMode = audioMode,
        narrationVoice = narrationVoice,
        recordedAudioUrl = recordedAudioUrl
    )
}
