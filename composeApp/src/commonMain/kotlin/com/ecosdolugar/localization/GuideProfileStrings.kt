package com.ecosdolugar.localization

data class GuideProfileStrings(
    val back: String,
    val title: String,
    val verifiedGuide: String,
    val defaultLocation: String,
    val noReviewsYet: String,
    val aboutGuide: String,
    val defaultRouteTitle: String,
    val noPublishedRoutes: String,
    val viewRoute: String,
    val ratingWithReviews: (rating: String, count: Int) -> String,
    val reviewsTitle: (count: Int) -> String,
    val routeAndGuideRating: (routeRating: Int, guideRating: Int) -> String,
    val routesCreatedBy: (firstName: String, count: Int) -> String,
    val routeSummary: (location: String, duration: String, stopsCount: Int) -> String
)

fun guideProfileStrings(language: AppLanguage): GuideProfileStrings =
    when (language) {
        AppLanguage.SPANISH -> GuideProfileStrings(
            back = "Volver",
            title = "Perfil del guía",
            verifiedGuide = "Guía verificado",
            defaultLocation = "Galicia",
            noReviewsYet = "Sin reseñas todavía",
            aboutGuide = "SOBRE EL GUÍA",
            defaultRouteTitle = "Ruta",
            noPublishedRoutes = "Este guía todavía no tiene rutas publicadas.",
            viewRoute = "Ver ruta",
            ratingWithReviews = { rating, count -> "$rating ($count reseñas)" },
            reviewsTitle = { count -> "Reseñas ($count)" },
            routeAndGuideRating = { routeRating, guideRating -> "Ruta $routeRating/5 · Guía $guideRating/5" },
            routesCreatedBy = { firstName, count -> "Rutas creadas por $firstName ($count)" },
            routeSummary = { location, duration, stopsCount -> "$location · $duration · $stopsCount paradas" }
        )
        AppLanguage.GALICIAN -> GuideProfileStrings(
            back = "Volver",
            title = "Perfil do guía",
            verifiedGuide = "Guía verificado",
            defaultLocation = "Galicia",
            noReviewsYet = "Sen avaliacións por agora",
            aboutGuide = "SOBRE O GUÍA",
            defaultRouteTitle = "Ruta",
            noPublishedRoutes = "Este guía aínda non ten rutas publicadas.",
            viewRoute = "Ver ruta",
            ratingWithReviews = { rating, count -> "$rating ($count avaliacións)" },
            reviewsTitle = { count -> "Avaliacións ($count)" },
            routeAndGuideRating = { routeRating, guideRating -> "Ruta $routeRating/5 · Guía $guideRating/5" },
            routesCreatedBy = { firstName, count -> "Rutas creadas por $firstName ($count)" },
            routeSummary = { location, duration, stopsCount -> "$location · $duration · $stopsCount paradas" }
        )
        AppLanguage.ENGLISH -> GuideProfileStrings(
            back = "Back",
            title = "Guide profile",
            verifiedGuide = "Verified guide",
            defaultLocation = "Galicia",
            noReviewsYet = "No reviews yet",
            aboutGuide = "ABOUT THE GUIDE",
            defaultRouteTitle = "Route",
            noPublishedRoutes = "This guide doesn't have any published routes yet.",
            viewRoute = "View route",
            ratingWithReviews = { rating, count -> "$rating ($count reviews)" },
            reviewsTitle = { count -> "Reviews ($count)" },
            routeAndGuideRating = { routeRating, guideRating -> "Route $routeRating/5 · Guide $guideRating/5" },
            routesCreatedBy = { firstName, count -> "Routes created by $firstName ($count)" },
            routeSummary = { location, duration, stopsCount -> "$location · $duration · $stopsCount stops" }
        )
    }
