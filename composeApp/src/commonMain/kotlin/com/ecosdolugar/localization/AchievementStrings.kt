package com.ecosdolugar.localization

data class AchievementStrings(
    val firstStepsTitle: String,
    val firstStepsDescription: String,
    val explorerTitle: String,
    val explorerDescription: String,
    val greatExplorerTitle: String,
    val greatExplorerDescription: String,
    val storyCollectorTitle: String,
    val storyCollectorDescription: String,
    val legendHunterTitle: String,
    val legendHunterDescription: String,
    val historyGuardianTitle: String,
    val historyGuardianDescription: String,
    val adventurousSpiritTitle: String,
    val adventurousSpiritDescription: String,
    val tirelessTravelerTitle: String,
    val tirelessTravelerDescription: String
)

fun achievementStrings(language: AppLanguage): AchievementStrings =
    when (language) {
        AppLanguage.SPANISH -> AchievementStrings(
            firstStepsTitle = "Primeros pasos",
            firstStepsDescription = "Completar la primera ruta",
            explorerTitle = "Explorador",
            explorerDescription = "Completar 3 rutas",
            greatExplorerTitle = "Gran explorador",
            greatExplorerDescription = "Completar 10 rutas",
            storyCollectorTitle = "Coleccionista de historias",
            storyCollectorDescription = "Descubrir 20 paradas",
            legendHunterTitle = "Cazaleyendas",
            legendHunterDescription = "Completar una ruta de leyendas",
            historyGuardianTitle = "Guardián de la historia",
            historyGuardianDescription = "Completar una ruta histórica",
            adventurousSpiritTitle = "Espíritu aventurero",
            adventurousSpiritDescription = "Completar una ruta de naturaleza",
            tirelessTravelerTitle = "Viajero incansable",
            tirelessTravelerDescription = "Completar rutas en 3 localidades diferentes"
        )
        AppLanguage.GALICIAN -> AchievementStrings(
            firstStepsTitle = "Primeiros pasos",
            firstStepsDescription = "Completar a primeira ruta",
            explorerTitle = "Explorador",
            explorerDescription = "Completar 3 rutas",
            greatExplorerTitle = "Gran explorador",
            greatExplorerDescription = "Completar 10 rutas",
            storyCollectorTitle = "Coleccionista de historias",
            storyCollectorDescription = "Descubrir 20 paradas",
            legendHunterTitle = "Cazalendas",
            legendHunterDescription = "Completar unha ruta de lendas",
            historyGuardianTitle = "Gardián da historia",
            historyGuardianDescription = "Completar unha ruta histórica",
            adventurousSpiritTitle = "Espírito aventureiro",
            adventurousSpiritDescription = "Completar unha ruta de natureza",
            tirelessTravelerTitle = "Viaxeiro incansable",
            tirelessTravelerDescription = "Completar rutas en 3 localidades diferentes"
        )
        AppLanguage.ENGLISH -> AchievementStrings(
            firstStepsTitle = "First steps",
            firstStepsDescription = "Complete your first route",
            explorerTitle = "Explorer",
            explorerDescription = "Complete 3 routes",
            greatExplorerTitle = "Great explorer",
            greatExplorerDescription = "Complete 10 routes",
            storyCollectorTitle = "Story collector",
            storyCollectorDescription = "Discover 20 stops",
            legendHunterTitle = "Legend hunter",
            legendHunterDescription = "Complete a legends route",
            historyGuardianTitle = "Guardian of history",
            historyGuardianDescription = "Complete a historical route",
            adventurousSpiritTitle = "Adventurous spirit",
            adventurousSpiritDescription = "Complete a nature route",
            tirelessTravelerTitle = "Tireless traveler",
            tirelessTravelerDescription = "Complete routes in 3 different locations"
        )
    }
