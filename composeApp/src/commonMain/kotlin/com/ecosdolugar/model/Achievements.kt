package com.ecosdolugar.model

fun completedRoutesOf(profile: UserProfile, routes: List<RouteItem>): List<RouteItem> =
    routes.filter { route ->
        route.stops.isNotEmpty() && route.stops.all { it.id in profile.solvedEnigmaIds }
    }

// Mirrors getUserAchievements in src/data/achievementsData.ts. Field notes do not exist on Android yet.
fun userAchievementStatuses(
    profile: UserProfile,
    completedRoutes: List<RouteItem>
): List<UserAchievementStatus> {
    val isRaiaVisited = completedRoutes.any { it.id == "contrabando" } ||
        profile.visitedStopIds.any { id -> listOf("cb", "tui", "valenca").any { id.contains(it) } }
    return listOf(
        UserAchievementStatus(
            id = "primera-ruta",
            title = "Primera ruta completada",
            description = "Completa al menos una expedición o ruta en la app.",
            icon = "🏁",
            isUnlocked = completedRoutes.isNotEmpty()
        ),
        UserAchievementStatus(
            id = "visitante-raia",
            title = "Visitante incansable de la Raia",
            description = "Explora o completa la ruta del Contrabando en la frontera.",
            icon = "🏰",
            isUnlocked = isRaiaVisited
        ),
        UserAchievementStatus(
            id = "mente-aguda",
            title = "Mente Aguda",
            description = "Resuelve con éxito tu primer enigma mitológico.",
            icon = "🔮",
            isUnlocked = profile.solvedEnigmaIds.isNotEmpty()
        ),
        UserAchievementStatus(
            id = "cronista-camino",
            title = "Cronista del Camino",
            description = "Escribe tu primera anotación en el cuaderno de campo.",
            icon = "📖",
            isUnlocked = false
        )
    )
}
