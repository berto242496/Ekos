package com.ecosdolugar.localization

data class AchievementStrings(
    val firstRouteTitle: String,
    val firstRouteDescription: String,
    val raiaVisitorTitle: String,
    val raiaVisitorDescription: String,
    val sharpMindTitle: String,
    val sharpMindDescription: String,
    val chroniclerTitle: String,
    val chroniclerDescription: String
)

fun achievementStrings(language: AppLanguage): AchievementStrings =
    when (language) {
        AppLanguage.SPANISH -> AchievementStrings(
            firstRouteTitle = "Primera ruta completada",
            firstRouteDescription = "Completa al menos una expedición o ruta en la app.",
            raiaVisitorTitle = "Visitante incansable de la Raia",
            raiaVisitorDescription = "Explora o completa la ruta del Contrabando en la frontera.",
            sharpMindTitle = "Mente Aguda",
            sharpMindDescription = "Resuelve con éxito tu primer enigma mitológico.",
            chroniclerTitle = "Cronista del Camino",
            chroniclerDescription = "Escribe tu primera anotación en el cuaderno de campo."
        )
        AppLanguage.GALICIAN -> AchievementStrings(
            firstRouteTitle = "Primeira ruta completada",
            firstRouteDescription = "Completa polo menos unha expedición ou ruta na app.",
            raiaVisitorTitle = "Visitante incansable da Raia",
            raiaVisitorDescription = "Explora ou completa a ruta do Contrabando na fronteira.",
            sharpMindTitle = "Mente Aguda",
            sharpMindDescription = "Resolve con éxito o teu primeiro enigma mitolóxico.",
            chroniclerTitle = "Cronista do Camiño",
            chroniclerDescription = "Escribe a túa primeira anotación no caderno de campo."
        )
        AppLanguage.ENGLISH -> AchievementStrings(
            firstRouteTitle = "First route completed",
            firstRouteDescription = "Complete at least one expedition or route in the app.",
            raiaVisitorTitle = "Tireless visitor of the Raia",
            raiaVisitorDescription = "Explore or complete the Contraband route on the border.",
            sharpMindTitle = "Sharp Mind",
            sharpMindDescription = "Successfully solve your first mythological enigma.",
            chroniclerTitle = "Chronicler of the Way",
            chroniclerDescription = "Write your first note in the field notebook."
        )
    }
