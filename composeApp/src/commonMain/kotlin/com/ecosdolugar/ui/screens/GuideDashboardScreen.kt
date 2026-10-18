package com.ecosdolugar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.formatRating
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview
import com.ecosdolugar.ui.components.GuideAvatar
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDashboardScreen(
    guide: GuideAuthor,
    routes: List<RouteItem>,
    reviews: List<RouteReview> = emptyList(),
    isSaving: Boolean = false,
    onBack: () -> Unit,
    onCreateNewRoute: () -> Unit,
    onEditRoute: (RouteItem) -> Unit,
    onPublishDraft: (RouteItem) -> Unit
) {
    val publishedRoutes = routes.filter { it.status == "publicada" }
    val pendingRoutes = routes.filter { it.status != "publicada" }
    var selectedTab by remember { mutableStateOf(0) }
    val visibleRoutes = when (selectedTab) {
        0 -> publishedRoutes
        1 -> pendingRoutes
        else -> emptyList()
    }
    val explorers = routes.sumOf { it.startedCount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel del guía",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCream)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateNewRoute,
                containerColor = PineDeep,
                contentColor = SurfaceCream,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva ruta", fontWeight = FontWeight.SemiBold) }
            )
        },
        containerColor = SurfaceCream
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GuideIdentityCard(guide = guide)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTile(
                        label = "Publicadas",
                        value = publishedRoutes.size.toString(),
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Pendientes",
                        value = routes.count { it.status == "pendiente" }.toString(),
                        icon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTile(
                        label = "Exploradores",
                        value = explorers.toString(),
                        icon = Icons.Default.Explore,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Valoración",
                        value = if (guide.reviewsCount > 0) formatRating(guide.rating) else "—",
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceCream,
                    contentColor = PineDeep
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Publicadas (${publishedRoutes.size})",
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Pendientes (${pendingRoutes.size})",
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "Reseñas (${reviews.size})",
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            if (selectedTab == 2) {
                if (reviews.isEmpty()) {
                    item {
                        Text(
                            text = "Todavía no tienes reseñas de exploradores.",
                            fontSize = 14.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
                items(reviews.size) { index ->
                    GuideReviewCard(review = reviews[index])
                }
            } else {
                if (visibleRoutes.isEmpty()) {
                    item {
                        Text(
                            text = if (selectedTab == 0) "Todavía no tienes rutas publicadas."
                            else "No hay rutas pendientes de revisión.",
                            fontSize = 14.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                items(visibleRoutes.size) { index ->
                    val route = visibleRoutes[index]
                    GuideRouteCard(
                        route = route,
                        isSaving = isSaving,
                        onEdit = { onEditRoute(route) },
                        onPublish = { onPublishDraft(route) }
                    )
                }
            }

            item {
                Spacer(Modifier.height(88.dp))
            }
        }
    }
}

@Composable
private fun GuideReviewCard(review: RouteReview) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = review.routeTitle.ifBlank { "Ruta" },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = OnSurfaceDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ReviewStars(label = "Ruta", value = review.routeRating)
                ReviewStars(label = "Guía", value = review.guideRating)
            }
            if (review.comment.isNotBlank()) {
                Text(
                    text = review.comment,
                    fontSize = 13.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
            Text(
                text = review.createdAt.take(10),
                fontSize = 11.sp,
                color = PineMoss
            )
        }
    }
}

@Composable
private fun ReviewStars(label: String, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "$label:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PineMoss
        )
        repeat(5) { index ->
            Icon(
                imageVector = if (index < value) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = TerracottaGold,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun GuideIdentityCard(guide: GuideAuthor) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GuideAvatar(name = guide.name, avatarUrl = guide.avatarUrl, size = 60.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = guide.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = OnSurfaceDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (guide.verified) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Guía verificado",
                            tint = TerracottaGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                val subtitle = listOf(guide.title, guide.location).filter { it.isNotBlank() }.joinToString(" · ")
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = PineMoss,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(14.dp))
                    Text(
                        text = if (guide.reviewsCount > 0) "${formatRating(guide.rating)} (${guide.reviewsCount} reseñas)" else "Sin reseñas todavía",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceDark
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = modifier,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PineMoss,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(icon, contentDescription = null, tint = PineMoss, modifier = Modifier.size(16.dp))
            }
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceDark
            )
        }
    }
}

@Composable
private fun GuideRouteCard(
    route: RouteItem,
    isSaving: Boolean,
    onEdit: () -> Unit,
    onPublish: () -> Unit
) {
    val isDraft = route.status == "borrador"
    val isPending = route.status == "pendiente"
    val statusLabel = when {
        isDraft -> "Borrador"
        isPending -> "Pendiente de verificación"
        else -> "Publicada"
    }
    val statusColor = when {
        isDraft -> Color(0xFFFEF3C7)
        isPending -> Color(0xFFFFEDD5)
        else -> Color(0xFFD1FAE5)
    }
    val statusTextColor = when {
        isDraft -> Color(0xFF92400E)
        isPending -> Color(0xFF9A3412)
        else -> Color(0xFF065F46)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                RouteCoverImage(
                    url = route.imageUrl,
                    modifier = Modifier.fillMaxSize().background(SurfaceContainerHighest)
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = statusLabel.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCream,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Text(
                        text = route.price,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PineDeep,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = route.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = OnSurfaceDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${route.location} · ${route.duration} · ${route.stopsCount} paradas",
                    fontSize = 12.sp,
                    color = PineMoss,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = route.description,
                    fontSize = 13.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCream,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("INICIOS", fontSize = 10.sp, color = PineMoss, fontWeight = FontWeight.Bold)
                            Text(route.startedCount.toString(), fontWeight = FontWeight.Bold, color = OnSurfaceDark)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PARADAS", fontSize = 10.sp, color = PineMoss, fontWeight = FontWeight.Bold)
                            Text(route.stopsCount.toString(), fontWeight = FontWeight.Bold, color = OnSurfaceDark)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DIFICULTAD", fontSize = 10.sp, color = PineMoss, fontWeight = FontWeight.Bold)
                            Text(
                                route.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = SurfaceContainerHighest)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (isDraft) {
                    OutlinedButton(
                        onClick = onPublish,
                        enabled = !isSaving,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PineDeep)
                    ) {
                        Text("Publicar")
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Button(
                    onClick = onEdit,
                    enabled = !isSaving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PineDeep, contentColor = SurfaceCream)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isDraft) "Continuar borrador" else "Editar ruta")
                }
            }
        }
    }
}
