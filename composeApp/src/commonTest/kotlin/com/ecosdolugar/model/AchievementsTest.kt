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

    private fun route(
        id: String,
        vararg stopIds: String,
        location: String = "",
        categories: List<Category> = emptyList()
    ) = RouteItem(
        id = id,
        title = "",
        subtitle = "",
        description = "",
        location = location,
        region = "",
        imageUrl = "",
        stopsCount = stopIds.size,
        duration = "",
        distance = "",
        difficulty = Difficulty.FACIL,
        categories = categories,
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

    private fun unlockedIds(profile: UserProfile, completed: List<RouteItem>) =
        userAchievementStatuses(profile, completed).filter { it.isUnlocked }.map { it.id }

    @Test
    fun completingRoutesUnlocksExplorerTiers() {
        val one = listOf(route("a"))
        assertEquals(listOf("primeros-pasos"), unlockedIds(UserProfile(), one))
        val three = List(3) { route("r$it") }
        assertTrue("explorador" in unlockedIds(UserProfile(), three))
        assertFalse("gran-explorador" in unlockedIds(UserProfile(), three))
        val ten = List(10) { route("r$it") }
        assertTrue("gran-explorador" in unlockedIds(UserProfile(), ten))
    }

    @Test
    fun discoveringTwentyStopsUnlocksStoryCollector() {
        val nineteen = UserProfile(visitedStopIds = (1..10).map { "v$it" }.toSet(), solvedEnigmaIds = (1..9).map { "s$it" }.toSet())
        assertFalse("coleccionista-historias" in unlockedIds(nineteen, emptyList()))
        val twenty = nineteen.copy(solvedEnigmaIds = nineteen.solvedEnigmaIds + "s10")
        assertTrue("coleccionista-historias" in unlockedIds(twenty, emptyList()))
    }

    @Test
    fun categoryAchievementsRequireCompletedRouteOfThatCategory() {
        val legends = listOf(route("l", location = "Tui", categories = listOf(Category.LEYENDAS)))
        val unlocked = unlockedIds(UserProfile(), legends)
        assertTrue("cazaleyendas" in unlocked)
        assertFalse("guardian-historia" in unlocked)
        assertFalse("espiritu-aventurero" in unlocked)
        assertTrue("guardian-historia" in unlockedIds(UserProfile(), listOf(route("h", categories = listOf(Category.HISTORIA)))))
        assertTrue("espiritu-aventurero" in unlockedIds(UserProfile(), listOf(route("n", categories = listOf(Category.NATURALEZA)))))
    }

    @Test
    fun threeDifferentLocationsUnlockTirelessTraveler() {
        val twoLocations = listOf(route("a", location = "Tui"), route("b", location = "Tui"), route("c", location = "Ourense"))
        assertFalse("viajero-incansable" in unlockedIds(UserProfile(), twoLocations))
        val threeLocations = twoLocations + route("d", location = "Valença")
        assertTrue("viajero-incansable" in unlockedIds(UserProfile(), threeLocations))
    }
}
