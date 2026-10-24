package com.ecosdolugar.localization

import com.ecosdolugar.model.Difficulty

data class GuideDashboardStrings(
    val back: String,
    val title: String,
    val newRoute: String,
    val publishedStat: String,
    val pendingStat: String,
    val explorersStat: String,
    val ratingStat: String,
    val verifiedGuide: String,
    val noReviewsYet: String,
    val defaultRouteTitle: String,
    val routeLabel: String,
    val guideLabel: String,
    val noReviews: String,
    val noPublishedRoutes: String,
    val noPendingRoutes: String,
    val statusDraft: String,
    val statusPending: String,
    val statusPublished: String,
    val startsLabel: String,
    val stopsLabel: String,
    val difficultyLabel: String,
    val publishAction: String,
    val continueDraftAction: String,
    val editRouteAction: String,
    val publishedTab: (count: Int) -> String,
    val pendingTab: (count: Int) -> String,
    val reviewsTab: (count: Int) -> String,
    val ratingWithReviews: (rating: String, count: Int) -> String,
    val routeSummary: (location: String, duration: String, stopsCount: Int) -> String,
    val difficulty: (difficulty: Difficulty) -> String
)

fun guideDashboardStrings(language: AppLanguage): GuideDashboardStrings =
    when (language) {
        AppLanguage.SPANISH -> GuideDashboardStrings(
            back = "Volver",
            title = "Panel del guía",
            newRoute = "Nueva ruta",
            publishedStat = "Publicadas",
            pendingStat = "Pendientes",
            explorersStat = "Exploradores",
            ratingStat = "Valoración",
            verifiedGuide = "Guía verificado",
            noReviewsYet = "Sin reseñas todavía",
            defaultRouteTitle = "Ruta",
            routeLabel = "Ruta",
            guideLabel = "Guía",
            noReviews = "Todavía no tienes reseñas de exploradores.",
            noPublishedRoutes = "Todavía no tienes rutas publicadas.",
            noPendingRoutes = "No hay rutas pendientes de revisión.",
            statusDraft = "Borrador",
            statusPending = "Pendiente de verificación",
            statusPublished = "Publicada",
            startsLabel = "INICIOS",
            stopsLabel = "PARADAS",
            difficultyLabel = "DIFICULTAD",
            publishAction = "Publicar",
            continueDraftAction = "Continuar borrador",
            editRouteAction = "Editar ruta",
            publishedTab = { count -> "Publicadas ($count)" },
            pendingTab = { count -> "Pendientes ($count)" },
            reviewsTab = { count -> "Reseñas ($count)" },
            ratingWithReviews = { rating, count -> "$rating ($count reseñas)" },
            routeSummary = { location, duration, stopsCount -> "$location · $duration · $stopsCount paradas" },
            difficulty = { difficulty ->
                when (difficulty) {
                    Difficulty.FACIL -> "Fácil"
                    Difficulty.MEDIA -> "Media"
                    Difficulty.EXIGENTE -> "Exigente"
                }
            }
        )
        AppLanguage.GALICIAN -> GuideDashboardStrings(
            back = "Volver",
            title = "Panel do guía",
            newRoute = "Nova ruta",
            publishedStat = "Publicadas",
            pendingStat = "Pendentes",
            explorersStat = "Exploradores",
            ratingStat = "Valoración",
            verifiedGuide = "Guía verificado",
            noReviewsYet = "Sen avaliacións por agora",
            defaultRouteTitle = "Ruta",
            routeLabel = "Ruta",
            guideLabel = "Guía",
            noReviews = "Aínda non tes avaliacións de exploradores.",
            noPublishedRoutes = "Aínda non tes rutas publicadas.",
            noPendingRoutes = "Non hai rutas pendentes de revisión.",
            statusDraft = "Borrador",
            statusPending = "Pendente de verificación",
            statusPublished = "Publicada",
            startsLabel = "INICIOS",
            stopsLabel = "PARADAS",
            difficultyLabel = "DIFICULTADE",
            publishAction = "Publicar",
            continueDraftAction = "Continuar borrador",
            editRouteAction = "Editar ruta",
            publishedTab = { count -> "Publicadas ($count)" },
            pendingTab = { count -> "Pendentes ($count)" },
            reviewsTab = { count -> "Avaliacións ($count)" },
            ratingWithReviews = { rating, count -> "$rating ($count avaliacións)" },
            routeSummary = { location, duration, stopsCount -> "$location · $duration · $stopsCount paradas" },
            difficulty = { difficulty ->
                when (difficulty) {
                    Difficulty.FACIL -> "Fácil"
                    Difficulty.MEDIA -> "Media"
                    Difficulty.EXIGENTE -> "Esixente"
                }
            }
        )
        AppLanguage.ENGLISH -> GuideDashboardStrings(
            back = "Back",
            title = "Guide dashboard",
            newRoute = "New route",
            publishedStat = "Published",
            pendingStat = "Pending",
            explorersStat = "Explorers",
            ratingStat = "Rating",
            verifiedGuide = "Verified guide",
            noReviewsYet = "No reviews yet",
            defaultRouteTitle = "Route",
            routeLabel = "Route",
            guideLabel = "Guide",
            noReviews = "You don't have any explorer reviews yet.",
            noPublishedRoutes = "You don't have any published routes yet.",
            noPendingRoutes = "There are no routes pending review.",
            statusDraft = "Draft",
            statusPending = "Pending verification",
            statusPublished = "Published",
            startsLabel = "STARTS",
            stopsLabel = "STOPS",
            difficultyLabel = "DIFFICULTY",
            publishAction = "Publish",
            continueDraftAction = "Continue draft",
            editRouteAction = "Edit route",
            publishedTab = { count -> "Published ($count)" },
            pendingTab = { count -> "Pending ($count)" },
            reviewsTab = { count -> "Reviews ($count)" },
            ratingWithReviews = { rating, count -> "$rating ($count reviews)" },
            routeSummary = { location, duration, stopsCount -> "$location · $duration · $stopsCount stops" },
            difficulty = { difficulty ->
                when (difficulty) {
                    Difficulty.FACIL -> "Easy"
                    Difficulty.MEDIA -> "Medium"
                    Difficulty.EXIGENTE -> "Demanding"
                }
            }
        )
    }
