package com.ecosdolugar.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.model.Category
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
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
    var isAmbientPlaying by remember { mutableStateOf(false) }

    val filteredRoutes = remember(searchQuery, selectedCategory, routes) {
        routes.filter { route ->
            val matchesCategory = selectedCategory == Category.ALL || route.categories.contains(selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                route.title.contains(searchQuery, ignoreCase = true) ||
                route.location.contains(searchQuery, ignoreCase = true) ||
                route.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ECOS DO LUGAR",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PineDeep
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenProfile) {
                        Surface(
                            shape = CircleShape,
                            color = MossContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "E",
                                    fontWeight = FontWeight.Bold,
                                    color = PineDeep,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceCream
                )
            )
        },
        containerColor = SurfaceCream
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Regional Section Eyebrow
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GALICIA & RAIA MIÑOTA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PineMoss,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                }
            }

            // Headline & Counter
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Elige tu ruta",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = OnSurfaceDark
                        )
                    )
                    Text(
                        text = "3 experiencias narradas disponibles hoy",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurfaceVariant
                        )
                    )
                }
            }

            // Search Text Field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por hito, pueblo o misterio...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = OutlineGrey)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PineDeep,
                        unfocusedBorderColor = OutlineVariant
                    ),
                    singleLine = true
                )
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(Category.values()) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.displayName, fontSize = 12.sp) },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PineDeep,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) PineDeep else OutlineVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // Ambient Soundbar of A Raia
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PineDeep,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isAmbientPlaying) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                    contentDescription = null,
                                    tint = TerracottaFixedDim,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Ambiente de la Raia",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Sonidos del río y niebla matinal",
                                    color = TerracottaFixed,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(onClick = { isAmbientPlaying = !isAmbientPlaying }) {
                            Icon(
                                imageVector = if (isAmbientPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            // List of Routes
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
                Spacer(Modifier.height(32.dp))
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
            // Hero Image Container with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(SurfaceContainerHighest)
            ) {
                // Top Tag Badges
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
                            color = PineDeep.copy(alpha = 0.85f)
                        ) {
                            Text(
                                text = "DESTACADA · 8 PARADAS",
                                color = TerracottaFixedDim,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                letterSpacing = 1.sp
                            )
                        }
                    } else {
                        Spacer(Modifier.width(1.dp))
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

                // Bottom Audio Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PineDeep.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = route.audioTag,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
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
                        color = OnSurfaceDark
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
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
