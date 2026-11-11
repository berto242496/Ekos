package com.ecosdolugar.data

import com.ecosdolugar.model.GeoPoint
import com.ecosdolugar.model.GuideProfileInfo
import com.ecosdolugar.model.ReportReason
import com.ecosdolugar.model.ReportTarget
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview

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
     * Returns the walking path for each route id, given its ordered stop coordinates.
     * Routes whose path cannot be calculated are omitted, and callers fall back to straight lines.
     */
    suspend fun loadRoutePaths(
        stopsByRouteId: Map<String, List<GeoPoint>>,
        accessToken: String
    ): Map<String, List<GeoPoint>> = emptyMap()

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
