package com.ecosdolugar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.data.RoutesRepository
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.ui.theme.*

@Composable
fun JournalScreen(
    userProfile: UserProfile,
    onBack: () -> Unit,
    onOpenRoute: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val character = remember(userProfile.characterId) {
        CharactersRepository.getById(userProfile.characterId)
    }
    val charColor = Color(character.accentColorHex)

    val savedRoutes = remember(userProfile.savedRouteIds) {
        RoutesRepository.routes.filter { userProfile.savedRouteIds.contains(it.id) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PineDeep)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF0F2618),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(charColor.copy(alpha = 0.5f))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(charColor.copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, charColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(character.avatarEmoji, fontSize = 28.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Compañero: ${character.name} (${character.galicianTitle})",
                                fontSize = 12.sp,
                                color = charColor
                            )
                            Text(
                                text = if (userProfile.isGoogleAuthenticated) userProfile.email else "Modo Explorador Libre",
                                fontSize = 11.sp,
                                color = SurfaceContainerHighest
                            )
                        }
                    }
                }
            }

            if (userProfile.isGoogleAuthenticated) {
                item {
                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }

            // Stats Badges Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.3f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("PARADAS", fontSize = 10.sp, color = TerracottaFixedDim, fontWeight = FontWeight.Bold)
                            Text("${userProfile.visitedStopIds.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.3f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ENIGMAS", fontSize = 10.sp, color = MossContainer, fontWeight = FontWeight.Bold)
                            Text("${userProfile.solvedEnigmaIds.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.3f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("GUARDADAS", fontSize = 10.sp, color = TerracottaGold, fontWeight = FontWeight.Bold)
                            Text("${savedRoutes.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Saved Routes Header
            item {
                Text(
                    text = "RUTAS GUARDADAS EN TU CUADERNO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TerracottaFixedDim,
                    letterSpacing = 1.sp
                )
            }

            if (savedRoutes.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Aún no has guardado ninguna ruta en tu cuaderno de campo.",
                            modifier = Modifier.padding(20.dp),
                            color = SurfaceCream,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(savedRoutes) { route ->
                    Surface(
                        onClick = { onOpenRoute(route.id) },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF14241B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = TerracottaGold)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(route.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("${route.distance} • ${route.duration}", fontSize = 11.sp, color = SurfaceContainerHighest)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
