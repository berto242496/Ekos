package com.ecosdolugar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.model.OverviewFeature
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ECOS DO LUGAR",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PineDeep
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCream)
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceCream,
                shadowElevation = 8.dp,
                modifier = Modifier.border(1.dp, PineContainer.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenMap,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SurfaceContainerHigh,
                            contentColor = OnSurfaceDark
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(OutlineVariant)
                        )
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Ver mapa", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onStartRoute(route) },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PineDeep,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Empezar ruta", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Visual Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerHighest)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        RouteCoverImage(
                            url = route.imageUrl,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Badge Top Left
                        Surface(
                            shape = CircleShape,
                            color = PineDeep.copy(alpha = 0.85f),
                            modifier = Modifier
                                .padding(14.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Text(
                                text = "Ruta Fronteriza Histórica",
                                color = TerracottaFixedDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }

                        // Bookmark Action Top Right
                        IconButton(
                            onClick = { onToggleSaved(route.id) },
                            modifier = Modifier
                                .padding(14.dp)
                                .align(Alignment.TopEnd)
                                .size(38.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isSaved) TerracottaGold else Color.White
                            )
                        }

                        // Tomo reference badge bottom
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = route.tomoRef,
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Title and Geographic Subtitle
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = route.region,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PineMoss,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Text(
                        text = route.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = OnSurfaceDark
                        )
                    )
                    Text(
                        text = route.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // Bento Grid Metrics (Duration, Distance, Stops, Difficulty)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "DURACIÓN ESTIMADA",
                            value = route.duration,
                            subtitle = "A pie y descansos",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "DISTANCIA",
                            value = route.distance,
                            subtitle = "Trazado circular",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "PARADAS CLAVE",
                            value = "${route.stopsCount} puntos",
                            subtitle = "Con audio y enigmas",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "DIFICULTAD",
                            value = route.difficulty.label,
                            subtitle = "Apta para paseantes",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Cartographic Preview Strip
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 1.dp
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MossContainer, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = PineDeep)
                            }
                            Column {
                                Text(
                                    text = "Trazado cartográfico",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceDark
                                )
                                Text(
                                    text = route.subtitle,
                                    fontSize = 11.sp,
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        TextButton(onClick = onOpenMap) {
                            Text("Ver mapa", color = PineMoss, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // "Qué descubrirás" Editorial Section
            item {
                Text(
                    text = "Qué descubrirás",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = OnSurfaceDark
                    )
                )
            }

            items(route.overviewFeatures.size) { index ->
                val feature = route.overviewFeatures[index]
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SurfaceContainerHigh, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontWeight = FontWeight.Bold,
                                color = PineDeep,
                                fontSize = 13.sp
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = feature.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = OnSurfaceDark
                            )
                            Text(
                                text = feature.description,
                                fontSize = 13.sp,
                                color = OnSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = modifier,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = PineMoss,
                letterSpacing = 0.8.sp
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceDark
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = OutlineGrey
            )
        }
    }
}
