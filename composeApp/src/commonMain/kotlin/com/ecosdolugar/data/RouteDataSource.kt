package com.ecosdolugar.data

import com.ecosdolugar.model.GeoPoint
import com.ecosdolugar.model.GuideProfileInfo
import com.ecosdolugar.model.GuideSalesSummary
import com.ecosdolugar.model.ReportReason
import com.ecosdolugar.model.ReportTarget
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview

/** Walking path between stops and its duration in seconds, as calculated by the routing service. */
data class RoutePath(
    val points: List<GeoPoint>,
    val durationSeconds: Int?
)

data class RemoteAccountProfile(
    val displayName: String?,
    val characterId: String?,
    val isGuide: Boolean
)

interface RouteDataSource {
    suspend fun loadRoutes(accessToken: String?): List<RouteItem>
    suspend fun loadRouteReviews(accessToken: String?): List<RouteReview>
    suspend fun loadGuideProfiles(guideIds: List<String>, accessToken: String): List<GuideProfileInfo>

    suspend fun saveRoute(route: RouteItem, status: String, accessToken: String): RouteItem

    /**
     * Returns the walking path and duration for each route id, given its ordered stop coordinates.
     * Routes whose path cannot be calculated are omitted, and callers fall back to straight lines.
     */
    suspend fun loadRoutePaths(
        stopsByRouteId: Map<String, List<GeoPoint>>,
        accessToken: String
    ): Map<String, RoutePath> = emptyMap()

    suspend fun saveChallengeProgress(
        userId: String,
        challengeId: String,
        attempts: Int,
        score: Int,
        completed: Boolean,
        accessToken: String
    )

    suspend fun saveRouteReview(
        userId: String,
        routeId: String,
        guideId: String,
        routeRating: Int,
        guideRating: Int,
        comment: String,
        accessToken: String
    )

    suspend fun loadCompletedChallenges(accessToken: String): Set<String>

    suspend fun loadChallengeScores(accessToken: String): Map<String, Int>

    suspend fun loadCompletedChallengesCount(): Int

    suspend fun loadAccountProfile(userId: String, accessToken: String): RemoteAccountProfile?

    suspend fun saveDisplayName(userId: String, displayName: String, accessToken: String)

    suspend fun saveCharacter(userId: String, characterId: String, accessToken: String)

    suspend fun saveGuideProfile(
        userId: String,
        displayName: String,
        bio: String,
        avatarUrl: String?,
        accessToken: String
    ): String?

    suspend fun loadSavedRouteIds(userId: String, accessToken: String): Set<String>

    /** Ids of the paid routes the signed-in user has already bought. */
    suspend fun loadPurchasedRouteIds(accessToken: String): Set<String> = emptySet()

    /** Sales and balance of the routes the signed-in guide owns. Null when unavailable. */
    suspend fun loadGuideSalesSummary(accessToken: String): GuideSalesSummary? = null

    /** Verifies a Google Play purchase on the server, records it for the user and consumes it. */
    suspend fun verifyRoutePurchase(
        routeId: String,
        productId: String,
        purchaseToken: String,
        accessToken: String
    ) {
        throw UnsupportedOperationException("Las compras no están disponibles.")
    }

    suspend fun saveSavedRouteIds(
        userId: String,
        displayName: String,
        routeIds: Set<String>,
        accessToken: String
    )

    suspend fun signOut(accessToken: String)

    /** Permanently deletes the account, its data and its uploaded media. */
    suspend fun deleteAccount(userId: String, accessToken: String)

    suspend fun reportContent(
        reporterId: String,
        targetType: ReportTarget,
        targetId: String,
        reason: ReportReason,
        accessToken: String
    )

    suspend fun loadBlockedUserIds(accessToken: String): Set<String>

    suspend fun blockUser(blockerId: String, blockedId: String, accessToken: String)
}
