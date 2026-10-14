package com.ecosdolugar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import com.ecosdolugar.auth.GoogleAuthResult
import com.ecosdolugar.auth.GoogleAuthService
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.data.RouteDataSource
import com.ecosdolugar.data.RoutesRepository
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.ui.screens.*
import com.ecosdolugar.ui.theme.EcosDoLugarTheme
import com.ecosdolugar.ui.theme.PineDeep
import com.ecosdolugar.ui.theme.SurfaceCream

enum class NavigationScreen {
    WELCOME,
    CHARACTER_SELECTION,
    CATALOG,
    DETAIL,
    EXPEDITION,
    MAP,
    JOURNAL,
    GUIDE_DASHBOARD,
    ROUTE_EDITOR
}

@Composable
fun App(
    googleAuthService: GoogleAuthService,
    routeDataSource: RouteDataSource
) {
    EcosDoLugarTheme {
        var currentScreen by remember { mutableStateOf(NavigationScreen.WELCOME) }
        var routes by remember { mutableStateOf(RoutesRepository.routes) }
        var selectedRoute by remember { mutableStateOf<RouteItem>(routes.first()) }
        var editingRoute by remember { mutableStateOf<RouteItem?>(null) }
        var userProfile by remember { mutableStateOf(UserProfile()) }
        var accessToken by remember { mutableStateOf<String?>(null) }
        var activeBottomTab by remember { mutableStateOf(0) }
        var isGoogleSignInLoading by remember { mutableStateOf(false) }
        var googleSignInError by remember { mutableStateOf<String?>(null) }
        var dataError by remember { mutableStateOf<String?>(null) }
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(accessToken) {
            try {
                val remoteRoutes = routeDataSource.loadRoutes(accessToken)
                val completedChallengeIds = accessToken
                    ?.let { routeDataSource.loadCompletedChallenges(it) }
                    .orEmpty()
                val completedStopIds = remoteRoutes
                    .flatMap { it.stops }
                    .filter { it.challengeId in completedChallengeIds }
                    .map { it.id }
                val remoteIds = remoteRoutes.mapTo(mutableSetOf()) { it.id }
                val localRoutes = RoutesRepository.routes.filterNot { it.id in remoteIds }
                val mergedRoutes = remoteRoutes + localRoutes
                routes = mergedRoutes
                selectedRoute = remoteRoutes.firstOrNull { it.id == selectedRoute.id }
                    ?: mergedRoutes.firstOrNull { it.id == selectedRoute.id }
                    ?: remoteRoutes.firstOrNull()
                    ?: mergedRoutes.first()
                userProfile = userProfile.copy(
                    solvedEnigmaIds = userProfile.solvedEnigmaIds + completedStopIds
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                dataError = error.message ?: "No se pudieron cargar las rutas de Supabase."
            }
        }

        LaunchedEffect(dataError) {
            val message = dataError ?: return@LaunchedEffect
            snackbarHostState.showSnackbar(message)
            dataError = null
        }

        val showBottomBar = currentScreen == NavigationScreen.CATALOG ||
            currentScreen == NavigationScreen.MAP ||
            currentScreen == NavigationScreen.JOURNAL

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = SurfaceCream,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = activeBottomTab == 0 && currentScreen == NavigationScreen.CATALOG,
                            onClick = {
                                activeBottomTab = 0
                                currentScreen = NavigationScreen.CATALOG
                            },
                            icon = { Icon(Icons.Default.Explore, contentDescription = "Rutas") },
                            label = { Text("Rutas", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PineDeep.copy(alpha = 0.12f),
                                selectedIconColor = PineDeep,
                                selectedTextColor = PineDeep
                            )
                        )
                        NavigationBarItem(
                            selected = activeBottomTab == 1 && currentScreen == NavigationScreen.MAP,
                            onClick = {
                                activeBottomTab = 1
                                currentScreen = NavigationScreen.MAP
                            },
                            icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                            label = { Text("Mapa", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PineDeep.copy(alpha = 0.12f),
                                selectedIconColor = PineDeep,
                                selectedTextColor = PineDeep
                            )
                        )
                        NavigationBarItem(
                            selected = activeBottomTab == 2 && currentScreen == NavigationScreen.JOURNAL,
                            onClick = {
                                activeBottomTab = 2
                                currentScreen = NavigationScreen.JOURNAL
                            },
                            icon = { Icon(Icons.Default.AutoStories, contentDescription = "Cuaderno") },
                            label = { Text("Cuaderno", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PineDeep.copy(alpha = 0.12f),
                                selectedIconColor = PineDeep,
                                selectedTextColor = PineDeep
                            )
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
                Crossfade(targetState = currentScreen) { screen ->
                    when (screen) {
                        NavigationScreen.WELCOME -> {
                            WelcomeScreen(
                                onSignInWithGoogle = {
                                    if (!isGoogleSignInLoading) {
                                        coroutineScope.launch {
                                            isGoogleSignInLoading = true
                                            googleSignInError = null
                                            try {
                                                when (val result = googleAuthService.signInWithGoogle()) {
                                                    is GoogleAuthResult.Success -> {
                                                        accessToken = result.accessToken
                                                        userProfile = userProfile.copy(
                                                            name = result.displayName.ifBlank { result.email },
                                                            email = result.email,
                                                            isVisitor = false,
                                                            isGoogleAuthenticated = true,
                                                            googleId = result.googleId
                                                        )
                                                        currentScreen = NavigationScreen.CHARACTER_SELECTION
                                                    }
                                                    is GoogleAuthResult.Error -> {
                                                        googleSignInError = result.message
                                                    }
                                                    GoogleAuthResult.Cancelled -> Unit
                                                }
                                            } finally {
                                                isGoogleSignInLoading = false
                                            }
                                        }
                                    }
                                },
                                isSignInLoading = isGoogleSignInLoading,
                                signInError = googleSignInError,
                                onOpenGuideMode = {
                                    currentScreen = NavigationScreen.GUIDE_DASHBOARD
                                },
                                onOpenMap = {
                                    activeBottomTab = 1
                                    currentScreen = NavigationScreen.MAP
                                }
                            )
                        }
                        NavigationScreen.CHARACTER_SELECTION -> {
                            CharacterSelectionScreen(
                                currentCharacterId = userProfile.characterId,
                                userEmail = if (userProfile.isGoogleAuthenticated) userProfile.email else null,
                                onSelectCharacter = { charItem ->
                                    userProfile = userProfile.copy(characterId = charItem.id)
                                },
                                onConfirm = { charItem ->
                                    userProfile = userProfile.copy(characterId = charItem.id)
                                    currentScreen = NavigationScreen.CATALOG
                                },
                                onBack = {
                                    currentScreen = NavigationScreen.CATALOG
                                }
                            )
                        }
                        NavigationScreen.CATALOG -> {
                            RoutesCatalogScreen(
                                routes = routes.filter { it.status == "publicada" },
                                savedRouteIds = userProfile.savedRouteIds,
                                userProfile = userProfile,
                                onSelectRoute = { route ->
                                    selectedRoute = route
                                    currentScreen = NavigationScreen.DETAIL
                                },
                                onToggleSaved = { routeId ->
                                    val newSaved = if (userProfile.savedRouteIds.contains(routeId)) {
                                        userProfile.savedRouteIds - routeId
                                    } else {
                                        userProfile.savedRouteIds + routeId
                                    }
                                    userProfile = userProfile.copy(savedRouteIds = newSaved)
                                },
                                onOpenProfile = {
                                    currentScreen = NavigationScreen.JOURNAL
                                },
                                onOpenCharacterSelection = {
                                    currentScreen = NavigationScreen.CHARACTER_SELECTION
                                },
                                onOpenGuideMode = {
                                    currentScreen = NavigationScreen.GUIDE_DASHBOARD
                                }
                            )
                        }
                        NavigationScreen.DETAIL -> {
                            RouteDetailScreen(
                                route = selectedRoute,
                                isSaved = userProfile.savedRouteIds.contains(selectedRoute.id),
                                onBack = { currentScreen = NavigationScreen.CATALOG },
                                onToggleSaved = { routeId ->
                                    val newSaved = if (userProfile.savedRouteIds.contains(routeId)) {
                                        userProfile.savedRouteIds - routeId
                                    } else {
                                        userProfile.savedRouteIds + routeId
                                    }
                                    userProfile = userProfile.copy(savedRouteIds = newSaved)
                                },
                                onOpenMap = {
                                    activeBottomTab = 1
                                    currentScreen = NavigationScreen.MAP
                                },
                                onStartRoute = { route ->
                                    selectedRoute = route
                                    currentScreen = NavigationScreen.EXPEDITION
                                },
                                onOpenProfile = {
                                    currentScreen = NavigationScreen.JOURNAL
                                }
                            )
                        }
                        NavigationScreen.EXPEDITION -> {
                            ActiveExpeditionScreen(
                                route = selectedRoute,
                                userProfile = userProfile,
                                onBack = { currentScreen = NavigationScreen.DETAIL },
                                onCheckInStop = { stopId ->
                                    userProfile = userProfile.copy(
                                        visitedStopIds = userProfile.visitedStopIds + stopId
                                    )
                                },
                                onSolveEnigma = { stopId, attempts ->
                                    userProfile = userProfile.copy(
                                        solvedEnigmaIds = userProfile.solvedEnigmaIds + stopId
                                    )
                                    val challengeId = selectedRoute.stops
                                        .firstOrNull { it.id == stopId }
                                        ?.challengeId
                                    val token = accessToken
                                    if (challengeId != null && token != null) {
                                        coroutineScope.launch {
                                            try {
                                                routeDataSource.saveChallengeProgress(
                                                    challengeId = challengeId,
                                                    attempts = attempts,
                                                    score = 100,
                                                    completed = true,
                                                    accessToken = token
                                                )
                                            } catch (error: CancellationException) {
                                                throw error
                                            } catch (error: Exception) {
                                                dataError = error.message
                                                    ?: "No se pudo sincronizar el progreso del reto."
                                            }
                                        }
                                    }
                                },
                                onAddNote = {
                                    // Add to field notes
                                },
                                onCompleteRoute = { routeId ->
                                    userProfile = userProfile.copy(
                                        completedRouteIds = userProfile.completedRouteIds + routeId
                                    )
                                    currentScreen = NavigationScreen.JOURNAL
                                }
                            )
                        }
                        NavigationScreen.MAP -> {
                            InteractiveMapScreen(
                                routes = routes,
                                onSelectRoute = { route ->
                                    selectedRoute = route
                                    currentScreen = NavigationScreen.DETAIL
                                }
                            )
                        }
                        NavigationScreen.JOURNAL -> {
                            JournalScreen(
                                userProfile = userProfile,
                                onBack = { currentScreen = NavigationScreen.CATALOG },
                                onOpenRoute = { routeId ->
                                    val r = routes.find { it.id == routeId }
                                    if (r != null) {
                                        selectedRoute = r
                                        currentScreen = NavigationScreen.DETAIL
                                    }
                                },
                                onSignOut = {
                                    coroutineScope.launch {
                                        try {
                                            googleAuthService.signOut(accessToken)
                                            accessToken = null
                                            userProfile = userProfile.copy(
                                                isVisitor = true,
                                                isGoogleAuthenticated = false,
                                                googleId = null
                                            )
                                            currentScreen = NavigationScreen.WELCOME
                                        } catch (error: CancellationException) {
                                            throw error
                                        } catch (error: Exception) {
                                            dataError = error.message ?: "No se pudo cerrar sesión."
                                        }
                                    }
                                }
                            )
                        }
                        NavigationScreen.GUIDE_DASHBOARD -> {
                            GuideDashboardScreen(
                                routes = routes.filter { it.guide.id == userProfile.googleId },
                                onBack = { currentScreen = NavigationScreen.CATALOG },
                                onCreateNewRoute = {
                                    if (accessToken == null) {
                                        dataError = "Inicia sesión con Google antes de crear una ruta."
                                        currentScreen = NavigationScreen.WELCOME
                                    } else {
                                        editingRoute = null
                                        currentScreen = NavigationScreen.ROUTE_EDITOR
                                    }
                                },
                                onEditDraft = { draft ->
                                    editingRoute = draft
                                    currentScreen = NavigationScreen.ROUTE_EDITOR
                                },
                                onPublishDraft = { draft ->
                                    val token = accessToken
                                    if (token == null) {
                                        dataError = "La sesión ha caducado. Inicia sesión de nuevo."
                                    } else {
                                        coroutineScope.launch {
                                            try {
                                                val published = routeDataSource.saveRoute(
                                                    draft,
                                                    "publicada",
                                                    token
                                                )
                                                routes = listOf(published) + routes.filterNot { it.id == published.id }
                                                currentScreen = NavigationScreen.GUIDE_DASHBOARD
                                            } catch (error: CancellationException) {
                                                throw error
                                            } catch (error: Exception) {
                                                dataError = error.message ?: "No se pudo publicar el borrador."
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        NavigationScreen.ROUTE_EDITOR -> {
                            RouteEditorScreen(
                                userProfile = userProfile,
                                initialRoute = editingRoute,
                                onBack = { currentScreen = NavigationScreen.GUIDE_DASHBOARD },
                                onSave = { route, status ->
                                    val token = accessToken
                                    if (token == null) {
                                        dataError = "La sesión ha caducado. Inicia sesión de nuevo."
                                    } else {
                                        coroutineScope.launch {
                                            try {
                                                val savedRoute = routeDataSource.saveRoute(route, status, token)
                                                routes = listOf(savedRoute) + routes.filterNot { it.id == savedRoute.id }
                                                if (savedRoute.status == "publicada") {
                                                    selectedRoute = savedRoute
                                                    currentScreen = NavigationScreen.DETAIL
                                                } else {
                                                    currentScreen = NavigationScreen.GUIDE_DASHBOARD
                                                }
                                            } catch (error: CancellationException) {
                                                throw error
                                            } catch (error: Exception) {
                                                dataError = error.message ?: "No se pudo guardar la ruta."
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
