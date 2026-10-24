package com.ecosdolugar.data

import com.ecosdolugar.model.GuideProfileInfo
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview

interface RouteDataSource {
    suspend fun loadRoutes(accessToken: String?): List<RouteItem>
    suspend fun loadRouteReviews(accessToken: String?): List<RouteReview>
    suspend fun loadGuideProfiles(guideIds: List<String>, accessToken: String): List<GuideProfileInfo>

    suspend fun saveRoute(route: RouteItem, status: String, accessToken: String): RouteItem

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

    suspend fun loadTotalChallengeScore(accessToken: String): Int

    suspend fun loadCompletedChallengesCount(): Int

    suspend fun saveDisplayName(userId: String, displayName: String, accessToken: String)

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
}
