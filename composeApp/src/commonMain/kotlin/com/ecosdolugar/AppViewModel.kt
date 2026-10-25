package com.ecosdolugar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecosdolugar.auth.GoogleAuthResult
import com.ecosdolugar.auth.GoogleAuthService
import com.ecosdolugar.data.RouteDataSource
import com.ecosdolugar.data.enrichRoutes
import com.ecosdolugar.data.withRouteTitles
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.AppMessageStrings
import com.ecosdolugar.localization.appMessageStrings
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.GuideProfileInfo
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.model.routeCompletionPoints
import com.ecosdolugar.model.stopArrivalPoints
import com.ecosdolugar.model.trialPoints
import com.ecosdolugar.model.withStopScore
import com.ecosdolugar.util.ProfanityFilter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val MAX_DISPLAY_NAME_LENGTH = 60

data class AppState(
    val screen: NavigationScreen = NavigationScreen.RESTORING_SESSION,
    val routes: List<RouteItem> = emptyList(),
    val routeReviews: List<RouteReview> = emptyList(),
    val guideProfiles: List<GuideProfileInfo> = emptyList(),
    val selectedRoute: RouteItem? = null,
    val selectedGuide: GuideAuthor? = null,
    val mapRouteToFocus: String? = null,
    val mapReturnScreen: NavigationScreen = NavigationScreen.CATALOG,
    val mapIsExpedition: Boolean = false,
    val activeExpeditionStopId: String? = null,
    val editingRoute: RouteItem? = null,
    val userProfile: UserProfile = UserProfile(),
    val accessToken: String? = null,
    val googleSignInLoadingFor: NavigationScreen? = null,
    val displayNameDraft: String = "",
    val isSavingDisplayName: Boolean = false,
    val displayNameError: String? = null,
    val pendingAuthenticatedScreen: NavigationScreen = NavigationScreen.CHARACTER_SELECTION,
    val googleSignInError: String? = null,
    val guideSignInError: String? = null,
    val isSavingRoute: Boolean = false,
    val triggeredStopIds: Set<String> = emptySet(),
    val completedChallengesCount: Int = 0,
    val pointsGain: PointsGain? = null,
    val routeBonusGain: PointsGain? = null
)

/** Points just earned. [serial] changes on every gain so the UI can re-show the feedback. */
data class PointsGain(val points: Int, val serial: Int)

private fun AppState.withPointsGain(gained: Int): AppState =
    if (gained > 0) copy(pointsGain = PointsGain(gained, (pointsGain?.serial ?: 0) + 1)) else this

private fun AppState.withRouteBonusGain(points: Int): AppState =
    if (points > 0) copy(routeBonusGain = PointsGain(points, (routeBonusGain?.serial ?: 0) + 1)) else this

/**
 * Single source of truth for session, data and navigation. All state changes go through
 * [MutableStateFlow.update] so concurrent async results cannot overwrite each other.
 */
class AppViewModel(
    private val googleAuthService: GoogleAuthService,
    private val routeDataSource: RouteDataSource
) : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val _messages = Channel<String>(Channel.BUFFERED)

    /** One-shot user-facing errors, meant to be shown in a snackbar. */
    val messages: Flow<String> = _messages.receiveAsFlow()

    private val savedRoutesMutex = Mutex()
    private var dataJob: Job? = null
    private var language = AppLanguage.SPANISH
    private val strings: AppMessageStrings
        get() = appMessageStrings(language)

    fun onLanguageChanged(newLanguage: AppLanguage) {
        language = newLanguage
    }

    init {
        reloadData()
        restoreSession()
    }

    // region Navigation

    fun navigateTo(screen: NavigationScreen) = _state.update { it.copy(screen = screen) }

    fun openMapTab() = _state.update {
        it.copy(
            mapRouteToFocus = null,
            mapReturnScreen = NavigationScreen.CATALOG,
            mapIsExpedition = false,
            screen = NavigationScreen.MAP
        )
    }

    fun openRoute(route: RouteItem) =
        _state.update { it.copy(selectedRoute = route, screen = NavigationScreen.DETAIL) }

    fun openRouteById(routeId: String) {
        _state.value.routes.firstOrNull { it.id == routeId }?.let(::openRoute)
    }

    fun openGuideProfile(guide: GuideAuthor) =
        _state.update { it.copy(selectedGuide = guide, screen = NavigationScreen.GUIDE_PROFILE) }

    fun openMapForSelectedRoute() = _state.update {
        it.copy(
            mapRouteToFocus = it.selectedRoute?.id,
            mapReturnScreen = NavigationScreen.DETAIL,
            mapIsExpedition = false,
            screen = NavigationScreen.MAP
        )
    }

    fun startExpedition(route: RouteItem) = _state.update {
        it.copy(
            selectedRoute = route,
            activeExpeditionStopId = null,
            mapRouteToFocus = route.id,
            mapReturnScreen = NavigationScreen.DETAIL,
            mapIsExpedition = true,
            screen = NavigationScreen.MAP
        )
    }

    fun backToExpeditionMap() = _state.update {
        it.copy(
            mapRouteToFocus = it.selectedRoute?.id,
            mapReturnScreen = NavigationScreen.DETAIL,
            mapIsExpedition = true,
            screen = NavigationScreen.MAP
        )
    }

    fun closeMap() = _state.update { it.copy(mapIsExpedition = false, screen = it.mapReturnScreen) }

    fun consumePointsGain() = _state.update { it.copy(pointsGain = null) }

    fun consumeRouteBonusGain() = _state.update { it.copy(routeBonusGain = null) }

    fun onStopReached(stop: RouteStop) {
        val current = _state.value
        val isFirstArrival = stop.id !in current.userProfile.stopScores
        val arrivalPoints = stopArrivalPoints(current.userProfile.characterId)
        _state.update { state ->
            val profile = state.userProfile.copy(visitedStopIds = state.userProfile.visitedStopIds + stop.id)
                .let { p -> if (isFirstArrival) p.withStopScore(stop.id, arrivalPoints) else p }
            state.copy(
                activeExpeditionStopId = stop.id,
                triggeredStopIds = state.triggeredStopIds + stop.id,
                userProfile = profile,
                screen = NavigationScreen.EXPEDITION
            ).withPointsGain(profile.totalScore - state.userProfile.totalScore)
        }
        val session = current.session()
        if (isFirstArrival && stop.challengeId != null && session != null) {
            launchReportingErrors(strings.syncChallengeFailed) {
                routeDataSource.saveChallengeProgress(
                    userId = session.userId,
                    challengeId = stop.challengeId,
                    attempts = 0,
                    score = arrivalPoints,
                    completed = false,
                    accessToken = session.token
                )
            }
        }
    }

    // endregion

    // region Session

    fun beginGoogleSignIn(destination: NavigationScreen) {
        if (_state.value.googleSignInLoadingFor != null) return
        val isGuide = destination == NavigationScreen.GUIDE_DASHBOARD
        _state.update {
            it.copy(
                googleSignInLoadingFor = destination,
                pendingAuthenticatedScreen = destination,
                guideSignInError = if (isGuide) null else it.guideSignInError,
                googleSignInError = if (isGuide) it.googleSignInError else null
            )
        }
        viewModelScope.launch {
            try {
                handleSignInResult(destination, googleAuthService.signInWithGoogle())
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val message = error.message ?: strings.signInFailed
                _state.update {
                    if (isGuide) it.copy(guideSignInError = message) else it.copy(googleSignInError = message)
                }
            } finally {
                _state.update { it.copy(googleSignInLoadingFor = null) }
            }
        }
    }

    private suspend fun handleSignInResult(destination: NavigationScreen, result: GoogleAuthResult) {
        val isGuideDestination = destination == NavigationScreen.GUIDE_DASHBOARD
        when (result) {
            is GoogleAuthResult.Success -> {
                if (isGuideDestination) {
                    try {
                        googleAuthService.setGuideAccount(true)
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Exception) {
                        report(error.message ?: strings.guideAccessSaveFailed)
                        navigateTo(NavigationScreen.WELCOME)
                        return
                    }
                }
                val screen = when {
                    isGuideDestination ->
                        if (result.profileComplete) NavigationScreen.GUIDE_DASHBOARD else NavigationScreen.GUIDE_WELCOME
                    result.profileComplete -> NavigationScreen.CATALOG
                    result.displayNameSet -> NavigationScreen.CHARACTER_SELECTION
                    else -> NavigationScreen.DISPLAY_NAME
                }
                _state.update {
                    it.copy(
                        accessToken = result.accessToken,
                        userProfile = it.userProfile.signedIn(
                            result,
                            name = result.displayName.ifBlank { result.email },
                            isGuideAccount = isGuideDestination || result.isGuideAccount
                        ),
                        displayNameDraft = result.displayName.ifBlank { result.email.substringBefore('@') },
                        displayNameError = null,
                        screen = screen
                    )
                }
                reloadData()
            }
            is GoogleAuthResult.Error ->
                _state.update {
                    if (isGuideDestination) it.copy(guideSignInError = result.message)
                    else it.copy(googleSignInError = result.message)
                }
            GoogleAuthResult.Cancelled,
            GoogleAuthResult.NoStoredSession -> Unit
        }
    }

    private fun restoreSession() {
        viewModelScope.launch {
            val result = try {
                googleAuthService.restoreSession()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                GoogleAuthResult.Error(error.message ?: strings.restoreSessionFailed)
            }
            when (result) {
                is GoogleAuthResult.Success -> {
                    val screen = when {
                        result.isGuideAccount && !result.profileComplete -> NavigationScreen.GUIDE_WELCOME
                        result.profileComplete -> NavigationScreen.CATALOG
                        result.displayNameSet -> NavigationScreen.CHARACTER_SELECTION
                        else -> NavigationScreen.DISPLAY_NAME
                    }
                    _state.update {
                        it.copy(
                            accessToken = result.accessToken,
                            userProfile = it.userProfile.signedIn(
                                result,
                                name = result.displayName,
                                isGuideAccount = result.isGuideAccount
                            ),
                            screen = screen
                        )
                    }
                    reloadData()
                }
                is GoogleAuthResult.Error ->
                    _state.update { it.copy(googleSignInError = result.message, screen = NavigationScreen.WELCOME) }
                GoogleAuthResult.NoStoredSession,
                GoogleAuthResult.Cancelled -> navigateTo(NavigationScreen.WELCOME)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                googleAuthService.signOut(_state.value.accessToken)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                // The service clears the local session even when the remote logout fails.
                report(error.message ?: strings.signOutFailed)
            }
            _state.update {
                it.copy(
                    accessToken = null,
                    userProfile = UserProfile(),
                    triggeredStopIds = emptySet(),
                    displayNameDraft = "",
                    screen = NavigationScreen.WELCOME
                )
            }
            reloadData()
        }
    }

    // endregion

    // region Profile

    fun onDisplayNameDraftChanged(name: String) = _state.update { it.copy(displayNameDraft = name) }

    fun submitDisplayName(requiredMessage: String) {
        val current = _state.value
        val name = current.displayNameDraft.trim()
        val validationError = when {
            name.isEmpty() || name.length > MAX_DISPLAY_NAME_LENGTH -> requiredMessage
            ProfanityFilter.containsProfanity(name) -> ProfanityFilter.displayNameError(name, language)
            else -> null
        }
        if (validationError != null) {
            _state.update { it.copy(displayNameError = validationError) }
            return
        }
        if (current.isSavingDisplayName) return
        val session = current.session()
        if (session == null) {
            _state.update { it.copy(displayNameError = strings.googleSessionExpired) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSavingDisplayName = true, displayNameError = null) }
            try {
                routeDataSource.saveDisplayName(session.userId, name, session.token)
                googleAuthService.saveDisplayName(name)
                val isGuideFlow = _state.value.pendingAuthenticatedScreen == NavigationScreen.GUIDE_DASHBOARD
                if (isGuideFlow) {
                    googleAuthService.saveProfile(name, _state.value.userProfile.characterId)
                }
                _state.update {
                    it.copy(
                        userProfile = it.userProfile.copy(name = name),
                        screen = if (isGuideFlow) NavigationScreen.GUIDE_DASHBOARD else NavigationScreen.CHARACTER_SELECTION
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val message = error.message ?: strings.saveDisplayNameFailed
                _state.update { it.copy(displayNameError = message) }
            } finally {
                _state.update { it.copy(isSavingDisplayName = false) }
            }
        }
    }

    fun saveDisplayNameFromJournal(name: String) {
        val validationError = ProfanityFilter.displayNameError(name, language)
        if (validationError != null) return report(validationError)
        val session = _state.value.session() ?: return report(strings.googleSessionExpired)
        launchReportingErrors(strings.saveDisplayNameFailed) {
            routeDataSource.saveDisplayName(session.userId, name, session.token)
            googleAuthService.saveDisplayName(name)
            _state.update { it.copy(userProfile = it.userProfile.copy(name = name)) }
        }
    }

    fun saveGuideProfile(name: String, bio: String, photoDataUrl: String?) {
        val current = _state.value
        if (current.isSavingDisplayName) return
        val session = current.session()
        if (session == null) {
            _state.update { it.copy(displayNameError = strings.googleSessionExpired) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSavingDisplayName = true, displayNameError = null) }
            try {
                routeDataSource.saveGuideProfile(session.userId, name, bio, photoDataUrl, session.token)
                googleAuthService.completeGuideProfile(name)
                _state.update {
                    it.copy(
                        userProfile = it.userProfile.copy(name = name),
                        screen = NavigationScreen.GUIDE_DASHBOARD
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val message = error.message ?: strings.saveGuideProfileFailed
                _state.update { it.copy(displayNameError = message) }
            } finally {
                _state.update { it.copy(isSavingDisplayName = false) }
            }
        }
    }

    fun selectCharacter(characterId: String) =
        _state.update { it.copy(userProfile = it.userProfile.copy(characterId = characterId)) }

    fun confirmCharacter(characterId: String) {
        launchReportingErrors(strings.saveCharacterFailed) {
            googleAuthService.saveProfile(_state.value.userProfile.name, characterId)
            _state.update {
                it.copy(
                    userProfile = it.userProfile.copy(characterId = characterId),
                    screen = NavigationScreen.CATALOG
                )
            }
        }
    }

    // endregion

    // region Routes and progress

    fun toggleSavedRoute(routeId: String) {
        var nextSaved = emptySet<String>()
        _state.update {
            val saved = it.userProfile.savedRouteIds
            nextSaved = if (routeId in saved) saved - routeId else saved + routeId
            it.copy(userProfile = it.userProfile.copy(savedRouteIds = nextSaved))
        }
        val current = _state.value
        val session = current.session() ?: return
        launchReportingErrors(strings.syncSavedRoutesFailed) {
            savedRoutesMutex.withLock {
                routeDataSource.saveSavedRouteIds(session.userId, current.userProfile.name, nextSaved, session.token)
            }
        }
    }

    fun solveEnigma(stopId: String, attempts: Int, hintsUsed: Int = 0) {
        val current = _state.value
        val profile = current.userProfile
        if (stopId in profile.solvedEnigmaIds) return
        val route = current.selectedRoute
        val solvedAfter = profile.solvedEnigmaIds + stopId
        val routeCompleted = route != null && route.stops.isNotEmpty() && route.stops.all { it.id in solvedAfter }
        val arrivalPoints = profile.stopScores[stopId] ?: stopArrivalPoints(profile.characterId)
        val routeBonus = if (routeCompleted) routeCompletionPoints(profile.characterId) else 0
        val stopTotal = arrivalPoints +
            trialPoints(profile.characterId, failedAttempts = (attempts - 1).coerceAtLeast(0), hintsUsed = hintsUsed) +
            routeBonus
        _state.update { state ->
            val updated = state.userProfile.copy(solvedEnigmaIds = state.userProfile.solvedEnigmaIds + stopId)
                .withStopScore(stopId, stopTotal)
            val gained = updated.totalScore - state.userProfile.totalScore
            state.copy(userProfile = updated)
                .withPointsGain(gained - routeBonus)
                .withRouteBonusGain(routeBonus)
        }
        val challengeId = route?.stops?.firstOrNull { it.id == stopId }?.challengeId
        val session = current.session()
        if (challengeId != null && session != null) {
            launchReportingErrors(strings.syncChallengeFailed) {
                routeDataSource.saveChallengeProgress(
                    userId = session.userId,
                    challengeId = challengeId,
                    attempts = attempts,
                    score = stopTotal,
                    completed = true,
                    accessToken = session.token
                )
            }
        }
    }

    fun submitReview(routeRating: Int, guideRating: Int, comment: String) {
        val current = _state.value
        val session = current.session() ?: return
        val route = current.selectedRoute ?: return
        launchReportingErrors(strings.saveReviewFailed) {
            routeDataSource.saveRouteReview(
                userId = session.userId,
                routeId = route.id,
                guideId = route.guide.id,
                routeRating = routeRating,
                guideRating = guideRating,
                comment = comment,
                accessToken = session.token
            )
        }
    }

    fun createNewRoute() {
        if (_state.value.accessToken == null) {
            report(strings.signInBeforeCreatingRoute)
            navigateTo(NavigationScreen.WELCOME)
        } else {
            _state.update { it.copy(editingRoute = null, screen = NavigationScreen.ROUTE_EDITOR) }
        }
    }

    fun editRoute(route: RouteItem) =
        _state.update { it.copy(editingRoute = route, screen = NavigationScreen.ROUTE_EDITOR) }

    fun publishDraft(draft: RouteItem) = saveRoute(draft, "publicada", afterSave = {
        _state.update { it.copy(screen = NavigationScreen.GUIDE_DASHBOARD) }
    }, fallbackMessage = strings.publishDraftFailed)

    fun saveEditedRoute(route: RouteItem, status: String) = saveRoute(route, status, afterSave = { saved ->
        _state.update {
            if (saved.status == "publicada") it.copy(selectedRoute = saved, screen = NavigationScreen.DETAIL)
            else it.copy(screen = NavigationScreen.GUIDE_DASHBOARD)
        }
    }, fallbackMessage = strings.saveRouteFailed)

    private fun saveRoute(
        route: RouteItem,
        status: String,
        afterSave: (RouteItem) -> Unit,
        fallbackMessage: String
    ) {
        if (_state.value.isSavingRoute) return
        val token = _state.value.accessToken
        if (token == null) return report(strings.sessionExpired)
        viewModelScope.launch {
            _state.update { it.copy(isSavingRoute = true) }
            try {
                val saved = routeDataSource.saveRoute(route, status, token)
                _state.update { it.copy(routes = listOf(saved) + it.routes.filterNot { r -> r.id == saved.id }) }
                afterSave(saved)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                report(error.message ?: fallbackMessage)
            } finally {
                _state.update { it.copy(isSavingRoute = false) }
            }
        }
    }

    // endregion

    private fun reloadData() {
        dataJob?.cancel()
        dataJob = viewModelScope.launch { loadData() }
    }

    private suspend fun loadData() {
        val snapshot = _state.value
        val token = snapshot.accessToken?.takeIf { it.isNotBlank() }
        val userId = snapshot.userProfile.googleId?.takeIf { it.isNotBlank() }
        try {
            val remoteRoutes = routeDataSource.loadRoutes(token)
            val savedRouteIds = if (token != null && userId != null) {
                routeDataSource.loadSavedRouteIds(userId, token)
            } else {
                null
            }
            val completedChallengeIds = token?.let { routeDataSource.loadCompletedChallenges(it) }.orEmpty()
            val challengeScores = token?.let { routeDataSource.loadChallengeScores(it) }.orEmpty()
            val remoteStopScores = remoteRoutes
                .flatMap { it.stops }
                .mapNotNull { stop -> stop.challengeId?.let { challengeScores[it] }?.let { stop.id to it } }
                .toMap()
            val completedStopIds = remoteRoutes
                .flatMap { it.stops }
                .filter { it.challengeId in completedChallengeIds }
                .map { it.id }
            val reviews = withRouteTitles(
                orEmptyOnFailure { routeDataSource.loadRouteReviews(token) },
                remoteRoutes
            )
            val profiles = token?.let {
                orEmptyOnFailure {
                    routeDataSource.loadGuideProfiles(
                        remoteRoutes.map { route -> route.guide.id } + listOfNotNull(userId),
                        it
                    )
                }
            }.orEmpty()
            val enrichedRoutes = enrichRoutes(remoteRoutes, reviews, profiles)
            val completedChallengesCount = orZeroOnFailure { routeDataSource.loadCompletedChallengesCount() }
            _state.update {
                it.copy(
                    routes = enrichedRoutes,
                    routeReviews = reviews,
                    guideProfiles = profiles,
                    completedChallengesCount = completedChallengesCount,
                    selectedRoute = enrichedRoutes.firstOrNull { route -> route.id == it.selectedRoute?.id }
                        ?: enrichedRoutes.firstOrNull()
                        ?: it.selectedRoute,
                    userProfile = it.userProfile.copy(
                        solvedEnigmaIds = it.userProfile.solvedEnigmaIds + completedStopIds,
                        savedRouteIds = savedRouteIds ?: it.userProfile.savedRouteIds,
                        stopScores = it.userProfile.stopScores + remoteStopScores
                    ).let { profile -> profile.copy(totalScore = profile.stopScores.values.sum()) }
                )
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            report(error.message ?: strings.loadRoutesFailed)
        }
    }

    private fun launchReportingErrors(fallbackMessage: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                report(error.message ?: fallbackMessage)
            }
        }
    }

    private fun report(message: String) {
        _messages.trySend(message)
    }
}

private class Session(val userId: String, val token: String)

private fun AppState.session(): Session? {
    val token = accessToken?.takeIf { it.isNotBlank() }
    val userId = userProfile.googleId?.takeIf { it.isNotBlank() }
    return if (token != null && userId != null) Session(userId, token) else null
}

private fun UserProfile.signedIn(result: GoogleAuthResult.Success, name: String, isGuideAccount: Boolean) = copy(
    name = name,
    email = result.email,
    isVisitor = false,
    isGoogleAuthenticated = true,
    isGuideAccount = isGuideAccount,
    googleId = result.googleId,
    characterId = result.characterId ?: characterId
)

/** Optional data: a failed load yields an empty list, but cancellation must still propagate. */
private suspend inline fun <T> orEmptyOnFailure(block: () -> List<T>): List<T> =
    try {
        block()
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        emptyList()
    }

private suspend inline fun orZeroOnFailure(block: () -> Int): Int =
    try {
        block()
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        0
    }
