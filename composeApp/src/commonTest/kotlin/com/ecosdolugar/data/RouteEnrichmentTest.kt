package com.ecosdolugar.data

import com.ecosdolugar.model.Category
import com.ecosdolugar.model.Difficulty
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.GuideProfileInfo
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RouteEnrichmentTest {
    private val guide = GuideAuthor(id = "guide-1", name = "Original", title = "", bio = "Original bio")

    private fun route(id: String) = RouteItem(
        id = id,
        title = "Ruta $id",
        subtitle = "",
        description = "",
        location = "",
        region = "",
        imageUrl = "",
        stopsCount = 0,
        duration = "",
        distance = "",
        difficulty = Difficulty.FACIL,
        categories = listOf(Category.HISTORIA),
        audioTag = "",
        tomoRef = "",
        guide = guide,
        overviewFeatures = emptyList(),
        stops = emptyList()
    )

    private fun review(routeId: String, routeRating: Int, guideRating: Int) = RouteReview(
        routeId = routeId,
        guideId = guide.id,
        userId = "user-$routeRating-$guideRating",
        routeRating = routeRating,
        guideRating = guideRating,
        comment = "",
        createdAt = ""
    )

    @Test
    fun routeWithoutReviewsHasZeroRating() {
        val enriched = enrichRoutes(listOf(route("a")), emptyList(), emptyList()).single()
        assertEquals(0.0, enriched.rating)
        assertEquals(0, enriched.reviewsCount)
    }

    @Test
    fun routeRatingOnlyUsesReviewsOfThatRoute() {
        val reviews = listOf(review("a", 5, 4), review("a", 4, 5), review("b", 1, 1))
        val enriched = enrichRoutes(listOf(route("a"), route("b")), reviews, emptyList())
        assertEquals(4.5, enriched[0].rating)
        assertEquals(2, enriched[0].reviewsCount)
        assertEquals(1.0, enriched[1].rating)
    }

    @Test
    fun guideRatingAggregatesAllGuideReviewsAndRoundsToOneDecimal() {
        val reviews = listOf(review("a", 5, 5), review("b", 5, 4), review("b", 5, 4))
        val enriched = enrichGuide(guide, reviews, emptyList())
        assertEquals(4.3, enriched.rating)
        assertEquals(3, enriched.reviewsCount)
    }

    @Test
    fun blankProfileFieldsKeepGuideValues() {
        val profile = GuideProfileInfo(userId = guide.id, displayName = " ", bio = "", avatarUrl = "photo")
        val enriched = enrichGuide(guide, emptyList(), listOf(profile))
        assertEquals("Original", enriched.name)
        assertEquals("Original bio", enriched.bio)
        assertEquals("photo", enriched.avatarUrl)
    }

    @Test
    fun profileOfAnotherUserIsIgnored() {
        val profile = GuideProfileInfo(userId = "other", displayName = "Other", bio = "x", avatarUrl = null)
        val enriched = enrichGuide(guide, emptyList(), listOf(profile))
        assertEquals("Original", enriched.name)
        assertNull(enriched.avatarUrl)
    }

    @Test
    fun reviewsGetRouteTitleOrEmptyWhenRouteIsMissing() {
        val result = withRouteTitles(listOf(review("a", 5, 5), review("missing", 5, 5)), listOf(route("a")))
        assertEquals("Ruta a", result[0].routeTitle)
        assertEquals("", result[1].routeTitle)
    }

    @Test
    fun formatRatingKeepsOneDecimal() {
        assertEquals("4.0", formatRating(4.0))
        assertEquals("4.3", formatRating(4.26))
    }
}
