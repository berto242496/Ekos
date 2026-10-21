package com.ecosdolugar.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AchievementsTest {
    private fun stop(id: String) = RouteStop(
        id = id,
        order = 1,
        title = "",
        subtitle = "",
        locationName = "",
        story = "",
        historicalNote = "",
        enigma = FieldEnigma(question = "", hint = "", answer = ""),
        audioDuration = "",
        latitude = 0.0,
        longitude = 0.0,
        mapPositionX = 0f,
        mapPositionY = 0f
    )

    private fun route(id: String, vararg stopIds: String) = RouteItem(
        id = id,
        title = "",
        subtitle = "",
        description = "",
        location = "",
        region = "",
        imageUrl = "",
        stopsCount = stopIds.size,
        duration = "",
        distance = "",
        difficulty = Difficulty.FACIL,
        categories = emptyList(),
        audioTag = "",
        tomoRef = "",
        overviewFeatures = emptyList(),
        stops = stopIds.map(::stop)
    )

    @Test
    fun routeIsCompletedOnlyWhenAllStopsAreSolved() {
        val routes = listOf(route("a", "a1", "a2"), route("b", "b1"), route("empty"))
        val profile = UserProfile(solvedEnigmaIds = setOf("a1", "b1"))
        assertEquals(listOf("b"), completedRoutesOf(profile, routes).map { it.id })
    }

    @Test
    fun newUserHasNoProgressNorAchievements() {
        val profile = UserProfile()
        assertTrue(profile.solvedEnigmaIds.isEmpty())
        assertFalse(userAchievementStatuses(profile, emptyList()).any { it.isUnlocked })
    }

    @Test
    fun solvingAnEnigmaUnlocksMenteAguda() {
        val profile = UserProfile(solvedEnigmaIds = setOf("x"))
        val unlocked = userAchievementStatuses(profile, emptyList()).filter { it.isUnlocked }.map { it.id }
        assertEquals(listOf("mente-aguda"), unlocked)
    }
}
