package com.ecosdolugar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.formatRating
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.appLanguageStrings
import com.ecosdolugar.localization.categoryDisplayName
import com.ecosdolugar.localization.difficultyLabel
import com.ecosdolugar.localization.routeDetailStrings
import com.ecosdolugar.model.Category
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.platform.rememberShareText
import com.ecosdolugar.ui.components.GuideAvatar
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.theme.*

// Public web address used to build shareable route links.
private const val APP_SHARE_BASE_URL = "https://ekosdolugar.com"

private fun routeShareMessage(route: RouteItem): String =
    "${route.title}\n$APP_SHARE_BASE_URL/?ruta=${route.id}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    route: RouteItem,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSaved: (String) -> Unit,
    onOpenMap: () -> Unit,
    onStartRoute: (RouteItem) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenGuideProfile: (GuideAuthor) -> Unit = {},
    language: AppLanguage = AppLanguage.SPANISH
) {
    val shareText = rememberShareText()
    val strings = routeDetailStrings(language)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = appLanguageStrings(language).brandName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PineDeep
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = strings.back)
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
                        Text(
                            strings.viewMap,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Button(
                        onClick = { onStartRoute(route) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PineDeep,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            strings.startRoute,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(22.dp))
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
                        // Actions Top Right
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .align(Alignment.TopEnd),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                onClick = { onToggleSaved(route.id) },
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.4f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = strings.saveRoute,
                                        tint = if (isSaved) TerracottaGold else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Surface(
                                onClick = { shareText(routeShareMessage(route)) },
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.4f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = strings.shareRoute,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Route type tags (multi-select categories)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            route.categories.filter { it != Category.ALL }.forEach { category ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PineDeep.copy(alpha = 0.85f)
                                ) {
                                    Text(
                                        text = categoryDisplayName(category, language),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
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

            // Route rating from user reviews
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (route.reviewsCount > 0) {
                            "${formatRating(route.rating)} · ${route.reviewsCount} ${strings.ratingsWord}"
                        } else {
                            strings.noRatingsYet
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceDark
                    )
                }
            }

            // Metrics Grid (icon + value, as in the web)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            icon = Icons.Default.Schedule,
                            value = route.duration,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            icon = Icons.Default.Straighten,
                            value = route.distance,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            icon = Icons.Default.Explore,
                            value = "${route.stopsCount} ${if (route.stopsCount == 1) strings.stopsSingular else strings.stopsPlural}",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            icon = Icons.Default.DirectionsWalk,
                            value = difficultyLabel(route.difficulty, language),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Guide card (route author)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenGuideProfile(route.guide) },
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = strings.routeGuideLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PineMoss,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            GuideAvatar(name = route.guide.name, avatarUrl = route.guide.avatarUrl, size = 52.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = route.guide.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = OnSurfaceDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (route.guide.verified) {
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = strings.verifiedGuide,
                                            tint = TerracottaGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = route.guide.title,
                                    fontSize = 12.sp,
                                    color = PineMoss,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = TerracottaGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (route.guide.reviewsCount > 0) formatRating(route.guide.rating) else strings.noRatings,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceDark
                                    )
                                    if (route.guide.reviewsCount > 0) {
                                        Text(
                                            text = "(${route.guide.reviewsCount} ${strings.guideReviewsWord})",
                                            fontSize = 12.sp,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        if (route.guide.bio.isNotBlank()) {
                            HorizontalDivider(color = PineContainer.copy(alpha = 0.1f))
                            Text(
                                text = route.guide.bio,
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
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = modifier,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MossContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PineDeep, modifier = Modifier.size(20.dp))
            }
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

