package com.ecosdolugar.data

import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.GuideProfileInfo
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview
import kotlin.math.roundToInt

fun enrichRoutes(
    routes: List<RouteItem>,
    reviews: List<RouteReview>,
    profiles: List<GuideProfileInfo>
): List<RouteItem> {
    val reviewsByRoute = reviews.groupBy { it.routeId }
    return routes.map { route ->
        val routeReviews = reviewsByRoute[route.id].orEmpty()
        route.copy(
            guide = enrichGuide(route.guide, reviews, profiles),
            rating = averageOf(routeReviews.map { it.routeRating }),
            reviewsCount = routeReviews.size
        )
    }
}

fun enrichGuide(
    guide: GuideAuthor,
    reviews: List<RouteReview>,
    profiles: List<GuideProfileInfo>
): GuideAuthor {
    val profile = profiles.firstOrNull { it.userId == guide.id }
    val guideReviews = reviews.filter { it.guideId == guide.id }
    return guide.copy(
        name = profile?.displayName?.takeIf { it.isNotBlank() } ?: guide.name,
        bio = profile?.bio?.takeIf { it.isNotBlank() } ?: guide.bio,
        avatarUrl = profile?.avatarUrl,
        rating = averageOf(guideReviews.map { it.guideRating }),
        reviewsCount = guideReviews.size
    )
}

fun withRouteTitles(reviews: List<RouteReview>, routes: List<RouteItem>): List<RouteReview> {
    val titles = routes.associate { it.id to it.title }
    return reviews.map { it.copy(routeTitle = titles[it.routeId].orEmpty()) }
}

fun formatRating(value: Double): String = ((value * 10).roundToInt() / 10.0).toString()

private fun averageOf(values: List<Int>): Double {
    if (values.isEmpty()) return 0.0
    return ((values.average() * 10).roundToInt()) / 10.0
}
