package com.ecosdolugar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecosdolugar.data.enrichGuide
import com.ecosdolugar.localization.appLanguageStrings
import com.ecosdolugar.localization.appShellStrings
import com.ecosdolugar.localization.rememberAppLanguageController
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.routeScore
import com.ecosdolugar.platform.PlatformBackHandler
import com.ecosdolugar.ui.screens.*
import com.ecosdolugar.ui.theme.EcosDoLugarTheme
import com.ecosdolugar.ui.theme.PineDeep
import com.ecosdolugar.ui.theme.SurfaceCream

enum class NavigationScreen {
    RESTORING_SESSION,
    WELCOME,
    CHARACTER_SELECTION,
    CATALOG,
    DETAIL,
    GUIDE_PROFILE,
    EXPEDITION,
    MAP,
    JOURNAL,
    DISPLAY_NAME,
    GUIDE_WELCOME,
    GUIDE_PROFILE_SETUP,
    GUIDE_DASHBOARD,
    ROUTE_EDITOR
}
@Composable
private fun RowScope.AppNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = PineDeep.copy(alpha = 0.12f),
            selectedIconColor = PineDeep,
            selectedTextColor = PineDeep
        )
    )
}

/** Screen shown by the system back gesture; null lets the platform handle it (exit app). */
private fun backDestination(screen: NavigationScreen): NavigationScreen? = when (screen) {
    NavigationScreen.DETAIL,
    NavigationScreen.JOURNAL,
    NavigationScreen.GUIDE_DASHBOARD -> NavigationScreen.CATALOG
    NavigationScreen.GUIDE_PROFILE -> NavigationScreen.DETAIL
    NavigationScreen.GUIDE_WELCOME -> NavigationScreen.WELCOME
    NavigationScreen.GUIDE_PROFILE_SETUP -> NavigationScreen.GUIDE_WELCOME
    // MAP, EXPEDITION and ROUTE_EDITOR register their own handlers.
    else -> null
}

@Composable
fun App(viewModel: AppViewModel) {
    EcosDoLugarTheme {
        val state by viewModel.state.collectAsStateWithLifecycle()
        val appLanguage = rememberAppLanguageController()
        val welcomeStrings = appLanguageStrings(appLanguage.current)
        val shellStrings = appShellStrings(appLanguage.current)
        val snackbarHostState = remember { SnackbarHostState() }
        val userProfile = state.userProfile

        LaunchedEffect(appLanguage.current) {
            viewModel.onLanguageChanged(appLanguage.current)
        }

        LaunchedEffect(viewModel) {
            viewModel.messages.collect { snackbarHostState.showSnackbar(it) }
        }

        if (state.screen == NavigationScreen.CHARACTER_SELECTION) {
            PlatformBackHandler { viewModel.cancelCharacterSelection() }
        }
        backDestination(state.screen)?.let { destination ->
            PlatformBackHandler { viewModel.navigateTo(destination) }
        }

        val showBottomBar = state.screen == NavigationScreen.CATALOG ||
            (state.screen == NavigationScreen.MAP && !state.mapIsExpedition) ||
            state.screen == NavigationScreen.JOURNAL

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = SurfaceCream,
                        tonalElevation = 8.dp
                    ) {
                        AppNavigationBarItem(
                            selected = state.screen == NavigationScreen.CATALOG,
                            onClick = { viewModel.navigateTo(NavigationScreen.CATALOG) },
                            icon = Icons.Default.Explore,
                            label = shellStrings.routesTab
                        )
                        AppNavigationBarItem(
                            selected = state.screen == NavigationScreen.MAP,
                            onClick = viewModel::openMapTab,
                            icon = Icons.Default.Map,
                            label = shellStrings.mapTab
                        )
                        AppNavigationBarItem(
                            selected = state.screen == NavigationScreen.JOURNAL,
                            onClick = { viewModel.navigateTo(NavigationScreen.JOURNAL) },
                            icon = Icons.Default.AutoStories,
                            label = shellStrings.journalTab
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = state.screen) { screen ->
                    when (screen) {
                        NavigationScreen.RESTORING_SESSION -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        NavigationScreen.WELCOME -> {
                            WelcomeScreen(
                                onSignInWithGoogle = {
                                    viewModel.beginGoogleSignIn(NavigationScreen.CHARACTER_SELECTION)
                                },
                                isSignInLoading = state.googleSignInLoadingFor == NavigationScreen.CHARACTER_SELECTION,
                                isGuideSignInLoading = state.googleSignInLoadingFor == NavigationScreen.GUIDE_DASHBOARD,
                                signInError = state.googleSignInError,
                                guideSignInError = state.guideSignInError,
                                language = appLanguage.current,
                                availableRoutesCount = state.routes.size,
                                activeGuidesCount = state.routes.map { it.guide.id }.distinct().size,
                                completedChallengesCount = state.completedChallengesCount,
                                onOpenGuideMode = {
                                    viewModel.beginGoogleSignIn(NavigationScreen.GUIDE_DASHBOARD)
                                }
                            )
                        }
                        NavigationScreen.GUIDE_WELCOME -> {
                            GuideWelcomeScreen(
                                language = appLanguage.current,
                                onContinue = { viewModel.navigateTo(NavigationScreen.GUIDE_PROFILE_SETUP) },
                                onBack = { viewModel.navigateTo(NavigationScreen.WELCOME) }
                            )
                        }
                        NavigationScreen.GUIDE_PROFILE_SETUP -> {
                            GuideProfileSetupScreen(
                                language = appLanguage.current,
                                initialName = state.displayNameDraft,
                                isSaving = state.isSavingDisplayName,
                                errorMessage = state.displayNameError,
                                onBack = { viewModel.navigateTo(NavigationScreen.GUIDE_WELCOME) },
                                onSave = viewModel::saveGuideProfile
                            )
                        }
                        NavigationScreen.DISPLAY_NAME -> {
                            DisplayNameScreen(
                                initialName = state.displayNameDraft,
                                strings = welcomeStrings,
                                isSaving = state.isSavingDisplayName,
                                errorMessage = state.displayNameError,
                                onNameChanged = viewModel::onDisplayNameDraftChanged,
                                onContinue = { viewModel.submitDisplayName(welcomeStrings.displayNameRequired) }
                            )
                        }
                        NavigationScreen.CHARACTER_SELECTION -> {
                            CharacterSelectionScreen(
                                currentCharacterId = userProfile.characterId,
                                language = appLanguage.current,
                                onSelectCharacter = { viewModel.selectCharacter(it.id) },
                                onConfirm = { viewModel.confirmCharacter(it.id) },
                                onBack = viewModel::cancelCharacterSelection
                            )
                        }
                        NavigationScreen.CATALOG -> {
                            val publishedRoutes = remember(state.routes) {
                                state.routes.filter { it.status == "publicada" }
                            }
                            RoutesCatalogScreen(
                                routes = publishedRoutes,
                                savedRouteIds = userProfile.savedRouteIds,
                                userProfile = userProfile,
                                language = appLanguage.current,
                                onSelectRoute = viewModel::openRoute,
                                onToggleSaved = viewModel::toggleSavedRoute,
                                onOpenProfile = { viewModel.navigateTo(NavigationScreen.JOURNAL) },
                                onOpenGuideMode = { viewModel.navigateTo(NavigationScreen.GUIDE_DASHBOARD) }
                            )
                        }
                        NavigationScreen.DETAIL -> {
                            state.selectedRoute?.let { route ->
                                RouteDetailScreen(
                                    route = route,
                                    language = appLanguage.current,
                                    isSaved = route.id in userProfile.savedRouteIds,
                                    onBack = { viewModel.navigateTo(NavigationScreen.CATALOG) },
                                    onToggleSaved = viewModel::toggleSavedRoute,
                                    onOpenMap = viewModel::openMapForSelectedRoute,
                                    onStartRoute = viewModel::startExpedition,
                                    onOpenProfile = { viewModel.navigateTo(NavigationScreen.JOURNAL) },
                                    onOpenGuideProfile = viewModel::openGuideProfile
                                )
                            }
                        }
                        NavigationScreen.GUIDE_PROFILE -> {
                            state.selectedGuide?.let { guide ->
                                GuideProfileScreen(
                                    guide = guide,
                                    language = appLanguage.current,
                                    allRoutes = state.routes,
                                    reviews = state.routeReviews.filter { it.guideId == guide.id },
                                    onBack = { viewModel.navigateTo(NavigationScreen.DETAIL) },
                                    onSelectRoute = viewModel::openRouteById
                                )
                            }
                        }
                        NavigationScreen.EXPEDITION -> {
                            state.selectedRoute?.let { route ->
                                ActiveExpeditionScreen(
                                    route = route,
                                    userProfile = userProfile,
                                    initialStopId = state.activeExpeditionStopId,
                                    pointsGain = state.pointsGain,
                                    onPointsGainShown = viewModel::consumePointsGain,
                                    routeBonusGain = state.routeBonusGain,
                                    onRouteBonusShown = viewModel::consumeRouteBonusGain,
                                    language = appLanguage.current,
                                    onBack = viewModel::backToExpeditionMap,
                                    onSolveEnigma = viewModel::solveEnigma,
                                    onSubmitReview = viewModel::submitReview
                                )
                            }
                        }
                        NavigationScreen.MAP -> {
                            InteractiveMapScreen(
                                routes = state.routes,
                                language = appLanguage.current,
                                initialRouteId = state.mapRouteToFocus,
                                expeditionMode = state.mapIsExpedition,
                                solvedStopIds = userProfile.solvedEnigmaIds,
                                triggeredStopIds = state.triggeredStopIds,
                                totalScore = state.selectedRoute?.let { userProfile.routeScore(it) } ?: 0,
                                onBack = viewModel::closeMap,
                                onSelectRoute = viewModel::openRoute,
                                onStopReached = viewModel::onStopReached
                            )
                        }
                        NavigationScreen.JOURNAL -> {
                            val myReviews = remember(state.routeReviews, userProfile.googleId) {
                                state.routeReviews.filter { it.userId == userProfile.googleId }
                            }
                            JournalScreen(
                                userProfile = userProfile,
                                routes = state.routes,
                                myReviews = myReviews,
                                language = appLanguage.current,
                                languageTitle = welcomeStrings.languageTitle,
                                onLanguageChange = appLanguage::select,
                                onBack = { viewModel.navigateTo(NavigationScreen.CATALOG) },
                                onOpenRoute = viewModel::openRouteById,
                                onSaveDisplayName = viewModel::saveDisplayNameFromJournal,
                                onOpenCharacterSelection = {
                                    viewModel.openCharacterSelection(NavigationScreen.JOURNAL)
                                },
                                onSignOut = viewModel::signOut
                            )
                        }
                        NavigationScreen.GUIDE_DASHBOARD -> {
                            val guideRoutes = remember(state.routes, userProfile.googleId) {
                                state.routes.filter { it.guide.id == userProfile.googleId }
                            }
                            val currentGuide = remember(
                                guideRoutes, state.routeReviews, state.guideProfiles, userProfile.googleId, userProfile.name
                            ) {
                                guideRoutes.firstOrNull()?.guide ?: enrichGuide(
                                    GuideAuthor(
                                        id = userProfile.googleId.orEmpty(),
                                        name = userProfile.name,
                                        title = shellStrings.guideTitle,
                                        bio = "",
                                        rating = 0.0,
                                        reviewsCount = 0,
                                        location = ""
                                    ),
                                    state.routeReviews,
                                    state.guideProfiles
                                )
                            }
                            GuideDashboardScreen(
                                guide = currentGuide,
                                language = appLanguage.current,
                                routes = guideRoutes,
                                reviews = state.routeReviews.filter { it.guideId == userProfile.googleId },
                                isSaving = state.isSavingRoute,
                                onBack = { viewModel.navigateTo(NavigationScreen.CATALOG) },
                                onCreateNewRoute = viewModel::createNewRoute,
                                onEditRoute = viewModel::editRoute,
                                onPublishDraft = viewModel::publishDraft
                            )
                        }
                        NavigationScreen.ROUTE_EDITOR -> {
                            RouteEditorScreen(
                                userProfile = userProfile,
                                initialRoute = state.editingRoute,
                                language = appLanguage.current,
                                isSaving = state.isSavingRoute,
                                onBack = { viewModel.navigateTo(NavigationScreen.GUIDE_DASHBOARD) },
                                onSave = viewModel::saveEditedRoute
                            )
                        }
                    }
                }
            }
        }
    }
}