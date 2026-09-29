package com.ecosdolugar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.RoutesRepository
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.ui.screens.CharacterSelectionScreen
import com.ecosdolugar.ui.screens.RouteDetailScreen
import com.ecosdolugar.ui.screens.RoutesCatalogScreen
import com.ecosdolugar.ui.screens.WelcomeScreen
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
    JOURNAL
}

@Composable
fun App() {
    EcosDoLugarTheme {
        var currentScreen by remember { mutableStateOf(NavigationScreen.WELCOME) }
        var selectedRoute by remember { mutableStateOf<RouteItem>(RoutesRepository.routes.first()) }
        var userProfile by remember { mutableStateOf(UserProfile()) }
        var activeBottomTab by remember { mutableStateOf(0) }

        Scaffold(
            bottomBar = {
                if (currentScreen == NavigationScreen.CATALOG) {
                    NavigationBar(
                        containerColor = SurfaceCream,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = activeBottomTab == 0,
                            onClick = { activeBottomTab = 0 },
                            icon = { Icon(Icons.Default.Explore, contentDescription = "Rutas") },
                            label = { Text("Rutas", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PineDeep.copy(alpha = 0.12f),
                                selectedIconColor = PineDeep,
                                selectedTextColor = PineDeep
                            )
                        )
                        NavigationBarItem(
                            selected = activeBottomTab == 1,
                            onClick = { activeBottomTab = 1 },
                            icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                            label = { Text("Mapa", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PineDeep.copy(alpha = 0.12f),
                                selectedIconColor = PineDeep,
                                selectedTextColor = PineDeep
                            )
                        )
                        NavigationBarItem(
                            selected = activeBottomTab == 2,
                            onClick = { activeBottomTab = 2 },
                            icon = { Icon(Icons.Default.AutoStories, contentDescription = "Mi aventura") },
                            label = { Text("Mi aventura", fontSize = 11.sp) },
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
                                    userProfile = userProfile.copy(
                                        name = "Explorador da Raia",
                                        email = "bert2424.b2@gmail.com",
                                        isVisitor = false,
                                        isGoogleAuthenticated = true,
                                        googleId = "google_user_89127"
                                    )
                                    // Proceed to choose character after Google Sign-In
                                    currentScreen = NavigationScreen.CHARACTER_SELECTION
                                },
                                onEnterGuest = {
                                    userProfile = userProfile.copy(isVisitor = true)
                                    currentScreen = NavigationScreen.CATALOG
                                },
                                onOpenCharacterSelect = {
                                    currentScreen = NavigationScreen.CHARACTER_SELECTION
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
                                    currentScreen = NavigationScreen.WELCOME
                                }
                            )
                        }
                        NavigationScreen.CATALOG -> {
                            RoutesCatalogScreen(
                                routes = RoutesRepository.routes,
                                savedRouteIds = userProfile.savedRouteIds,
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
                                    // Open Profile or switch to Welcome
                                    currentScreen = NavigationScreen.WELCOME
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
                                    // Map view
                                },
                                onStartRoute = { route ->
                                    // Start expedition mode
                                },
                                onOpenProfile = {
                                    currentScreen = NavigationScreen.WELCOME
                                }
                            )
                        }
                        else -> {
                            // Other views
                        }
                    }
                }
            }
        }
    }
}
