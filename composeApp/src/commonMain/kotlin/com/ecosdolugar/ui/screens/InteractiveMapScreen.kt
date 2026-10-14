package com.ecosdolugar.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.material.icons.Icons
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
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.ui.components.RouteMapView
import com.ecosdolugar.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InteractiveMapScreen(
    routes: List<RouteItem>,
    onSelectRoute: (RouteItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeRouteId by remember { mutableStateOf<String?>(null) }
    var selectedStop by remember { mutableStateOf<RouteStop?>(null) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val selectedRoute = routes.firstOrNull { it.id == activeRouteId }
    val routeIds = routes.map { it.id }

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFD4E4E9))
    ) {
        RouteMapView(
            routes = routes,
            selectedRouteId = activeRouteId,
            selectedStopId = selectedStop?.id,
            onRouteSelected = ::focusRoute,
            onStopSelected = { stopId ->
                selectedStop = selectedRoute?.stops?.firstOrNull { it.id == stopId }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (routes.isEmpty()) {
            Surface(
                modifier = Modifier.align(Alignment.Center),
                shape = RoundedCornerShape(12.dp),
                color = PineContainer.copy(alpha = 0.94f)
            ) {
                Text(
                    "No hay rutas disponibles para mostrar.",
                    modifier = Modifier.padding(14.dp),
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
        }

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
                                    .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .padding(vertical = 2.dp),
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
                                        "${route.stops.size} paradas • ${route.difficulty.label}",
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
                                        contentDescription = "Ver detalle de ${route.title}",
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
}
