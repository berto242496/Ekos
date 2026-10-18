package com.ecosdolugar.data

import com.ecosdolugar.model.RouteItem

interface RouteDataSource {
    suspend fun loadRoutes(accessToken: String?): List<RouteItem>

    suspend fun saveRoute(route: RouteItem, status: String, accessToken: String): RouteItem

    suspend fun saveChallengeProgress(
        challengeId: String,
        attempts: Int,
        score: Int,
        completed: Boolean,
        accessToken: String
    )

    suspend fun loadCompletedChallenges(accessToken: String): Set<String>

    suspend fun saveDisplayName(userId: String, displayName: String, accessToken: String)

    suspend fun loadSavedRouteIds(userId: String, accessToken: String): Set<String>

    suspend fun saveSavedRouteIds(
        userId: String,
        displayName: String,
        routeIds: Set<String>,
        accessToken: String
    )

    suspend fun signOut(accessToken: String)
}
