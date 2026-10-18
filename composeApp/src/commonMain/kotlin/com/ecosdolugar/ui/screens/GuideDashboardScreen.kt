package com.ecosdolugar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.model.Category
import com.ecosdolugar.model.Difficulty
import com.ecosdolugar.model.FieldEnigma
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.ui.theme.*

@Composable
fun GuideDashboardScreen(
    routes: List<RouteItem>,
    onBack: () -> Unit,
    onCreateNewRoute: () -> Unit,
    onEditRoute: (RouteItem) -> Unit,
    onPublishDraft: (RouteItem) -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PineDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text("PANEL DE GUÍA OFICIAL", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TerracottaFixedDim, letterSpacing = 1.sp)
                IconButton(onClick = onCreateNewRoute) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva Ruta", tint = TerracottaGold)
                }
            }

            // Stats Metrics
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("RUTAS PUBLICADAS", fontSize = 10.sp, color = TerracottaFixedDim)
                        Text(
                            routes.count { it.status == "publicada" }.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("PENDIENTES DE REVISIÓN", fontSize = 10.sp, color = TerracottaFixedDim)
                        Text(
                            routes.count { it.status == "pendiente" }.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("EXPLORADORES", fontSize = 10.sp, color = MossContainer)
                        Text("238", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Surface(
                onClick = onCreateNewRoute,
                shape = RoundedCornerShape(16.dp),
                color = PineMoss.copy(alpha = 0.3f),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MossContainer)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = MossContainer, modifier = Modifier.size(28.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Crear nueva ruta o leyenda", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text("Define paradas, enigmas, audionarraciones y precio", fontSize = 12.sp, color = SurfaceContainerHighest)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                }
            }

            if (routes.isNotEmpty()) {
                Text("TUS RUTAS", fontSize = 11.sp, color = TerracottaGold, fontWeight = FontWeight.Bold)
                routes.forEach { route ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(route.title, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                "${route.stops.size} paradas · ${route.location} · " +
                                    when (route.status) {
                                        "borrador" -> "Borrador"
                                        "pendiente" -> "Pendiente de verificación"
                                        else -> "Publicada"
                                    },
                                color = SurfaceContainerHighest,
                                fontSize = 12.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { onEditRoute(route) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = SurfaceCream
                                    )
                                ) {
                                    Text(if (route.status == "borrador") "Continuar borrador" else "Editar ruta")
                                }
                                if (route.status == "borrador") {
                                    Button(
                                        onClick = { onPublishDraft(route) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = TerracottaGold,
                                            contentColor = PineDeep
                                        )
                                    ) {
                                        Text("Publicar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
