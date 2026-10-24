package com.ecosdolugar.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.difficultyLabel
import com.ecosdolugar.localization.interactiveMapStrings
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.platform.PlatformBackHandler
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.components.RouteMapView
import com.ecosdolugar.ui.theme.*
import com.ecosdolugar.util.distanceMeters
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val STOP_REACHED_RADIUS_METERS = 10.0

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InteractiveMapScreen(
    routes: List<RouteItem>,
    language: AppLanguage,
    initialRouteId: String? = null,
    expeditionMode: Boolean = false,
    solvedStopIds: Set<String> = emptySet(),
    triggeredStopIds: Set<String> = emptySet(),
    onBack: () -> Unit,
    onSelectRoute: (RouteItem) -> Unit,
    onStopReached: ((RouteStop) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val strings = interactiveMapStrings(language)
    var activeRouteId by remember(initialRouteId) { mutableStateOf(initialRouteId) }
    var selectedStop by remember { mutableStateOf<RouteStop?>(null) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val selectedRoute = routes.firstOrNull { it.id == activeRouteId }
    val routeIds = remember(routes) { routes.map { it.id } }
    var userLocation by remember(activeRouteId) { mutableStateOf<Pair<Double, Double>?>(null) }
    var showApproachBanner by remember { mutableStateOf(false) }

    PlatformBackHandler(onBack = onBack)

    LaunchedEffect(expeditionMode) {
        if (expeditionMode) {
            showApproachBanner = true
            delay(4000)
            showApproachBanner = false
        }
    }

    LaunchedEffect(listState, routeIds) {
        var previousCenteredRouteId: String? = null
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val center = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val centeredIndex = layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs(item.offset + item.size / 2 - center)
            }?.index
            centeredIndex to listState.isScrollInProgress
        }.collect { (centeredIndex, scrolling) ->
            val centeredRouteId = centeredIndex?.let(routeIds::getOrNull)
            if (!scrolling && centeredRouteId != null) {
                if (previousCenteredRouteId != null && previousCenteredRouteId != centeredRouteId) {
                    activeRouteId = centeredRouteId
                }
                previousCenteredRouteId = centeredRouteId
            }
        }
    }

    LaunchedEffect(routeIds, activeRouteId) {
        if (activeRouteId != null && activeRouteId !in routeIds) {
            activeRouteId = null
        }
    }
    LaunchedEffect(activeRouteId) {
        selectedStop = null
    }

    fun focusRoute(routeId: String) {
        activeRouteId = routeId
        routes.indexOfFirst { it.id == routeId }.takeIf { it >= 0 }?.let { index ->
            coroutineScope.launch { listState.animateScrollToItem(index) }
        }
    }

    LaunchedEffect(initialRouteId, routeIds) {
        initialRouteId?.takeIf { it in routeIds }?.let(::focusRoute)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFD4E4E9))
    ) {
        RouteMapView(
            routes = if (expeditionMode) routes.filter { it.id == activeRouteId } else routes,
            selectedRouteId = activeRouteId,
            selectedStopId = selectedStop?.id,
            onRouteSelected = { routeId ->
                if (!expeditionMode) focusRoute(routeId)
            },
            onStopSelected = { stopId ->
                val stop = selectedRoute?.stops?.firstOrNull { it.id == stopId }
                if (stop != null) {
                    val location = userLocation
                    val isNear = expeditionMode && location != null &&
                        distanceMeters(location.first, location.second, stop.latitude, stop.longitude) <= STOP_REACHED_RADIUS_METERS
                    if (isNear) onStopReached?.invoke(stop) else selectedStop = stop
                }
            },
            completedStopIds = solvedStopIds,
            modifier = Modifier.fillMaxSize(),
            onUserLocationChanged = if (expeditionMode) {
                { latitude, longitude -> userLocation = latitude to longitude }
            } else {
                null
            },
            language = language
        )

        LaunchedEffect(expeditionMode, selectedRoute, userLocation, solvedStopIds, triggeredStopIds) {
            val location = userLocation ?: return@LaunchedEffect
            val route = selectedRoute ?: return@LaunchedEffect
            if (!expeditionMode || onStopReached == null) return@LaunchedEffect
            val reached = route.stops.firstOrNull { stop ->
                stop.id !in solvedStopIds &&
                    stop.id !in triggeredStopIds &&
                    distanceMeters(location.first, location.second, stop.latitude, stop.longitude) <= STOP_REACHED_RADIUS_METERS
            } ?: return@LaunchedEffect
            onStopReached(reached)
        }

        if (expeditionMode) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .statusBarsPadding()
                    .size(44.dp)
                    .background(PineContainer.copy(alpha = 0.92f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = strings.back,
                    tint = Color.White
                )
            }

            AnimatedVisibility(
                visible = showApproachBanner,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PineContainer.copy(alpha = 0.96f),
                    shadowElevation = 6.dp
                ) {
                    Text(
                        strings.approachStop,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (routes.isEmpty()) {
            Surface(
                modifier = Modifier.align(Alignment.Center),
                shape = RoundedCornerShape(12.dp),
                color = PineContainer.copy(alpha = 0.94f)
            ) {
                Text(
                    strings.noRoutes,
                    modifier = Modifier.padding(14.dp),
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
        }

        if (!expeditionMode) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val cardWidth = 320.dp.coerceAtMost(maxWidth - 32.dp)
                    val sidePadding = ((maxWidth - cardWidth) / 2).coerceAtLeast(16.dp)
                    LazyRow(
                        state = listState,
                        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                        contentPadding = PaddingValues(horizontal = sidePadding),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(routes, key = { _, route -> route.id }) { _, route ->
                            val focused = route.id == activeRouteId
                            val cardColor = if (focused) TerracottaGold else PineContainer.copy(alpha = 0.96f)
                            val textColor = if (focused) PineDeep else Color.White
                            Surface(
                                modifier = Modifier
                                    .width(cardWidth)
                                    .heightIn(min = 96.dp),
                                shape = RoundedCornerShape(18.dp),
                                color = cardColor,
                                shadowElevation = 8.dp,
                                border = if (focused) null else ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(TerracottaGold.copy(alpha = 0.55f))
                                )
                            ) {
                        Row(
                            modifier = Modifier
                                .clickable { focusRoute(route.id) }
                                .padding(start = 0.dp, top = 0.dp, bottom = 0.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RouteCoverImage(
                                url = route.imageUrl,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                        Text(
                                            route.title,
                                            color = textColor,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            "${route.location} • ${route.distance} • ${route.duration}",
                                            color = textColor.copy(alpha = 0.82f),
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            "${route.stops.size} ${strings.stopsLabel} • ${difficultyLabel(route.difficulty, language)}",
                                            color = textColor.copy(alpha = 0.72f),
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            activeRouteId = route.id
                                            onSelectRoute(route)
                                        }
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "${strings.viewDetailOf} ${route.title}",
                                            tint = textColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedStop?.let { stop ->
            AlertDialog(
                onDismissRequest = { selectedStop = null },
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stop.title)
                        Text(stop.locationName, color = TerracottaFixedDim, fontSize = 13.sp)
                    }
                },
                text = null,
                confirmButton = {
                    TextButton(onClick = { selectedStop = null }) {
                        Text(strings.close)
                    }
                },
                containerColor = PineContainer,
                titleContentColor = Color.White,
                textContentColor = Color.White
            )
        }
    }
}
