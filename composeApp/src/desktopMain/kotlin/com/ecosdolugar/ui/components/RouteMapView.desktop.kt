package com.ecosdolugar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import kotlin.math.max

@Composable
actual fun RouteMapView(
    routes: List<RouteItem>,
    selectedRouteId: String?,
    selectedStopId: String?,
    onRouteSelected: (String) -> Unit,
    onStopSelected: (String) -> Unit,
    modifier: Modifier,
    onCoordinateSelected: ((Double, Double) -> Unit)?,
    selectedCoordinates: Pair<Double, Double>?,
    pickerCoordinates: List<Pair<Double, Double>>,
    onUserLocationChanged: ((Double, Double) -> Unit)?,
    completedStopIds: Set<String>,
    language: AppLanguage
) {
    val selectedRoute = routes.firstOrNull { it.id == selectedRouteId }
    val selectedStops = selectedRoute?.stops.orEmpty().filter { it.latitude.isFinite() && it.longitude.isFinite() }
    val starts = routes.mapNotNull { route ->
        route.stops.firstOrNull()?.takeIf { it.latitude.isFinite() && it.longitude.isFinite() }?.let { route to it }
    }
    val allPoints = starts.map { it.second } + selectedStops
    val paddingPx = with(LocalDensity.current) { 40.dp.toPx() }
    val minLat = allPoints.minOfOrNull { it.latitude } ?: 42.65
    val maxLat = allPoints.maxOfOrNull { it.latitude } ?: 42.65
    val minLng = allPoints.minOfOrNull { it.longitude } ?: -8.2
    val maxLng = allPoints.maxOfOrNull { it.longitude } ?: -8.2
    val latSpan = max(maxLat - minLat, 0.02)
    val lngSpan = max(maxLng - minLng, 0.02)

    fun position(stop: RouteStop, width: Float, height: Float): Offset {
        val x = ((stop.longitude - minLng) / lngSpan).toFloat()
        val y = (1.0 - (stop.latitude - minLat) / latSpan).toFloat()
        return Offset(
            paddingPx + (width - paddingPx * 2) * x,
            paddingPx + (height - paddingPx * 2) * y
        )
    }

    Canvas(
        modifier = modifier.pointerInput(routes, selectedRouteId) {
            detectTapGestures { tap ->
                if (onCoordinateSelected != null) {
                    val x = ((tap.x - paddingPx) / (size.width - paddingPx * 2)).coerceIn(0f, 1f)
                    val y = ((tap.y - paddingPx) / (size.height - paddingPx * 2)).coerceIn(0f, 1f)
                    onCoordinateSelected(
                        43.8 - y * (43.8 - 41.7),
                        -9.5 + x * (-6.5 - -9.5)
                    )
                    return@detectTapGestures
                }
                val nearestStart = starts.minByOrNull { (_, stop) ->
                    (position(stop, size.width.toFloat(), size.height.toFloat()) - tap).getDistance()
                }
                if (nearestStart != null) {
                    val (route, stop) = nearestStart
                    val center = position(stop, size.width.toFloat(), size.height.toFloat())
                    if ((center - tap).getDistance() <= 48.dp.toPx()) {
                        onRouteSelected(route.id)
                        return@detectTapGestures
                    }
                }
                val nearestStop = selectedStops.minByOrNull { stop ->
                    (position(stop, size.width.toFloat(), size.height.toFloat()) - tap).getDistance()
                }
                if (nearestStop != null) {
                    val center = position(nearestStop, size.width.toFloat(), size.height.toFloat())
                    if ((center - tap).getDistance() <= 48.dp.toPx()) onStopSelected(nearestStop.id)
                }
            }
        }
    ) {
        drawRect(Color(0xFFE4EBDD))
        if (onCoordinateSelected != null) {
            pickerCoordinates.forEachIndexed { index, (latitude, longitude) ->
                val point = Offset(
                    paddingPx + (size.width - paddingPx * 2) * ((longitude + 9.5) / 3.0).toFloat(),
                    paddingPx + (size.height - paddingPx * 2) * ((43.8 - latitude) / 2.1).toFloat()
                )
                drawCircle(Color(0xFF47654E), 11.dp.toPx(), point)
            }
            selectedCoordinates?.let { (latitude, longitude) ->
                val point = Offset(
                    paddingPx + (size.width - paddingPx * 2) * ((longitude + 9.5) / 3.0).toFloat(),
                    paddingPx + (size.height - paddingPx * 2) * ((43.8 - latitude) / 2.1).toFloat()
                )
                drawCircle(Color(0xFFD49B45), 15.dp.toPx(), point)
                drawCircle(Color.White, 15.dp.toPx(), point, style = Stroke(width = 2.dp.toPx()))
            }
            return@Canvas
        }
        if (selectedStops.size > 1) {
            val path = Path()
            selectedStops.forEachIndexed { index, stop ->
                val point = position(stop, size.width, size.height)
                if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
            }
            drawPath(path, Color(0xFFD49B45), style = Stroke(width = 5.dp.toPx()))
        }
        starts.filterNot { (route, _) -> route.id == selectedRouteId }.forEach { (_, stop) ->
            drawCircle(Color(0xFF17452F), 11.dp.toPx(), position(stop, size.width, size.height))
        }
        selectedStops.forEach { stop ->
            drawCircle(
                color = when {
                    stop.id == selectedStopId -> Color(0xFFD49B45)
                    stop.id in completedStopIds -> Color(0xFF3F8A4F)
                    else -> Color(0xFF17452F)
                },
                radius = 16.dp.toPx(),
                center = position(stop, size.width, size.height)
            )
        }
    }
}
