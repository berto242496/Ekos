package com.ecosdolugar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.RoutesRepository
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.ui.theme.*

@Composable
fun InteractiveMapScreen(
    routes: List<RouteItem>,
    onSelectRoute: (RouteItem) -> Unit,
    onStartExpedition: (RouteItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRoute by remember { mutableStateOf(routes.firstOrNull() ?: RoutesRepository.routes.first()) }
    var selectedStop by remember { mutableStateOf<RouteStop?>(null) }
    var zoomLevel by remember { mutableStateOf(1f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PineDeep)
    ) {
        // Map Top Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(16.dp),
            color = PineContainer.copy(alpha = 0.9f),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = TerracottaGold)
                    Text("Cartografía Raiana", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2.5f) }) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White)
                    }
                    IconButton(onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.8f) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White)
                    }
                }
            }
        }

        // Stylized Canvas Map (Miño River & Historical Markers)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 120.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                val w = size.width
                val h = size.height

                // River Rio Miño line
                val riverColor = Color(0xFF1E3A4C)
                drawLine(
                    color = riverColor,
                    start = Offset(w * 0.1f, h * 0.2f),
                    end = Offset(w * 0.9f, h * 0.8f),
                    strokeWidth = 14.dp.toPx()
                )

                // Bridge connecting Spain and Portugal
                drawLine(
                    color = TerracottaGold.copy(alpha = 0.8f),
                    start = Offset(w * 0.45f, h * 0.42f),
                    end = Offset(w * 0.55f, h * 0.52f),
                    strokeWidth = 4.dp.toPx()
                )
            }

            // Route Stops Markers
            selectedRoute.stops.forEachIndexed { idx, stop ->
                val xPercent = stop.mapPositionX / 100f
                val yPercent = stop.mapPositionY / 100f

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(
                                x = (xPercent * 300).dp,
                                y = (yPercent * 340).dp
                            )
                            .size(38.dp)
                            .background(
                                if (selectedStop?.id == stop.id) TerracottaGold else PineContainer,
                                CircleShape
                            )
                            .border(2.dp, Color.White, CircleShape)
                            .clickable {
                                selectedStop = stop
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedStop?.id == stop.id) PineDeep else Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Bottom Selected Stop or Route Info Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F2618),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(TerracottaGold.copy(alpha = 0.5f))
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (selectedStop != null) {
                    val stop = selectedStop!!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(stop.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(stop.locationName, fontSize = 12.sp, color = TerracottaFixedDim)
                        }
                        IconButton(onClick = { selectedStop = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                        }
                    }
                    Text(stop.story, fontSize = 12.sp, maxLines = 2, color = SurfaceCream)
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(selectedRoute.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${selectedRoute.distance} • ${selectedRoute.duration} • ${selectedRoute.stops.size} paradas", fontSize = 12.sp, color = TerracottaFixedDim)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onSelectRoute(selectedRoute) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))
                    ) {
                        Text("Ver detalles")
                    }

                    Button(
                        onClick = { onStartExpedition(selectedRoute) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaGold, contentColor = PineDeep)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Iniciar ruta", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
