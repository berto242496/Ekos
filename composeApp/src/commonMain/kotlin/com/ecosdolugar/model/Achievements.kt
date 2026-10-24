package com.ecosdolugar.model

import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.achievementStrings

fun completedRoutesOf(profile: UserProfile, routes: List<RouteItem>): List<RouteItem> =
    routes.filter { route ->
        route.stops.isNotEmpty() && route.stops.all { it.id in profile.solvedEnigmaIds }
    }

// Mirrors getUserAchievements in src/data/achievementsData.ts. Field notes do not exist on Android yet.
fun userAchievementStatuses(
    profile: UserProfile,
    completedRoutes: List<RouteItem>,
    language: AppLanguage = AppLanguage.SPANISH
): List<UserAchievementStatus> {
    val strings = achievementStrings(language)
    val isRaiaVisited = completedRoutes.any { it.id == "contrabando" } ||
        profile.visitedStopIds.any { id -> listOf("cb", "tui", "valenca").any { id.contains(it) } }
    return listOf(
        UserAchievementStatus(
            id = "primera-ruta",
            title = strings.firstRouteTitle,
            description = strings.firstRouteDescription,
            icon = "🏁",
            isUnlocked = completedRoutes.isNotEmpty()
        ),
        UserAchievementStatus(
            id = "visitante-raia",
            title = strings.raiaVisitorTitle,
            description = strings.raiaVisitorDescription,
            icon = "🏰",
            isUnlocked = isRaiaVisited
        ),
        UserAchievementStatus(
            id = "mente-aguda",
            title = strings.sharpMindTitle,
            description = strings.sharpMindDescription,
            icon = "🔮",
            isUnlocked = profile.solvedEnigmaIds.isNotEmpty()
        ),
        UserAchievementStatus(
            id = "cronista-camino",
            title = strings.chroniclerTitle,
            description = strings.chroniclerDescription,
            icon = "📖",
            isUnlocked = false
        )
    )
}
