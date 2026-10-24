package com.ecosdolugar

import com.ecosdolugar.auth.GoogleAuthResult
import com.ecosdolugar.auth.GoogleAuthService
import com.ecosdolugar.data.RouteDataSource
import com.ecosdolugar.model.Category
import com.ecosdolugar.model.Difficulty
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.GuideProfileInfo
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
    private val auth = FakeAuth()
    private val data = FakeData()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = AppViewModel(auth, data)

    private fun session(
        profileComplete: Boolean = true,
        displayNameSet: Boolean = true,
        isGuide: Boolean = false
    ) = GoogleAuthResult.Success(
        googleId = "user-1",
        email = "ana@example.com",
        displayName = "Ana",
        photoUrl = null,
        idToken = "",
        accessToken = "token",
        refreshToken = "refresh",
        characterId = "meiga",
        displayNameSet = displayNameSet,
        profileComplete = profileComplete,
        isGuideAccount = isGuide
    )

    @Test
    fun withoutStoredSessionShowsWelcomeAndLoadsPublicRoutes() {
        auth.restoreResult = GoogleAuthResult.NoStoredSession
        val state = viewModel().state.value
        assertEquals(NavigationScreen.WELCOME, state.screen)
        assertEquals(data.routes.map { it.id }, state.routes.map { it.id })
        assertNull(state.accessToken)
    }

    @Test
    fun restoredCompleteSessionGoesToCatalogWithRemoteProgress() {
        auth.restoreResult = session()
        data.savedIds = setOf("route-x")
        data.score = 300
        val state = viewModel().state.value
        assertEquals(NavigationScreen.CATALOG, state.screen)
        assertEquals("token", state.accessToken)
        assertEquals(setOf("route-x"), state.userProfile.savedRouteIds)
        assertEquals(300, state.userProfile.totalScore)
    }

    @Test
    fun restoredSessionWithoutDisplayNameAsksForIt() {
        auth.restoreResult = session(profileComplete = false, displayNameSet = false)
        assertEquals(NavigationScreen.DISPLAY_NAME, viewModel().state.value.screen)
    }

    @Test
    fun cancelledSignInStaysOnWelcomeAndClearsLoading() {
        auth.restoreResult = GoogleAuthResult.NoStoredSession
        auth.signInResult = GoogleAuthResult.Cancelled
        val vm = viewModel()
        vm.beginGoogleSignIn(NavigationScreen.CHARACTER_SELECTION)
        assertEquals(NavigationScreen.WELCOME, vm.state.value.screen)
        assertNull(vm.state.value.googleSignInLoadingFor)
    }

    @Test
    fun failedSignInShowsErrorOnTheMatchingButton() {
        auth.restoreResult = GoogleAuthResult.NoStoredSession
        auth.signInResult = GoogleAuthResult.Error("boom")
        val vm = viewModel()
        vm.beginGoogleSignIn(NavigationScreen.GUIDE_DASHBOARD)
        assertEquals("boom", vm.state.value.guideSignInError)
        assertNull(vm.state.value.googleSignInError)
    }

    @Test
    fun guideSignInMarksAccountAsGuide() {
        auth.restoreResult = GoogleAuthResult.NoStoredSession
        auth.signInResult = session(profileComplete = false)
        val vm = viewModel()
        vm.beginGoogleSignIn(NavigationScreen.GUIDE_DASHBOARD)
        assertEquals(NavigationScreen.GUIDE_WELCOME, vm.state.value.screen)
        assertTrue(vm.state.value.userProfile.isGuideAccount)
        assertTrue(auth.guideAccountSet)
    }

    @Test
    fun signOutResetsProgressEvenWhenRemoteLogoutFails() = runTest {
        auth.restoreResult = session()
        val vm = viewModel()
        vm.solveEnigma("stop-1", attempts = 1)
        auth.signOutError = IllegalStateException("offline")
        vm.signOut()
        val state = vm.state.value
        assertEquals(NavigationScreen.WELCOME, state.screen)
        assertNull(state.accessToken)
        assertTrue(state.userProfile.solvedEnigmaIds.isEmpty())
        assertEquals(0, state.userProfile.totalScore)
        assertEquals("offline", vm.messages.first())
    }

    @Test
    fun savedRouteChangesAreSyncedInOrder() {
        auth.restoreResult = session()
        val vm = viewModel()
        vm.toggleSavedRoute("a")
        vm.toggleSavedRoute("b")
        vm.toggleSavedRoute("a")
        assertEquals(setOf("b"), vm.state.value.userProfile.savedRouteIds)
        assertEquals(listOf(setOf("a"), setOf("a", "b"), setOf("b")), data.syncedSavedIds)
    }

    @Test
    fun visitorsDoNotSyncSavedRoutes() {
        auth.restoreResult = GoogleAuthResult.NoStoredSession
        val vm = viewModel()
        vm.toggleSavedRoute("a")
        assertEquals(setOf("a"), vm.state.value.userProfile.savedRouteIds)
        assertTrue(data.syncedSavedIds.isEmpty())
    }

    @Test
    fun solvingTheSameEnigmaTwiceScoresOnce() {
        auth.restoreResult = GoogleAuthResult.NoStoredSession
        val vm = viewModel()
        vm.solveEnigma("stop-1", 1)
        vm.solveEnigma("stop-1", 2)
        assertEquals(CHALLENGE_SCORE, vm.state.value.userProfile.totalScore)
    }

    @Test
    fun failedOptionalLoadsDoNotBreakRouteLoading() {
        auth.restoreResult = session()
        data.failReviews = true
        data.failProfiles = true
        val state = viewModel().state.value
        assertEquals(data.routes.map { it.id }, state.routes.map { it.id })
        assertTrue(state.routeReviews.isEmpty())
        assertTrue(state.guideProfiles.isEmpty())
    }

    @Test
    fun routeLoadFailureIsReportedAsMessage() = runTest {
        auth.restoreResult = GoogleAuthResult.NoStoredSession
        data.failRoutes = true
        val vm = viewModel()
        assertEquals("sin red", vm.messages.first())
    }

    @Test
    fun profaneDisplayNameIsRejectedWithoutCallingTheServer() {
        auth.restoreResult = session(profileComplete = false, displayNameSet = false)
        val vm = viewModel()
        vm.onDisplayNameDraftChanged("fuck")
        vm.submitDisplayName(requiredMessage = "requerido")
        assertEquals("Ese nombre contiene palabras no permitidas.", vm.state.value.displayNameError)
        assertTrue(data.savedNames.isEmpty())
        assertFalse(vm.state.value.isSavingDisplayName)
    }

    @Test
    fun validDisplayNameIsSavedAndMovesToCharacterSelection() {
        auth.restoreResult = session(profileComplete = false, displayNameSet = false)
        val vm = viewModel()
        vm.onDisplayNameDraftChanged("  Xoán  ")
        vm.submitDisplayName(requiredMessage = "requerido")
        assertEquals(listOf("Xoán"), data.savedNames)
        assertEquals(NavigationScreen.CHARACTER_SELECTION, vm.state.value.screen)
        assertEquals("Xoán", vm.state.value.userProfile.name)
    }

    @Test
    fun mapNavigationRemembersWhereToReturn() {
        auth.restoreResult = GoogleAuthResult.NoStoredSession
        val vm = viewModel()
        val route = data.routes.first()
        vm.startExpedition(route)
        assertEquals(NavigationScreen.MAP, vm.state.value.screen)
        assertTrue(vm.state.value.mapIsExpedition)
        vm.closeMap()
        assertEquals(NavigationScreen.DETAIL, vm.state.value.screen)
        assertFalse(vm.state.value.mapIsExpedition)
    }

    @Test
    fun appWorksWithoutAnyRoutes() {
        auth.restoreResult = GoogleAuthResult.NoStoredSession
        data.routes = emptyList()
        val vm = viewModel()
        assertEquals(NavigationScreen.WELCOME, vm.state.value.screen)
        assertTrue(vm.state.value.routes.isEmpty())
        assertNull(vm.state.value.selectedRoute)
    }
}

private fun fixtureRoute(id: String) = RouteItem(
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
    guide = GuideAuthor(id = "guide-1", name = "Guía", title = "", bio = ""),
    overviewFeatures = emptyList(),
    stops = emptyList()
)

private class FakeAuth : GoogleAuthService {
    var restoreResult: GoogleAuthResult = GoogleAuthResult.NoStoredSession
    var signInResult: GoogleAuthResult = GoogleAuthResult.Cancelled
    var signOutError: Exception? = null
    var guideAccountSet = false

    override suspend fun signInWithGoogle() = signInResult
    override suspend fun restoreSession() = restoreResult
    override suspend fun saveDisplayName(displayName: String) = Unit
    override suspend fun completeGuideProfile(displayName: String) = Unit
    override suspend fun saveProfile(displayName: String, characterId: String) = Unit
    override suspend fun setGuideAccount(isGuideAccount: Boolean) {
        guideAccountSet = isGuideAccount
    }

    override suspend fun signOut(accessToken: String?) {
        signOutError?.let { throw it }
    }
}

private class FakeData : RouteDataSource {
    var routes: List<RouteItem> = listOf(fixtureRoute("ruta-1"), fixtureRoute("ruta-2"))
    var savedIds: Set<String> = emptySet()
    var score = 0
    var completedChallengesCount = 0
    var failReviews = false
    var failProfiles = false
    var failRoutes = false
    val syncedSavedIds = mutableListOf<Set<String>>()
    val savedNames = mutableListOf<String>()

    override suspend fun loadRoutes(accessToken: String?): List<RouteItem> {
        if (failRoutes) throw IllegalStateException("sin red")
        return routes
    }

    override suspend fun loadRouteReviews(accessToken: String?): List<RouteReview> {
        if (failReviews) throw IllegalStateException("reviews")
        return emptyList()
    }

    override suspend fun loadGuideProfiles(guideIds: List<String>, accessToken: String): List<GuideProfileInfo> {
        if (failProfiles) throw IllegalStateException("profiles")
        return emptyList()
    }

    override suspend fun saveRoute(route: RouteItem, status: String, accessToken: String) = route.copy(status = status)
    override suspend fun saveChallengeProgress(
        userId: String, challengeId: String, attempts: Int, score: Int, completed: Boolean, accessToken: String
    ) = Unit

    override suspend fun saveRouteReview(
        userId: String, routeId: String, guideId: String, routeRating: Int, guideRating: Int,
        comment: String, accessToken: String
    ) = Unit

    override suspend fun loadCompletedChallenges(accessToken: String): Set<String> = emptySet()
    override suspend fun loadTotalChallengeScore(accessToken: String) = score
    override suspend fun loadCompletedChallengesCount() = completedChallengesCount
    override suspend fun saveDisplayName(userId: String, displayName: String, accessToken: String) {
        savedNames += displayName
    }

    override suspend fun saveGuideProfile(
        userId: String, displayName: String, bio: String, avatarUrl: String?, accessToken: String
    ): String? = null

    override suspend fun loadSavedRouteIds(userId: String, accessToken: String) = savedIds
    override suspend fun saveSavedRouteIds(
        userId: String, displayName: String, routeIds: Set<String>, accessToken: String
    ) {
        syncedSavedIds += routeIds
    }

    override suspend fun signOut(accessToken: String) = Unit
}
