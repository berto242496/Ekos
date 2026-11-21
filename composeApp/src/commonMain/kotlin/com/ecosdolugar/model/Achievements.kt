package com.ecosdolugar.model

import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.achievementStrings

fun completedRoutesOf(profile: UserProfile, routes: List<RouteItem>): List<RouteItem> =
    routes.filter { route ->
        route.stops.isNotEmpty() && route.stops.all { it.id in profile.solvedEnigmaIds }
    }

fun userAchievementStatuses(
    profile: UserProfile,
    completedRoutes: List<RouteItem>,
    language: AppLanguage = AppLanguage.SPANISH
): List<UserAchievementStatus> {
    val strings = achievementStrings(language)
    val completedCount = completedRoutes.size
    val discoveredStopsCount = (profile.visitedStopIds + profile.solvedEnigmaIds).size
    val completedLocationsCount = completedRoutes.map { it.location.trim().lowercase() }.distinct().size
    fun hasCompletedCategory(category: Category) = completedRoutes.any { category in it.categories }
    return listOf(
        UserAchievementStatus(
            id = "primeros-pasos",
            title = strings.firstStepsTitle,
            description = strings.firstStepsDescription,
            icon = "🥾",
            isUnlocked = completedCount >= 1
        ),
        UserAchievementStatus(
            id = "explorador",
            title = strings.explorerTitle,
            description = strings.explorerDescription,
            icon = "🧭",
            isUnlocked = completedCount >= 3
        ),
        UserAchievementStatus(
            id = "gran-explorador",
            title = strings.greatExplorerTitle,
            description = strings.greatExplorerDescription,
            icon = "🏆",
            isUnlocked = completedCount >= 10
        ),
        UserAchievementStatus(
            id = "coleccionista-historias",
            title = strings.storyCollectorTitle,
            description = strings.storyCollectorDescription,
            icon = "📖",
            isUnlocked = discoveredStopsCount >= 20
        ),
        UserAchievementStatus(
            id = "cazaleyendas",
            title = strings.legendHunterTitle,
            description = strings.legendHunterDescription,
            icon = "✨",
            isUnlocked = hasCompletedCategory(Category.LEYENDAS)
        ),
        UserAchievementStatus(
            id = "guardian-historia",
            title = strings.historyGuardianTitle,
            description = strings.historyGuardianDescription,
            icon = "🏛️",
            isUnlocked = hasCompletedCategory(Category.HISTORIA)
        ),
        UserAchievementStatus(
            id = "espiritu-aventurero",
            title = strings.adventurousSpiritTitle,
            description = strings.adventurousSpiritDescription,
            icon = "🌿",
            isUnlocked = hasCompletedCategory(Category.NATURALEZA)
        ),
        UserAchievementStatus(
            id = "viajero-incansable",
            title = strings.tirelessTravelerTitle,
            description = strings.tirelessTravelerDescription,
            icon = "🌍",
            isUnlocked = completedLocationsCount >= 3
        )
    )
}
