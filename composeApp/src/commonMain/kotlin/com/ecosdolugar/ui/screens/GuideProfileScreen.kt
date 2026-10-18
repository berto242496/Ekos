package com.ecosdolugar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun GuideProfileScreen(
    guide: GuideAuthor,
    allRoutes: List<RouteItem>,
    reviews: List<RouteReview> = emptyList(),
    onBack: () -> Unit,
    onSelectRoute: (String) -> Unit
) {
    val guideRoutes = allRoutes.filter { it.guide.id == guide.id }
    val firstName = guide.name.substringBefore(' ')

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Perfil del guía",
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
        containerColor = SurfaceCream
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GuideAvatar(name = guide.name, avatarUrl = guide.avatarUrl, size = 72.dp)
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = guide.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
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
                            Text(
                                text = guide.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PineMoss
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    text = guide.location.ifBlank { "Galicia" },
                                    fontSize = 12.sp,
                                    color = PineMoss,
                                    maxLines = 1,
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

            if (guide.bio.isNotBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "SOBRE EL GUÍA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PineMoss,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            )
                            Text(
                                text = guide.bio,
                                fontSize = 14.sp,
                                color = OnSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            if (reviews.isNotEmpty()) {
                item {
                    Text(
                        text = "Reseñas (${reviews.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark
                        )
                    )
                }
                items(reviews.size) { index ->
                    val review = reviews[index]
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = review.routeTitle.ifBlank { "Ruta" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = OnSurfaceDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Ruta ${review.routeRating}/5 · Guía ${review.guideRating}/5",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TerracottaGold
                            )
                            if (review.comment.isNotBlank()) {
                                Text(
                                    text = review.comment,
                                    fontSize = 13.sp,
                                    color = OnSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Rutas creadas por $firstName (${guideRoutes.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceDark
                    )
                )
            }

            if (guideRoutes.isEmpty()) {
                item {
                    Text(
                        text = "Este guía todavía no tiene rutas publicadas.",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            items(guideRoutes.size) { index ->
                val route = guideRoutes[index]
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectRoute(route.id) },
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RouteCoverImage(
                            url = route.imageUrl,
                            modifier = Modifier
                                .size(72.dp)
                                .background(SurfaceContainerHighest, RoundedCornerShape(12.dp))
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = route.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = OnSurfaceDark,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${route.location} · ${route.duration} · ${route.stopsCount} paradas",
                                fontSize = 12.sp,
                                color = OnSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = route.price,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PineMoss
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Ver ruta", tint = PineMoss)
                    }
                }
            }

            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
