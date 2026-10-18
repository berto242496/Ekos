package com.ecosdolugar.model

enum class Category(val displayName: String) {
    ALL("Todas"),
    HISTORIA("Historia"),
    LEYENDAS("Leyendas"),
    FAMILIAS("Familias"),
    PATRIMONIO("Patrimonio"),
    NATURALEZA("Naturaleza")
}

enum class Difficulty(val label: String, val detail: String) {
    FACIL("Fácil", "Paseo accesible"),
    MEDIA("Media", "Camino tradicional"),
    EXIGENTE("Exigente", "Costa o desnivel")
}

enum class TrialType(val label: String) {
    ACERTIJO("Acertijo Clásico"),
    PUZLE_RUNICO("Puzle Rúnico"),
    CIFRADO_MAGICO("Cifrado Mágico"),
    ALINEACION_SIMBOLOS("Alineación de Símbolos"),
    PUZLE_IMAGEN("Puzle de imagen"),
    ORDENAR_PALABRA("Ordenar palabra"),
    COMPLETAR_FRASE("Completar frase")
}

enum class StopAudioMode { WRITTEN, TTS, RECORDED }

enum class NarrationVoice { FEMALE, MALE }

data class RuneGlyph(
    val id: String,
    val glyph: String,
    val name: String,
    val desc: String
)

data class StopTrial(
    val type: TrialType = TrialType.ACERTIJO,
    val title: String,
    val loreContext: String,
    val question: String,
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val runes: List<RuneGlyph> = emptyList(),
    val correctRuneOrder: List<String> = emptyList(),
    val cipherWord: String = "",
    val scrambledLetters: List<String> = emptyList(),
    val cipherClue: String = "",
    // For 'puzle_imagen': imagen completa que se trocea y se desordena
    val imageUrl: String = "",
    val puzzleGridSize: Int = 3,
    // For 'completar_frase': frase completa y palabra oculta en ella
    val sentence: String = "",
    val missingWord: String = "",
    val generalHint: String,
    val characterClues: Map<String, String> = emptyMap(),
    val solutionExplanation: String,
    val magicalReward: String
)

data class FieldEnigma(
    val question: String,
    val hint: String,
    val answer: String
)

data class RouteStop(
    val id: String,
    val order: Int,
    val title: String,
    val subtitle: String,
    val locationName: String,
    val story: String,
    val historicalNote: String,
    val enigma: FieldEnigma,
    val trial: StopTrial? = null,
    val challengeId: String? = null,
    val audioDuration: String,
    val audioNarration: String? = null,
    val imageUrl: String = "",
    val audioMode: StopAudioMode = StopAudioMode.WRITTEN,
    val narrationVoice: NarrationVoice = NarrationVoice.FEMALE,
    val recordedAudioUrl: String? = null,
    val latitude: Double,
    val longitude: Double,
    val mapPositionX: Float, // 0f to 100f
    val mapPositionY: Float  // 0f to 100f
)

data class OverviewFeature(
    val iconName: String,
    val title: String,
    val description: String,
    val accentTag: String
)

data class GuideAuthor(
    val id: String,
    val name: String,
    val title: String,
    val bio: String,
    val rating: Double = 4.9,
    val reviewsCount: Int = 42,
    val location: String = "Tui - Valença",
    val verified: Boolean = true,
    val routesCount: Int = 3,
    val availableBalance: Double = 142.50
)

data class RouteItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val location: String,
    val region: String,
    val imageUrl: String,
    val isFeatured: Boolean = false,
    val stopsCount: Int,
    val duration: String,
    val distance: String,
    val difficulty: Difficulty,
    val categories: List<Category>,
    val audioTag: String,
    val tomoRef: String,
    val price: String = "Gratis",
    val isFree: Boolean = true,
    val guide: GuideAuthor = GuideAuthor(
        id = "guia_oficial",
        name = "Arquivo Histórico da Raia",
        title = "Investigadores Territoriais do Miño",
        bio = "Especialistas na memoria oral, arquivos transfronteirizos e camiños clandestinos."
    ),
    val overviewFeatures: List<OverviewFeature>,
    val stops: List<RouteStop>,
    val status: String = "publicada"
)

data class FieldNote(
    val id: String,
    val routeId: String,
    val routeTitle: String,
    val stopTitle: String? = null,
    val content: String,
    val timestamp: String
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String
)

data class UserProfile(
    val name: String = "Explorador da Raia",
    val email: String = "explorador@ecosdolugar.gal",
    val isVisitor: Boolean = true,
    val characterId: String = "meiga",
    val isGoogleAuthenticated: Boolean = false,
    val isGuideAccount: Boolean = false,
    val googleId: String? = null,
    val savedRouteIds: Set<String> = emptySet(),
    val visitedStopIds: Set<String> = setOf("stop-c1"),
    val solvedEnigmaIds: Set<String> = setOf("stop-c1"),
    val completedRouteIds: Set<String> = emptySet(),
    val unlockedAchievementIds: Set<String> = setOf("primera_ruta", "espiritu_raiano")
)
