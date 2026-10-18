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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.ecosdolugar.auth.GoogleAuthResult
import com.ecosdolugar.auth.GoogleAuthService
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.data.RouteDataSource
import com.ecosdolugar.data.RoutesRepository
import com.ecosdolugar.localization.appLanguageStrings
import com.ecosdolugar.localization.rememberAppLanguageController
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.UserProfile
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
    EXPEDITION,
    MAP,
    JOURNAL,
    DISPLAY_NAME,
    GUIDE_DASHBOARD,
    ROUTE_EDITOR
}

@Composable
fun App(
    googleAuthService: GoogleAuthService,
    routeDataSource: RouteDataSource
) {
    EcosDoLugarTheme {
        var currentScreen by remember { mutableStateOf(NavigationScreen.RESTORING_SESSION) }
        var routes by remember { mutableStateOf(RoutesRepository.routes) }
        var selectedRoute by remember { mutableStateOf<RouteItem>(routes.first()) }
        var mapRouteToFocus by remember { mutableStateOf<String?>(null) }
        var mapReturnScreen by remember { mutableStateOf(NavigationScreen.CATALOG) }
        var mapIsExpedition by remember { mutableStateOf(false) }
        var activeExpeditionStopId by remember { mutableStateOf<String?>(null) }
        var editingRoute by remember { mutableStateOf<RouteItem?>(null) }
        var userProfile by remember { mutableStateOf(UserProfile()) }
        var accessToken by remember { mutableStateOf<String?>(null) }
        var activeBottomTab by remember { mutableStateOf(0) }
        var googleSignInLoadingFor by remember { mutableStateOf<NavigationScreen?>(null) }
        var displayNameDraft by remember { mutableStateOf("") }
        var isSavingDisplayName by remember { mutableStateOf(false) }
        var displayNameError by remember { mutableStateOf<String?>(null) }
        var pendingAuthenticatedScreen by remember {
            mutableStateOf(NavigationScreen.CHARACTER_SELECTION)
        }
        var googleSignInError by remember { mutableStateOf<String?>(null) }
        var guideSignInError by remember { mutableStateOf<String?>(null) }
        var dataError by remember { mutableStateOf<String?>(null) }
        val appLanguage = rememberAppLanguageController()
        val welcomeStrings = appLanguageStrings(appLanguage.current)
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val savedRoutesMutex = remember { Mutex() }

        fun toggleSavedRoute(routeId: String) {
            val currentSaved = userProfile.savedRouteIds
            val nextSaved = if (routeId in currentSaved) currentSaved - routeId else currentSaved + routeId
            userProfile = userProfile.copy(savedRouteIds = nextSaved)
            val token = accessToken
            val userId = userProfile.googleId
            if (!token.isNullOrBlank() && !userId.isNullOrBlank()) {
                coroutineScope.launch {
                    try {
                        savedRoutesMutex.withLock {
                            routeDataSource.saveSavedRouteIds(userId, userProfile.name, nextSaved, token)
                        }
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Exception) {
                        dataError = error.message ?: "No se pudieron sincronizar las rutas guardadas."
                    }
                }
            }
        }

        fun beginGoogleSignIn(destination: NavigationScreen) {
            if (googleSignInLoadingFor != null) return
            pendingAuthenticatedScreen = destination
            if (destination == NavigationScreen.GUIDE_DASHBOARD) {
                guideSignInError = null
            } else {
                googleSignInError = null
            }
            coroutineScope.launch {
                googleSignInLoadingFor = destination
                try {
                    when (val result = googleAuthService.signInWithGoogle()) {
                        is GoogleAuthResult.Success -> {
                            val isGuideAccount =
                                destination == NavigationScreen.GUIDE_DASHBOARD || result.isGuideAccount
                            if (destination == NavigationScreen.GUIDE_DASHBOARD) {
                                try {
                                    googleAuthService.setGuideAccount(true)
                                } catch (error: Exception) {
                                    dataError = error.message ?: "No se pudo guardar el acceso de guía."
                                    currentScreen = NavigationScreen.WELCOME
                                    return@launch
                                }
                            }
                            accessToken = result.accessToken
                            userProfile = userProfile.copy(
                                name = result.displayName.ifBlank { result.email },
                                email = result.email,
                                isVisitor = false,
                                isGoogleAuthenticated = true,
                                isGuideAccount = isGuideAccount,
                                googleId = result.googleId,
                                characterId = result.characterId ?: userProfile.characterId
                            )
                            val displayName = result.displayName.ifBlank {
                                result.email.substringBefore('@')
                            }
                            displayNameDraft = displayName
                            displayNameError = null
                            currentScreen = when {
                                result.profileComplete -> if (
                                    destination == NavigationScreen.GUIDE_DASHBOARD
                                ) NavigationScreen.GUIDE_DASHBOARD else NavigationScreen.CATALOG
                                result.displayNameSet && destination == NavigationScreen.GUIDE_DASHBOARD -> {
                                    val characterId = result.characterId ?: userProfile.characterId
                                    try {
                                        googleAuthService.saveProfile(displayName, characterId)
                                        userProfile = userProfile.copy(characterId = characterId)
                                        NavigationScreen.GUIDE_DASHBOARD
                                    } catch (error: Exception) {
                                        dataError = error.message ?: "No se pudo guardar el perfil."
                                        NavigationScreen.DISPLAY_NAME
                                    }
                                }
                                result.displayNameSet -> NavigationScreen.CHARACTER_SELECTION
                                else -> NavigationScreen.DISPLAY_NAME
                            }
                        }
                        is GoogleAuthResult.Error -> {
                            if (destination == NavigationScreen.GUIDE_DASHBOARD) {
                                guideSignInError = result.message
                            } else {
                                googleSignInError = result.message
                            }
                        }
                        GoogleAuthResult.Cancelled -> Unit
                        GoogleAuthResult.NoStoredSession -> Unit
                    }
                } finally {
                    googleSignInLoadingFor = null
                }
            }
        }

        LaunchedEffect(Unit) {
            when (val result = googleAuthService.restoreSession()) {
                is GoogleAuthResult.Success -> {
                    accessToken = result.accessToken
                    userProfile = userProfile.copy(
                        name = result.displayName,
                        email = result.email,
                        isVisitor = false,
                        isGoogleAuthenticated = true,
                        isGuideAccount = result.isGuideAccount,
                        googleId = result.googleId,
                        characterId = result.characterId ?: userProfile.characterId
                    )
                    currentScreen = when {
                        result.profileComplete -> NavigationScreen.CATALOG
                        result.displayNameSet -> NavigationScreen.CHARACTER_SELECTION
                        else -> NavigationScreen.DISPLAY_NAME
                    }
                }
                is GoogleAuthResult.Error -> {
                    googleSignInError = result.message
                    currentScreen = NavigationScreen.WELCOME
                }
                GoogleAuthResult.NoStoredSession,
                GoogleAuthResult.Cancelled -> currentScreen = NavigationScreen.WELCOME
            }
        }

        LaunchedEffect(accessToken) {
            try {
                val remoteRoutes = routeDataSource.loadRoutes(accessToken)
                val savedRouteIds = if (!accessToken.isNullOrBlank() &&
                    !userProfile.googleId.isNullOrBlank()
                ) {
                    routeDataSource.loadSavedRouteIds(userProfile.googleId!!, accessToken!!)
                } else {
                    null
                }
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
                    solvedEnigmaIds = userProfile.solvedEnigmaIds + completedStopIds,
                    savedRouteIds = savedRouteIds ?: userProfile.savedRouteIds
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
            (currentScreen == NavigationScreen.MAP && !mapIsExpedition) ||
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
                                mapRouteToFocus = null
                                mapReturnScreen = NavigationScreen.CATALOG
                                mapIsExpedition = false
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
                        NavigationScreen.RESTORING_SESSION -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        NavigationScreen.WELCOME -> {
                            WelcomeScreen(
                                onSignInWithGoogle = {
                                    beginGoogleSignIn(NavigationScreen.CHARACTER_SELECTION)
                                },
                                isSignInLoading = googleSignInLoadingFor == NavigationScreen.CHARACTER_SELECTION,
                                isGuideSignInLoading = googleSignInLoadingFor == NavigationScreen.GUIDE_DASHBOARD,
                                signInError = googleSignInError,
                                guideSignInError = guideSignInError,
                                language = appLanguage.current,
                                onOpenGuideMode = {
                                    beginGoogleSignIn(NavigationScreen.GUIDE_DASHBOARD)
                                }
                            )
                        }
                        NavigationScreen.DISPLAY_NAME -> {
                            DisplayNameScreen(
                                initialName = displayNameDraft,
                                strings = welcomeStrings,
                                isSaving = isSavingDisplayName,
                                errorMessage = displayNameError,
                                onNameChanged = { displayNameDraft = it },
                                onContinue = {
                                    val name = displayNameDraft.trim()
                                    if (name.isEmpty() || name.length > 60) {
                                        displayNameError = welcomeStrings.displayNameRequired
                                    } else if (!isSavingDisplayName) {
                                        val token = accessToken
                                        val userId = userProfile.googleId
                                        if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                                            displayNameError = "La sesión de Google ha caducado. Inicia sesión de nuevo."
                                        } else {
                                            coroutineScope.launch {
                                                isSavingDisplayName = true
                                                displayNameError = null
                                                try {
                                                    routeDataSource.saveDisplayName(userId, name, token)
                                                    googleAuthService.saveDisplayName(name)
                                                    userProfile = userProfile.copy(name = name)
                                                    if (pendingAuthenticatedScreen == NavigationScreen.GUIDE_DASHBOARD) {
                                                        googleAuthService.saveProfile(name, userProfile.characterId)
                                                        currentScreen = NavigationScreen.GUIDE_DASHBOARD
                                                    } else {
                                                        currentScreen = NavigationScreen.CHARACTER_SELECTION
                                                    }
                                                } catch (error: CancellationException) {
                                                    throw error
                                                } catch (error: Exception) {
                                                    displayNameError = error.message
                                                        ?: "No se pudo guardar el nombre en tu perfil."
                                                } finally {
                                                    isSavingDisplayName = false
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        NavigationScreen.CHARACTER_SELECTION -> {
                            CharacterSelectionScreen(
                                currentCharacterId = userProfile.characterId,
                                language = appLanguage.current,
                                onSelectCharacter = { charItem ->
                                    userProfile = userProfile.copy(characterId = charItem.id)
                                },
                                onConfirm = { charItem ->
                                    coroutineScope.launch {
                                        try {
                                            googleAuthService.saveProfile(userProfile.name, charItem.id)
                                            userProfile = userProfile.copy(characterId = charItem.id)
                                            currentScreen = NavigationScreen.CATALOG
                                        } catch (error: CancellationException) {
                                            throw error
                                        } catch (error: Exception) {
                                            dataError = error.message ?: "No se pudo guardar tu personaje."
                                        }
                                    }
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
                                language = appLanguage.current,
                                onSelectRoute = { route ->
                                    selectedRoute = route
                                    currentScreen = NavigationScreen.DETAIL
                                },
                                onToggleSaved = { routeId ->
                                    toggleSavedRoute(routeId)
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
                                    toggleSavedRoute(routeId)
                                },
                                onOpenMap = {
                                    activeBottomTab = 1
                                    mapRouteToFocus = selectedRoute.id
                                    mapReturnScreen = NavigationScreen.DETAIL
                                    mapIsExpedition = false
                                    currentScreen = NavigationScreen.MAP
                                },
                                onStartRoute = { route ->
                                    selectedRoute = route
                                    activeExpeditionStopId = null
                                    mapRouteToFocus = route.id
                                    mapReturnScreen = NavigationScreen.DETAIL
                                    mapIsExpedition = true
                                    currentScreen = NavigationScreen.MAP
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
                                initialStopId = activeExpeditionStopId,
                                onBack = {
                                    mapRouteToFocus = selectedRoute.id
                                    mapReturnScreen = NavigationScreen.DETAIL
                                    mapIsExpedition = true
                                    currentScreen = NavigationScreen.MAP
                                },
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
                                language = appLanguage.current,
                                initialRouteId = mapRouteToFocus,
                                expeditionMode = mapIsExpedition,
                                solvedStopIds = userProfile.solvedEnigmaIds,
                                onBack = {
                                    mapIsExpedition = false
                                    currentScreen = mapReturnScreen
                                },
                                onSelectRoute = { route ->
                                    selectedRoute = route
                                    currentScreen = NavigationScreen.DETAIL
                                },
                                onStopReached = { stop ->
                                    activeExpeditionStopId = stop.id
                                    userProfile = userProfile.copy(
                                        visitedStopIds = userProfile.visitedStopIds + stop.id
                                    )
                                    currentScreen = NavigationScreen.EXPEDITION
                                }
                            )
                        }
                        NavigationScreen.JOURNAL -> {
                            JournalScreen(
                                userProfile = userProfile,
                                routes = routes,
                                language = appLanguage.current,
                                languageTitle = welcomeStrings.languageTitle,
                                onLanguageChange = appLanguage::select,
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
                                                isGuideAccount = false,
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
                                onEditRoute = { route ->
                                    editingRoute = route
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
                                language = appLanguage.current,
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
