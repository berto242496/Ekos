package com.ecosdolugar.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop

@Composable
expect fun RouteMapView(
    routes: List<RouteItem>,
    selectedRouteId: String?,
    selectedStopId: String?,
    onRouteSelected: (String) -> Unit,
    onStopSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    onCoordinateSelected: ((Double, Double) -> Unit)? = null,
    selectedCoordinates: Pair<Double, Double>? = null,
    pickerCoordinates: List<Pair<Double, Double>> = emptyList(),
    onUserLocationChanged: ((Double, Double) -> Unit)? = null,
    language: AppLanguage = AppLanguage.SPANISH
)
