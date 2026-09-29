package com.ecosdolugar.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.ui.theme.*

@Composable
fun WelcomeScreen(
    onSignInWithGoogle: () -> Unit,
    onEnterGuest: () -> Unit,
    onOpenCharacterSelect: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val compassAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(90000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PineDeep)
    ) {
        // Multi-tier Atmospheric Gradient Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PineDeep.copy(alpha = 0.85f),
                            PineDeep.copy(alpha = 0.45f),
                            PineDeep
                        )
                    )
                )
        )

        // Editorial Ambient Compass Watermark
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 60.dp)
                .rotate(compassAngle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = null,
                tint = TerracottaFixedDim.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxSize()
            )
        }

        // Screen Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Tier: Logo & Region Subtitle
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ECOS DO LUGAR",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Box(modifier = Modifier.size(4.dp).background(TerracottaFixedDim, CircleShape))
                        Text(
                            text = "RUTAS & PATRIMONIO OCULTO",
                            color = SurfaceContainerHigh,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.5.sp
                        )
                        Box(modifier = Modifier.size(4.dp).background(TerracottaFixedDim, CircleShape))
                    }
                }
            }

            // Narrative & Tactical Micro-Metrics Foundation
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cultural Heritage Pill
                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerHighest.copy(alpha = 0.20f),
                    modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = TerracottaFixedDim,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "TURISMO CULTURAL VIVO",
                            color = TerracottaFixed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Poetic Headline
                Text(
                    text = "Haz visitable\nlo invisible",
                    color = Color.White,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold
                    ),
                    lineHeight = 40.sp
                )

                // Editorial Excerpt
                Text(
                    text = "Descubre historias, leyendas y patrimonio oculto a través de rutas digitales interactivas.",
                    color = SurfaceContainerHigh,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                // Tactical Micro-Metrics of the Borderland
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.10f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("RUTAS SECRETAS", color = SurfaceContainerHighest, fontSize = 10.sp, letterSpacing = 1.sp)
                            Text("42", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("LEYENDAS ORALES", color = SurfaceContainerHighest, fontSize = 10.sp, letterSpacing = 1.sp)
                            Text("118", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("FRONTERA VIVA", color = SurfaceContainerHighest, fontSize = 10.sp, letterSpacing = 1.sp)
                            Text("A Raia", color = TerracottaFixedDim, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Action Stack - Exclusivo Google Sign-In & Elige tu personaje
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Primary Action: Exclusively Google Sign-In
                    Surface(
                        onClick = onSignInWithGoogle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF4285F4)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Continuar con Google",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color(0xFF1F1F1F)
                            )
                        }
                    }

                    // Secondary Action: Choose Character Button
                    OutlinedButton(
                        onClick = onOpenCharacterSelect,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(
                                listOf(TerracottaFixedDim.copy(alpha = 0.6f), Color.White.copy(alpha = 0.2f))
                            )
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = SurfaceContainerHighest.copy(alpha = 0.15f)
                        )
                    ) {
                        Text("✨", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Elige o teu personaxe (Meiga, Trasno...)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    // Subtle Guest Access
                    TextButton(
                        onClick = onEnterGuest,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Entrar como visitante libre", color = SurfaceContainerHighest, fontSize = 13.sp)
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = SurfaceContainerHighest,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Fine Print Coordinates
                Text(
                    text = "Galicia • Norte de Portugal — Arquivo Vivo",
                    color = OutlineVariant.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
