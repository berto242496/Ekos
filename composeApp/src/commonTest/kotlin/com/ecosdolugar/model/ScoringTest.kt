package com.ecosdolugar.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoringTest {
    private val meiga = "meiga"
    private val trasno = "trasno"
    private val nubeiro = "nubeiro"
    private val urco = "urco"

    @Test
    fun stopArrivalIsBaseOrTrasnoBonus() {
        assertEquals(50, stopArrivalPoints(meiga))
        assertEquals(70, stopArrivalPoints(trasno))
        assertEquals(50, stopArrivalPoints(nubeiro))
    }

    @Test
    fun routeCompletionIsBaseOrNubeiroBonus() {
        assertEquals(200, routeCompletionPoints(meiga))
        assertEquals(300, routeCompletionPoints(nubeiro))
        assertEquals(200, routeCompletionPoints(trasno))
    }

    @Test
    fun trialWithoutErrorsOrHintsGivesBaseScore() {
        assertEquals(100, trialPoints(meiga, failedAttempts = 0, hintsUsed = 0))
    }

    @Test
    fun failedAttemptsAndHintsReduceTrialScore() {
        assertEquals(80, trialPoints(trasno, failedAttempts = 1, hintsUsed = 0))
        assertEquals(85, trialPoints(trasno, failedAttempts = 0, hintsUsed = 1))
        assertEquals(65, trialPoints(trasno, failedAttempts = 1, hintsUsed = 1))
    }

    @Test
    fun solvedTrialNeverGoesBelowTwentyPoints() {
        assertEquals(20, trialPoints(trasno, failedAttempts = 10, hintsUsed = 0))
        assertEquals(20, trialPoints(trasno, failedAttempts = 0, hintsUsed = 10))
    }

    @Test
    fun meigaHintsAreFreeButFailedAttemptsStillCost() {
        assertEquals(100, trialPoints(meiga, failedAttempts = 0, hintsUsed = 5))
        assertEquals(80, trialPoints(meiga, failedAttempts = 1, hintsUsed = 5))
    }

    @Test
    fun urcoGetsFifteenExtraPointsPerSolvedTrial() {
        assertEquals(115, trialPoints(urco, failedAttempts = 0, hintsUsed = 0))
        assertEquals(95, trialPoints(urco, failedAttempts = 1, hintsUsed = 0))
        assertEquals(35, trialPoints(urco, failedAttempts = 10, hintsUsed = 0))
    }

    @Test
    fun nubeiroTrialScoreIsUnchangedByPower() {
        assertEquals(100, trialPoints(nubeiro, failedAttempts = 0, hintsUsed = 0))
    }

    @Test
    fun stopScoreAccumulatesWithoutDuplicatingPoints() {
        val profile = UserProfile(characterId = trasno)
            .withStopScore("stop-1", 70)
            .withStopScore("stop-1", 70 + 100)
        assertEquals(170, profile.stopScores["stop-1"])
    }

    @Test
    fun routeScoreOnlySumsItsOwnStops() {
        val route = RouteItem(
            id = "ruta-a",
            title = "Ruta A",
            subtitle = "",
            description = "",
            location = "",
            region = "",
            imageUrl = "",
            stopsCount = 2,
            duration = "",
            distance = "",
            difficulty = Difficulty.FACIL,
            categories = listOf(Category.HISTORIA),
            audioTag = "",
            tomoRef = "",
            guide = GuideAuthor(id = "guide-1", name = "Guía", title = "", bio = ""),
            overviewFeatures = emptyList(),
            stops = listOf(
                stopWithId("a1"),
                stopWithId("a2")
            )
        )
        val profile = UserProfile(characterId = meiga)
            .withStopScore("a1", 150)
            .withStopScore("b1", 300)
        assertEquals(150, profile.routeScore(route))
        assertEquals(450, profile.totalScore)
    }
}

private fun stopWithId(id: String) = RouteStop(
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
