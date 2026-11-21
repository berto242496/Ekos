package com.ecosdolugar.localization

data class AppShellStrings(
    val routesTab: String,
    val mapTab: String,
    val journalTab: String,
    val guideTitle: String
)

fun appShellStrings(language: AppLanguage): AppShellStrings =
    when (language) {
        AppLanguage.SPANISH -> AppShellStrings(
            routesTab = "Rutas",
            mapTab = "Mapa",
            journalTab = "Mi aventura",
            guideTitle = "Guía"
        )
        AppLanguage.GALICIAN -> AppShellStrings(
            routesTab = "Rutas",
            mapTab = "Mapa",
            journalTab = "A miña aventura",
            guideTitle = "Guía"
        )
        AppLanguage.ENGLISH -> AppShellStrings(
            routesTab = "Routes",
            mapTab = "Map",
            journalTab = "My journey",
            guideTitle = "Guide"
        )
    }
