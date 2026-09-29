import React, { useState } from 'react';
import { X, Copy, Check, Code, FileCode, Layers, Smartphone, Globe, Laptop } from 'lucide-react';

interface KmpCodeViewerModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const KMP_FILES: { name: string; path: string; category: string; content: string }[] = [
  {
    name: 'App.kt',
    path: 'composeApp/src/commonMain/kotlin/com/ecosdolugar/App.kt',
    category: 'Core Navigation',
    content: `package com.ecosdolugar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.RoutesRepository
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.ui.screens.*
import com.ecosdolugar.ui.theme.EcosDoLugarTheme
import com.ecosdolugar.ui.theme.PineDeep
import com.ecosdolugar.ui.theme.SurfaceCream

enum class NavigationScreen {
    WELCOME, CATALOG, DETAIL, EXPEDITION, MAP, JOURNAL
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
                            label = { Text("Rutas", fontSize = 11.sp) }
                        )
                        NavigationBarItem(
                            selected = activeBottomTab == 1,
                            onClick = { activeBottomTab = 1 },
                            icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                            label = { Text("Mapa", fontSize = 11.sp) }
                        )
                        NavigationBarItem(
                            selected = activeBottomTab == 2,
                            onClick = { activeBottomTab = 2 },
                            icon = { Icon(Icons.Default.AutoStories, contentDescription = "Mi aventura") },
                            label = { Text("Mi aventura", fontSize = 11.sp) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Crossfade(targetState = currentScreen) { screen ->
                    when (screen) {
                        NavigationScreen.WELCOME -> WelcomeScreen(
                            onEnterGuest = { currentScreen = NavigationScreen.CATALOG },
                            onOpenSignUp = { currentScreen = NavigationScreen.CATALOG },
                            onOpenLogin = { currentScreen = NavigationScreen.CATALOG }
                        )
                        NavigationScreen.CATALOG -> RoutesCatalogScreen(
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
                            onOpenProfile = { currentScreen = NavigationScreen.WELCOME }
                        )
                        NavigationScreen.DETAIL -> RouteDetailScreen(
                            route = selectedRoute,
                            isSaved = userProfile.savedRouteIds.contains(selectedRoute.id),
                            onBack = { currentScreen = NavigationScreen.CATALOG },
                            onToggleSaved = { /* toggle */ },
                            onOpenMap = { /* map modal */ },
                            onStartRoute = { /* expedition */ },
                            onOpenProfile = { currentScreen = NavigationScreen.WELCOME }
                        )
                        else -> {}
                    }
                }
            }
        }
    }
}`
  },
  {
    name: 'WelcomeScreen.kt',
    path: 'composeApp/src/commonMain/kotlin/com/ecosdolugar/ui/screens/WelcomeScreen.kt',
    category: 'Screens',
    content: `package com.ecosdolugar.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.ui.theme.*

@Composable
fun WelcomeScreen(
    onEnterGuest: () -> Unit,
    onOpenSignUp: () -> Unit,
    onOpenLogin: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val compassAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(90000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(PineDeep)) {
        // Gradient Scrim
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(PineDeep.copy(0.85f), PineDeep.copy(0.45f), PineDeep)
                )
            )
        )

        // Rotating Compass Watermark
        Box(
            modifier = Modifier.size(260.dp).align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 60.dp).rotate(compassAngle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = null,
                tint = TerracottaFixedDim.copy(0.12f),
                modifier = Modifier.fillMaxSize()
            )
        }

        // Layout Content
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).padding(top = 48.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand Badge
            Surface(
                shape = CircleShape,
                color = Color.White.copy(0.12f),
                modifier = Modifier.border(1.dp, Color.White.copy(0.15f), CircleShape)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ECOS DO LUGAR", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("RUTAS & PATRIMONIO OCULTO", color = SurfaceContainerHigh, fontSize = 10.sp, letterSpacing = 1.5.sp)
                }
            }

            // Narrative Body
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Pill
                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerHighest.copy(0.20f),
                    modifier = Modifier.border(1.dp, Color.White.copy(0.12f), CircleShape)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        Text("TURISMO CULTURAL VIVO", color = TerracottaFixed, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Text(
                    text = "Haz visitable\\nlo invisible",
                    color = Color.White,
                    style = MaterialTheme.typography.displayLarge.copy(fontStyle = FontStyle.Italic),
                    lineHeight = 40.sp
                )

                Text(
                    text = "Descubre historias, leyendas y patrimonio oculto a través de rutas digitales interactivas.",
                    color = SurfaceContainerHigh,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                // 3 Micro-Metrics
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(0.10f),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(16.dp))
                ) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("RUTAS", color = SurfaceContainerHighest, fontSize = 10.sp); Text("42", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                        Column { Text("LEYENDAS", color = SurfaceContainerHighest, fontSize = 10.sp); Text("118", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                        Column { Text("FRONTERA", color = SurfaceContainerHighest, fontSize = 10.sp); Text("A Raia", color = TerracottaFixedDim, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                    }
                }

                // Buttons
                Button(
                    onClick = onOpenSignUp,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCream, contentColor = PineDeep)
                ) {
                    Text("Crear cuenta", fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onOpenLogin,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Iniciar sesión")
                }

                TextButton(onClick = onEnterGuest, modifier = Modifier.fillMaxWidth()) {
                    Text("Entrar como visitante", color = SurfaceContainerHighest, fontSize = 13.sp)
                }
            }
        }
    }
}`
  },
  {
    name: 'RoutesCatalogScreen.kt',
    path: 'composeApp/src/commonMain/kotlin/com/ecosdolugar/ui/screens/RoutesCatalogScreen.kt',
    category: 'Screens',
    content: `package com.ecosdolugar.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.model.Category
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.ui.theme.*

@Composable
fun RoutesCatalogScreen(
    routes: List<RouteItem>,
    savedRouteIds: Set<String>,
    onSelectRoute: (RouteItem) -> Unit,
    onToggleSaved: (String) -> Unit,
    onOpenProfile: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.ALL) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "GALICIA & RAIA MIÑOTA",
                style = MaterialTheme.typography.labelSmall.copy(color = PineMoss, fontWeight = FontWeight.Bold)
            )
            Text(
                "Elige tu ruta",
                style = MaterialTheme.typography.headlineLarge.copy(color = OnSurfaceDark)
            )
            Text(
                "3 experiencias narradas disponibles hoy",
                style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant)
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por hito, pueblo o misterio...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Route Cards
        items(routes) { route ->
            val isSaved = savedRouteIds.contains(route.id)
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelectRoute(route) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(route.title, style = MaterialTheme.typography.headlineSmall)
                    Text(route.description, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("\${route.duration} • \${route.stopsCount} paradas", fontSize = 12.sp)
                        Button(onClick = { onSelectRoute(route) }) {
                            Text("Ver ruta")
                        }
                    }
                }
            }
        }
    }
}`
  },
  {
    name: 'RouteDetailScreen.kt',
    path: 'composeApp/src/commonMain/kotlin/com/ecosdolugar/ui/screens/RouteDetailScreen.kt',
    category: 'Screens',
    content: `package com.ecosdolugar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.ui.theme.*

@Composable
fun RouteDetailScreen(
    route: RouteItem,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSaved: (String) -> Unit,
    onOpenMap: () -> Unit,
    onStartRoute: (RouteItem) -> Unit,
    onOpenProfile: () -> Unit
) {
    Scaffold(
        bottomBar = {
            Surface(color = SurfaceCream, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onOpenMap, modifier = Modifier.weight(1f)) {
                        Text("Ver mapa")
                    }
                    Button(onClick = { onStartRoute(route) }, modifier = Modifier.weight(1.3f)) {
                        Text("Empezar ruta")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            item {
                Text(route.region, color = PineMoss)
                Text(route.title, style = MaterialTheme.typography.headlineLarge)
                Text(route.description, style = MaterialTheme.typography.bodyMedium)
            }
            // Bento Grid Metrics
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard("DURACIÓN", route.duration, "A pie", Modifier.weight(1f))
                    MetricCard("DISTANCIA", route.distance, "Circular", Modifier.weight(1f))
                }
            }
        }
    }
}`
  },
  {
    name: 'Models.kt',
    path: 'composeApp/src/commonMain/kotlin/com/ecosdolugar/model/Models.kt',
    category: 'Data Models',
    content: `package com.ecosdolugar.model

enum class Category(val displayName: String) {
    ALL("Todas"), HISTORIA("Historia"), LEYENDAS("Leyendas"),
    FAMILIAS("Familias"), PATRIMONIO("Patrimonio"), NATURALEZA("Naturaleza")
}

enum class Difficulty(val label: String) {
    FACIL("Fácil"), MEDIA("Media"), EXIGENTE("Exigente")
}

data class FieldEnigma(
    val question: String, val hint: String, val answer: String
)

data class RouteStop(
    val id: String, val order: Int, val title: String,
    val subtitle: String, val locationName: String, val story: String,
    val historicalNote: String, val enigma: FieldEnigma,
    val audioDuration: String, val latitude: Double, val longitude: Double,
    val mapPositionX: Float, val mapPositionY: Float
)

data class RouteItem(
    val id: String, val title: String, val subtitle: String,
    val description: String, val location: String, val region: String,
    val imageUrl: String, val isFeatured: Boolean = false,
    val stopsCount: Int, val duration: String, val distance: String,
    val difficulty: Difficulty, val categories: List<Category>,
    val audioTag: String, val tomoRef: String,
    val overviewFeatures: List<OverviewFeature>,
    val stops: List<RouteStop>
)

data class UserProfile(
    val name: String = "Explorador da Raia",
    val email: String = "explorador@ecosdolugar.gal",
    val isVisitor: Boolean = true,
    val savedRouteIds: Set<String> = setOf("contrabando"),
    val visitedStopIds: Set<String> = setOf("stop-c1"),
    val solvedEnigmaIds: Set<String> = setOf("stop-c1")
)`
  },
  {
    name: 'CharacterSelectionScreen.kt',
    path: 'composeApp/src/commonMain/kotlin/com/ecosdolugar/ui/screens/CharacterSelectionScreen.kt',
    category: 'UI Screens',
    content: `package com.ecosdolugar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.model.CharacterItem
import com.ecosdolugar.ui.theme.*

@Composable
fun CharacterSelectionScreen(
    currentCharacterId: String,
    onSelectCharacter: (CharacterItem) -> Unit,
    onConfirm: (CharacterItem) -> Unit,
    onBack: () -> Unit,
    userEmail: String? = null
) {
    var selectedChar by remember {
        mutableStateOf(CharactersRepository.getById(currentCharacterId))
    }

    Box(modifier = Modifier.fillMaxSize().background(PineDeep)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 32.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text("Elige tu personaje", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(selectedChar.avatarEmoji, fontSize = 22.sp)
            }

            // Carousel selector: Meiga, Trasno, Moura, Lavandeira, Lobishome, Caminante
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(CharactersRepository.characters) { charItem ->
                    val isSelected = charItem.id == selectedChar.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(88.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                            .clickable { selectedChar = charItem; onSelectCharacter(charItem) }
                            .padding(10.dp)
                    ) {
                        Text(charItem.avatarEmoji, fontSize = 28.sp)
                        Text(charItem.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Detailed Character Card with Special Ability ("Don") and Stats
            Surface(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F2618)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(selectedChar.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(selectedChar.lore, fontSize = 13.sp, color = SurfaceCream)
                    Text("Don del personaje: " + selectedChar.abilityName, fontWeight = FontWeight.Bold, color = TerracottaFixedDim)
                    Text(selectedChar.abilityDescription, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }

            Button(
                onClick = { onConfirm(selectedChar) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(selectedChar.accentColorHex))
            ) {
                Text("Comenzar expedición como " + selectedChar.name, fontWeight = FontWeight.Bold)
            }
        }
    }
}`
  },
  {
    name: 'CharactersRepository.kt',
    path: 'composeApp/src/commonMain/kotlin/com/ecosdolugar/data/CharactersRepository.kt',
    category: 'Data Repository',
    content: `package com.ecosdolugar.data

import com.ecosdolugar.model.CharacterItem
import com.ecosdolugar.model.CharacterStat

object CharactersRepository {
    val characters: List<CharacterItem> = listOf(
        CharacterItem(
            id = "meiga",
            name = "Meiga",
            galicianTitle = "A Sabia das Herbas e dos Lumes",
            avatarEmoji = "🧙‍♀️",
            archetype = "Mística & Curandeira",
            badge = "Espírito Sabio",
            lore = "Coñecedora dos meigallos, do lume sagrado e das herbas de San Xoán...",
            quote = "«Eu non creo nas meigas, mais habelas, hainas.»",
            abilityName = "Visión das Fragas",
            abilityDescription = "Detecta pistas ocultas e enigmas esotéricos a 50 metros sen gastar pistas.",
            sacredItem = "Herba de San Xoán e auga de sete fontes",
            element = "Lume & Vexetación",
            accentColorHex = 0xFF9F7AEA
        ),
        CharacterItem(
            id = "trasno",
            name = "Trasno",
            galicianTitle = "O Folgazán Bromista da Raia",
            avatarEmoji = "👺",
            archetype = "Pícaro & Veloz",
            badge = "Espírito Travieso",
            lore = "Espírito miúdo que foxe pola noite entre os camiños vellos da fronteira...",
            quote = "«Se atopas unha presa de millo no camiño, non a contes...»",
            abilityName = "Paso Escorregadizo",
            abilityDescription = "Reduce un 30% a distancia percibida entre paradas e revela atallos secretos da Raia.",
            sacredItem = "Gorro encarnado e cazaña de millo",
            element = "Terra & Penumbra",
            accentColorHex = 0xFFF56565
        )
        // Moura, Lavandeira, Lobishome, Contrabandista...
    )

    fun getById(id: String): CharacterItem = characters.find { it.id == id } ?: characters.first()
}`
  },
  {
    name: 'GoogleAuthService.kt',
    path: 'composeApp/src/commonMain/kotlin/com/ecosdolugar/auth/GoogleAuthService.kt',
    category: 'Auth KMP',
    content: `package com.ecosdolugar.auth

import com.ecosdolugar.model.UserProfile

sealed class GoogleAuthResult {
    data class Success(val email: String, val displayName: String, val idToken: String) : GoogleAuthResult()
    data class Error(val message: String) : GoogleAuthResult()
}

interface GoogleAuthService {
    suspend fun signInWithGoogle(): GoogleAuthResult
    suspend fun signOut()
    fun getCurrentUser(): UserProfile?
}`
  },
  {
    name: 'Theme.kt',
    path: 'composeApp/src/commonMain/kotlin/com/ecosdolugar/ui/theme/Theme.kt',
    category: 'Styling',
    content: `package com.ecosdolugar.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PineDeep = Color(0xFF061B0E)
val PineContainer = Color(0xFF1B3022)
val PineMoss = Color(0xFF47654E)
val MossContainer = Color(0xFFC5E8CA)
val TerracottaGold = Color(0xFFC48B47)
val TerracottaFixedDim = Color(0xFFFABA71)
val SurfaceCream = Color(0xFFFCF9F2)
val OnSurfaceDark = Color(0xFF1C1C18)

private val LightColorScheme = lightColorScheme(
    primary = PineDeep,
    onPrimary = Color.White,
    primaryContainer = PineContainer,
    secondary = PineMoss,
    secondaryContainer = MossContainer,
    surface = SurfaceCream,
    onSurface = OnSurfaceDark
)

@Composable
fun EcosDoLugarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}`
  },
  {
    name: 'build.gradle.kts',
    path: 'composeApp/build.gradle.kts',
    category: 'Build Config',
    content: `plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget()
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
            }
        }
    }
}`
  }
];

export const KmpCodeViewerModal: React.FC<KmpCodeViewerModalProps> = ({ isOpen, onClose }) => {
  const [activeFileIndex, setActiveFileIndex] = useState(0);
  const [copied, setCopied] = useState(false);

  if (!isOpen) return null;

  const currentFile = KMP_FILES[activeFileIndex];

  const handleCopy = () => {
    navigator.clipboard.writeText(currentFile.content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-[#061b0e]/75 backdrop-blur-md flex items-center justify-center p-3 sm:p-6 animate-in fade-in duration-200"
      onClick={onClose}
    >
      <div
        className="w-full max-w-4xl h-[90vh] bg-[#1a1c1a] text-[#f1eee7] rounded-2xl shadow-2xl border border-white/10 flex flex-col overflow-hidden animate-in zoom-in-95 duration-200"
        onClick={e => e.stopPropagation()}
      >
        {/* Header */}
        <div className="px-5 py-3.5 bg-[#0f110f] border-b border-white/10 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-purple-600 via-indigo-600 to-amber-500 flex items-center justify-center text-white shadow-sm font-bold text-[14px]">
              K
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="font-sans font-bold text-[16px] text-white">
                  Kotlin Multiplatform (KMP) Architecture
                </h3>
                <span className="px-2 py-0.5 rounded-full bg-emerald-950 text-emerald-300 border border-emerald-800 text-[10px] font-semibold uppercase tracking-wider">
                  Compose Multiplatform
                </span>
              </div>
              <p className="text-[12px] text-[#c3c8c1]">
                Android • iOS • Desktop (JVM) • Web (Wasm)
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={handleCopy}
              className="px-3 py-1.5 rounded-lg bg-white/10 hover:bg-white/20 text-white text-[12px] font-medium flex items-center gap-1.5 transition-colors cursor-pointer"
            >
              {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copied ? 'Copiado' : 'Copiar archivo'}</span>
            </button>
            <button
              onClick={onClose}
              className="w-8 h-8 rounded-lg bg-white/10 hover:bg-white/20 flex items-center justify-center text-[#c3c8c1] hover:text-white cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Multiplatform Targets Ribbon */}
        <div className="px-5 py-2 bg-[#141614] border-b border-white/5 flex items-center gap-4 text-[12px] text-[#c3c8c1]">
          <span className="font-semibold text-white/70">Targets compilables:</span>
          <span className="flex items-center gap-1 text-emerald-400">
            <Smartphone className="w-3.5 h-3.5" /> Android (Jetpack Compose)
          </span>
          <span className="flex items-center gap-1 text-blue-400">
            <Smartphone className="w-3.5 h-3.5" /> iOS (Native UIKit / Compose)
          </span>
          <span className="flex items-center gap-1 text-purple-400">
            <Laptop className="w-3.5 h-3.5" /> Desktop (JVM / macOS / Windows / Linux)
          </span>
          <span className="flex items-center gap-1 text-amber-400">
            <Globe className="w-3.5 h-3.5" /> Web (Compose Wasm)
          </span>
        </div>

        {/* File Tabs & Code Area */}
        <div className="flex flex-1 overflow-hidden">
          {/* File sidebar list */}
          <div className="w-64 bg-[#121412] border-r border-white/10 p-3 flex flex-col gap-1 overflow-y-auto">
            <span className="text-[11px] font-semibold text-white/50 uppercase tracking-wider px-2 py-1">
              Archivos KMP Generados
            </span>
            {KMP_FILES.map((file, idx) => {
              const isSelected = idx === activeFileIndex;
              return (
                <button
                  key={file.name}
                  onClick={() => setActiveFileIndex(idx)}
                  className={`px-3 py-2 rounded-xl text-left font-mono text-[12px] flex items-center gap-2 transition-all cursor-pointer ${
                    isSelected
                      ? 'bg-purple-600/30 text-purple-200 border border-purple-500/40 font-semibold'
                      : 'text-[#c3c8c1] hover:bg-white/5 hover:text-white'
                  }`}
                >
                  <FileCode className="w-3.5 h-3.5 shrink-0 opacity-70" />
                  <div className="truncate">
                    <div>{file.name}</div>
                    <div className="text-[10px] text-white/40">{file.category}</div>
                  </div>
                </button>
              );
            })}
          </div>

          {/* Code Viewer */}
          <div className="flex-1 flex flex-col bg-[#161816] overflow-hidden">
            <div className="px-4 py-2 bg-[#121412] border-b border-white/5 text-[11px] font-mono text-white/60 flex items-center justify-between">
              <span>{currentFile.path}</span>
              <span className="text-[10px] text-white/40">Kotlin Multiplatform 2.0</span>
            </div>

            <pre className="flex-1 p-4 text-[12px] font-mono leading-relaxed text-[#e6edf3] overflow-auto bg-[#0d1117] select-text">
              <code>{currentFile.content}</code>
            </pre>
          </div>
        </div>
      </div>
    </div>
  );
};
