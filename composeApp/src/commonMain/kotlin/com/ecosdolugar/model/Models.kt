package com.ecosdolugar.model

enum class Category(val displayName: String) {
    ALL("Todas"),
    HISTORIA("Historia"),
    LEYENDAS("Leyendas"),
    FAMILIAS("Familias"),
    PATRIMONIO("Patrimonio"),
    NATURALEZA("Naturaleza")
}

enum class Difficulty(val label: String) {
    FACIL("Fácil"),
    MEDIA("Media"),
    EXIGENTE("Exigente")
}

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
    val audioDuration: String,
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
    val overviewFeatures: List<OverviewFeature>,
    val stops: List<RouteStop>
)

data class FieldNote(
    val id: String,
    val routeId: String,
    val routeTitle: String,
    val stopTitle: String? = null,
    val content: String,
    val timestamp: String
)

data class UserProfile(
    val name: String = "Explorador da Raia",
    val email: String = "explorador@ecosdolugar.gal",
    val isVisitor: Boolean = true,
    val characterId: String = "meiga",
    val isGoogleAuthenticated: Boolean = false,
    val googleId: String? = null,
    val savedRouteIds: Set<String> = setOf("contrabando"),
    val visitedStopIds: Set<String> = setOf("stop-c1"),
    val solvedEnigmaIds: Set<String> = setOf("stop-c1")
)
