package com.ecosdolugar.localization

import com.ecosdolugar.model.Difficulty

data class RouteDetailStrings(
    val back: String,
    val saveRoute: String,
    val shareRoute: String,
    val viewMap: String,
    val startRoute: String,
    val verifiedGuide: String,
    val ratingsWord: String,
    val noRatingsYet: String,
    val noRatings: String,
    val guideReviewsWord: String,
    val routeGuideLabel: String,
    val stopsSingular: String,
    val stopsPlural: String
)

fun routeDetailStrings(language: AppLanguage): RouteDetailStrings =
    when (language) {
        AppLanguage.SPANISH -> RouteDetailStrings(
            back = "Volver",
            saveRoute = "Guardar ruta",
            shareRoute = "Compartir ruta",
            viewMap = "Ver mapa",
            startRoute = "Empezar ruta",
            verifiedGuide = "Guía verificado",
            ratingsWord = "valoraciones",
            noRatingsYet = "Sin valoraciones todavía",
            noRatings = "Sin valoraciones",
            guideReviewsWord = "reseñas",
            routeGuideLabel = "GUÍA DE LA RUTA",
            stopsSingular = "parada",
            stopsPlural = "paradas"
        )
        AppLanguage.GALICIAN -> RouteDetailStrings(
            back = "Volver",
            saveRoute = "Gardar ruta",
            shareRoute = "Compartir ruta",
            viewMap = "Ver mapa",
            startRoute = "Comezar ruta",
            verifiedGuide = "Guía verificado",
            ratingsWord = "valoracións",
            noRatingsYet = "Sen valoracións todavía",
            noRatings = "Sen valoracións",
            guideReviewsWord = "avaliacións",
            routeGuideLabel = "GUÍA DA RUTA",
            stopsSingular = "parada",
            stopsPlural = "paradas"
        )
        AppLanguage.ENGLISH -> RouteDetailStrings(
            back = "Back",
            saveRoute = "Save route",
            shareRoute = "Share route",
            viewMap = "View map",
            startRoute = "Start route",
            verifiedGuide = "Verified guide",
            ratingsWord = "ratings",
            noRatingsYet = "No ratings yet",
            noRatings = "No ratings",
            guideReviewsWord = "reviews",
            routeGuideLabel = "ROUTE GUIDE",
            stopsSingular = "stop",
            stopsPlural = "stops"
        )
    }

fun difficultyLabel(difficulty: Difficulty, language: AppLanguage): String =
    when (language) {
        AppLanguage.SPANISH -> when (difficulty) {
            Difficulty.FACIL -> "Fácil"
            Difficulty.MEDIA -> "Moderada"
            Difficulty.EXIGENTE -> "Difícil"
        }
        AppLanguage.GALICIAN -> when (difficulty) {
            Difficulty.FACIL -> "Fácil"
            Difficulty.MEDIA -> "Moderada"
            Difficulty.EXIGENTE -> "Difícil"
        }
        AppLanguage.ENGLISH -> when (difficulty) {
            Difficulty.FACIL -> "Easy"
            Difficulty.MEDIA -> "Moderate"
            Difficulty.EXIGENTE -> "Hard"
        }
    }
