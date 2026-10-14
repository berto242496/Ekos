package com.ecosdolugar.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop

@Composable
expect fun RouteMapView(
    routes: List<RouteItem>,
    selectedRouteId: String?,
    selectedStopId: String?,
    onRouteSelected: (String) -> Unit,
    onStopSelected: (String) -> Unit,
    modifier: Modifier = Modifier
)
