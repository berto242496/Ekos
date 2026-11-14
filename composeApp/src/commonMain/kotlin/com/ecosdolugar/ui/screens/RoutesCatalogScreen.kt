package com.ecosdolugar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.appLanguageStrings
import com.ecosdolugar.localization.categoryDisplayName
import com.ecosdolugar.localization.routeCatalogStrings
import com.ecosdolugar.model.Category
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.ui.components.CharacterAvatarImage
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.theme.*
import ecosdolugar.composeapp.generated.resources.Res
import ecosdolugar.composeapp.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesCatalogScreen(
    routes: List<RouteItem>,
    savedRouteIds: Set<String>,
    userProfile: UserProfile = UserProfile(),
    language: AppLanguage,
    onSelectRoute: (RouteItem) -> Unit,
    onToggleSaved: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenGuideMode: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(emptySet<Category>()) }
    var onlySaved by remember { mutableStateOf(false) }
    val strings = routeCatalogStrings(language)

    val profileCharacter = userProfile.characterId?.let { CharactersRepository.getById(it) }

    val filteredRoutes = remember(searchQuery, selectedCategories, onlySaved, routes, savedRouteIds) {
        routes.filter { route ->
            val matchesCategory = selectedCategories.isEmpty() || route.categories.any { it in selectedCategories }
            val matchesSearch = searchQuery.isBlank() ||
                route.title.contains(searchQuery, ignoreCase = true) ||
                route.location.contains(searchQuery, ignoreCase = true) ||
                route.description.contains(searchQuery, ignoreCase = true) ||
                route.guide.name.contains(searchQuery, ignoreCase = true)
            val matchesSaved = !onlySaved || savedRouteIds.contains(route.id)
            matchesCategory && matchesSearch && matchesSaved
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
                        Image(
                            painter = painterResource(Res.drawable.app_icon),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        )
                        Column {
                            Text(
                                text = appLanguageStrings(language).brandName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PineMoss,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = strings.routesTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        }
                    }

                    // Actions Header (Soy Guía, Personaje, Perfil)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (userProfile.isGuideAccount) {
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
                                    Text(strings.guideButton, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TerracottaGold)
                                }
                            }
                        }

                        // Profile / Session Button (user photo or guide avatar)
                        Surface(
                            onClick = onOpenProfile,
                            shape = CircleShape,
                            color = PineDeep,
                            modifier = Modifier.size(34.dp)
                        ) {
                            when {
                                profileCharacter != null -> CharacterAvatarImage(
                                    image = profileCharacter.avatarImage,
                                    modifier = Modifier.fillMaxSize()
                                )
                                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = strings.profile, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
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
                            text = strings.topLabel,
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
                                contentDescription = strings.savedRoutes,
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
                        text = strings.heading,
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
                            text = strings.routesAvailable(filteredRoutes.size),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = OnSurfaceVariant,
                                fontStyle = FontStyle.Italic
                            )
                        )

                        if (selectedCategories.isNotEmpty() || onlySaved || searchQuery.isNotBlank()) {
                            TextButton(
                                onClick = {
                                    selectedCategories = emptySet()
                                    onlySaved = false
                                    searchQuery = ""
                                }
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(strings.clearFilters, color = TerracottaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    placeholder = { Text(strings.searchPlaceholder, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = OutlineGrey)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = strings.clearSearch, tint = OutlineGrey)
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

            // Categories Filter Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Category.values().filter { it != Category.ALL }) { category ->
                        val isSelected = category in selectedCategories
                        Surface(
                            onClick = {
                                selectedCategories = if (isSelected) selectedCategories - category
                                else selectedCategories + category
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PineDeep else SurfaceContainerHigh,
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isSelected) PineDeep else OutlineVariant.copy(alpha = 0.5f)
                                )
                            )
                        ) {
                            Text(
                                text = categoryDisplayName(category, language),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Route Cards List
            if (filteredRoutes.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(strings.noRoutesTitle, color = PineDeep, fontWeight = FontWeight.Bold)
                        Text(strings.noRoutesDescription, color = OnSurfaceVariant, fontSize = 13.sp)
                    }
                }
            } else {
                items(filteredRoutes, key = { it.id }) { route ->
                    val isSaved = savedRouteIds.contains(route.id)
                    RouteCard(
                        route = route,
                        isSaved = isSaved,
                        strings = strings,
                        onClick = { onSelectRoute(route) },
                        onToggleSaved = { onToggleSaved(route.id) }
                    )
                }
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
    strings: com.ecosdolugar.localization.RouteCatalogStrings,
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
            RouteCoverImage(
                url = route.imageUrl,
                modifier = Modifier.fillMaxSize()
            )
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
                                text = "${strings.featured} · ${route.stopsCount} " +
                                    if (route.stopsCount == 1) strings.stopsSingular else strings.stopsPlural,
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
                                text = "${route.stopsCount} " +
                                    if (route.stopsCount == 1) strings.stopsSingular else strings.stopsPlural,
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
                            contentDescription = strings.saveRoute,
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

                    if (!route.isFree && !route.price.equals("Gratis", ignoreCase = true)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TerracottaGold.copy(alpha = 0.9f)
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
                        text = "${strings.byGuide} ${route.guide.name}",
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
                        text = "${route.stopsCount} " +
                            if (route.stopsCount == 1) strings.geolocatedStopsSingular else strings.geolocatedStopsPlural,
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
                        Text(strings.viewRoute, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
