package com.ecosdolugar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.model.Category
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesCatalogScreen(
    routes: List<RouteItem>,
    savedRouteIds: Set<String>,
    userProfile: UserProfile = UserProfile(),
    onSelectRoute: (RouteItem) -> Unit,
    onToggleSaved: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenCharacterSelection: () -> Unit = {},
    onOpenGuideMode: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.ALL) }
    var selectedPriceFilter by remember { mutableStateOf("all") }
    var onlySaved by remember { mutableStateOf(false) }
    var isAmbientPlaying by remember { mutableStateOf(false) }
    var activeFilterDrawer by remember { mutableStateOf<String?>(null) } // "price", "guide", "zone"

    val activeCharacter = remember(userProfile.characterId) {
        CharactersRepository.getById(userProfile.characterId)
    }
    val charColor = Color(activeCharacter.accentColorHex)

    val filteredRoutes = remember(searchQuery, selectedCategory, selectedPriceFilter, onlySaved, routes) {
        routes.filter { route ->
            val matchesCategory = selectedCategory == Category.ALL || route.categories.contains(selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                route.title.contains(searchQuery, ignoreCase = true) ||
                route.location.contains(searchQuery, ignoreCase = true) ||
                route.description.contains(searchQuery, ignoreCase = true) ||
                route.guide.name.contains(searchQuery, ignoreCase = true)
            val matchesSaved = !onlySaved || savedRouteIds.contains(route.id)
            val matchesPrice = when (selectedPriceFilter) {
                "free" -> route.isFree || route.price.contains("Gratis", ignoreCase = true)
                "paid" -> !route.isFree && !route.price.contains("Gratis", ignoreCase = true)
                else -> true
            }
            matchesCategory && matchesSearch && matchesSaved && matchesPrice
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = SurfaceCream.copy(alpha = 0.95f),
                tonalElevation = 2.dp,
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(PineContainer.copy(alpha = 0.08f))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Logo & App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PineDeep,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🌿", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "ECOS DO LUGAR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PineMoss,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Rutas",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        }
                    }

                    // Actions Header (Soy Guía, Personaje, Perfil)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Soy Guía Pill Button
                        Surface(
                            onClick = onOpenGuideMode,
                            shape = RoundedCornerShape(20.dp),
                            color = TerracottaGold.copy(alpha = 0.15f),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(TerracottaGold.copy(alpha = 0.4f))
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CompassCalibration, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(13.dp))
                                Text("Guía", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TerracottaGold)
                            }
                        }

                        // Character Quick Badge
                        Surface(
                            onClick = onOpenCharacterSelection,
                            shape = RoundedCornerShape(20.dp),
                            color = charColor.copy(alpha = 0.15f),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(charColor.copy(alpha = 0.35f))
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(activeCharacter.avatarEmoji, fontSize = 14.sp)
                                Text(activeCharacter.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PineDeep)
                            }
                        }

                        // Profile / Session Button
                        IconButton(
                            onClick = onOpenProfile,
                            modifier = Modifier
                                .size(34.dp)
                                .background(PineDeep, CircleShape)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        containerColor = SurfaceCream
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(Modifier.height(4.dp))
            }

            // Top Location Bar & Bookmark filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, tint = PineMoss, modifier = Modifier.size(15.dp))
                        Text(
                            text = "TODA GALICIA · LAS 4 PROVINCIAS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PineMoss,
                            letterSpacing = 1.sp
                        )
                    }

                    Surface(
                        onClick = { onlySaved = !onlySaved },
                        shape = CircleShape,
                        color = if (onlySaved) PineDeep else SurfaceContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (onlySaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Guardadas",
                                tint = if (onlySaved) Color.White else OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Title & Count
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Elige tu ruta mágica",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = PineDeep,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${filteredRoutes.size} experiencia${if (filteredRoutes.size == 1) "" else "s"} en catálogo",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = OnSurfaceVariant,
                                fontStyle = FontStyle.Italic
                            )
                        )

                        if (selectedCategory != Category.ALL || selectedPriceFilter != "all" || onlySaved || searchQuery.isNotBlank()) {
                            TextButton(
                                onClick = {
                                    selectedCategory = Category.ALL
                                    selectedPriceFilter = "all"
                                    onlySaved = false
                                    searchQuery = ""
                                    activeFilterDrawer = null
                                }
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Limpiar filtros", color = TerracottaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Character Companion Banner (Identical to Web)
            item {
                Surface(
                    onClick = onOpenCharacterSelection,
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1B3022), Color(0xFF0B2414))
                                ),
                                RoundedCornerShape(18.dp)
                            )
                            .border(1.dp, Color(0xFF2B4C37), RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(activeCharacter.avatarEmoji, fontSize = 24.sp)
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(activeCharacter.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = TerracottaFixedDim.copy(alpha = 0.2f),
                                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                                brush = androidx.compose.ui.graphics.SolidColor(TerracottaFixedDim.copy(alpha = 0.4f))
                                            )
                                        ) {
                                            Text(
                                                activeCharacter.archetype.uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TerracottaFixedDim,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "Don activo: ${activeCharacter.abilityName}",
                                        fontSize = 12.sp,
                                        color = TerracottaFixedDim
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    "Cambiar",
                                    color = TerracottaFixedDim,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por título, pueblo, zona o guía...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = OutlineGrey)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = OutlineGrey)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceContainerLow,
                        unfocusedContainerColor = SurfaceContainerLow,
                        focusedBorderColor = PineDeep,
                        unfocusedBorderColor = OutlineVariant.copy(alpha = 0.4f)
                    ),
                    singleLine = true
                )
            }

            // 3 Filter Pills: Precio, Guías, Zona (Matching Web)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Precio Button
                    FilterChip(
                        selected = selectedPriceFilter != "all",
                        onClick = {
                            selectedPriceFilter = when (selectedPriceFilter) {
                                "all" -> "free"
                                "free" -> "paid"
                                else -> "all"
                            }
                        },
                        label = {
                            Text(
                                when (selectedPriceFilter) {
                                    "free" -> "Precio: Gratis"
                                    "paid" -> "Precio: Pago"
                                    else -> "Precio"
                                },
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Sell, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(14.dp))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Guía Button
                    FilterChip(
                        selected = false,
                        onClick = onOpenGuideMode,
                        label = { Text("Guías", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = PineMoss, modifier = Modifier.size(14.dp))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Zona Button
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text("Zona", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PineMoss, modifier = Modifier.size(14.dp))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Categories Filter Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Category.values()) { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            onClick = { selectedCategory = category },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PineDeep else SurfaceContainerHigh,
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isSelected) PineDeep else OutlineVariant.copy(alpha = 0.5f)
                                )
                            )
                        ) {
                            Text(
                                text = category.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Soundscape Bar (Rías & Fragas)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF132B1B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Forest, contentDescription = null, tint = MossContainer, modifier = Modifier.size(24.dp))
                            Column {
                                Text(
                                    text = "PAISAXE SONORA GALEGA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TerracottaFixedDim
                                )
                                Text(
                                    text = if (isAmbientPlaying) "Reproducindo rumor de auga e bosque" else "Activar ambiente natural",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }

                        IconButton(onClick = { isAmbientPlaying = !isAmbientPlaying }) {
                            Icon(
                                if (isAmbientPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = TerracottaGold,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }
            }

            // Route Cards List
            items(filteredRoutes) { route ->
                val isSaved = savedRouteIds.contains(route.id)
                RouteCard(
                    route = route,
                    isSaved = isSaved,
                    onClick = { onSelectRoute(route) },
                    onToggleSaved = { onToggleSaved(route.id) }
                )
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun RouteCard(
    route: RouteItem,
    isSaved: Boolean,
    onClick: () -> Unit,
    onToggleSaved: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Visual Header Container with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1B3022), Color(0xFF0F1E15))
                        )
                    )
            ) {
                // Top Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (route.isFeatured) {
                        Surface(
                            shape = CircleShape,
                            color = PineDeep.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "DESTACADA · ${route.stopsCount} PARADAS",
                                color = TerracottaFixedDim,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                letterSpacing = 1.sp
                            )
                        }
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "${route.stopsCount} PARADAS",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleSaved,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Guardar",
                            tint = if (isSaved) TerracottaGold else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Bottom Audio & Price Tag
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PineDeep.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Headphones, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Text(route.audioTag, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (route.isFree) MossContainer.copy(alpha = 0.9f) else TerracottaGold.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = route.price,
                            color = PineDeep,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Card Body Content
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = route.location.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PineMoss,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${route.duration} • ${route.distance}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = OutlineGrey
                        )
                    )
                }

                Text(
                    text = route.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = OnSurfaceDark,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = route.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurfaceVariant
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Guide Author Sub-badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = PineMoss, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Por ${route.guide.name}",
                        fontSize = 11.sp,
                        color = OutlineGrey
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${route.stopsCount} paradas geolocalizadas",
                        color = OutlineGrey,
                        fontSize = 12.sp
                    )

                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PineDeep,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Ver ruta", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
