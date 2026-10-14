package com.ecosdolugar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ecosdolugar.composeapp.generated.resources.Res
import ecosdolugar.composeapp.generated.resources.hero_forest
import org.jetbrains.compose.resources.painterResource
import com.ecosdolugar.ui.theme.*

@Composable
fun WelcomeScreen(
    onSignInWithGoogle: () -> Unit,
    isSignInLoading: Boolean,
    signInError: String?,
    onOpenGuideMode: () -> Unit,
    onOpenMap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PineDeep)
    ) {
        Image(
            painter = painterResource(Res.drawable.hero_forest),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to PineDeep.copy(alpha = 0.78f),
                            0.42f to PineDeep.copy(alpha = 0.48f),
                            1f to PineDeep
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            0.5f to Color.Transparent,
                            1f to PineDeep.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            WelcomeHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(top = 28.dp)
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = maxHeight)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 38.dp, bottom = 22.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                Text(
                    text = "Haz visitable\nlo invisible",
                    color = SurfaceCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 33.sp,
                    lineHeight = 37.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(11.dp))
                Text(
                    text = "Descubre la historia, mitología y tesoros ocultos de toda Galicia a través de rutas digitales interactivas y relatos sonoros.",
                    color = SurfaceContainerHigh,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(20.dp))
                WelcomeMetrics()
                Spacer(Modifier.height(20.dp))
                GoogleSignInButton(onClick = onSignInWithGoogle, isLoading = isSignInLoading)
                Spacer(Modifier.height(8.dp))
                PublicMapButton(onClick = onOpenMap)
                if (signInError != null) {
                    Text(
                        text = signInError,
                        color = Color(0xFFFFD5D5),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
                Spacer(Modifier.height(9.dp))
                WelcomeTrustNotice()
                Spacer(Modifier.height(14.dp))
                GuideDivider()
                Spacer(Modifier.height(10.dp))
                GuideCallToAction(onClick = onOpenGuideMode)
                Spacer(Modifier.height(13.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TerracottaFixedDim,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "A Coruña · Lugo · Ourense · Pontevedra — Arquivo Vivo",
                        color = OutlineVariant.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
}

@Composable
private fun PublicMapButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.12f),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.3f))
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Map, contentDescription = null, tint = TerracottaFixedDim)
            Spacer(Modifier.width(10.dp))
            Text(
                "Explorar el mapa sin registrarme",
                color = SurfaceCream,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun WelcomeHeader(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.12f),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.13f))
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ECOS DO LUGAR",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    maxLines = 1
                )
                Text(
                    "RUTAS & PATRIMONIO DE TODA GALICIA",
                    color = SurfaceContainerHigh,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.35.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun WelcomeMetrics() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetricColumn("PROVINCIAS", "4", Modifier.weight(1f).padding(start = 11.dp))
        Box(Modifier.width(1.dp).height(38.dp).background(Color.White.copy(alpha = 0.12f)))
        MetricColumn("RUTAS MÍTICAS", "7", Modifier.weight(1f).padding(start = 11.dp))
        Box(Modifier.width(1.dp).height(38.dp).background(Color.White.copy(alpha = 0.12f)))
        MetricColumn("GEOGRAFÍA", "Toda Galicia", Modifier.weight(1.18f).padding(start = 11.dp))
    }
}

@Composable
private fun MetricColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, color = SurfaceContainerHigh, fontSize = 9.sp, maxLines = 1)
        Text(
            value,
            color = if (value == "Toda Galicia") TerracottaFixedDim else Color.White,
            fontFamily = FontFamily.Serif,
            fontSize = if (value == "Toda Galicia") 15.sp else 18.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit, isLoading: Boolean) {
    Surface(
        onClick = onClick,
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))
        ),
        shadowElevation = 5.dp,
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = PineDeep,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(11.dp))
                Text(
                    "Conectando con Google...",
                    color = Color(0xFF1F1F1F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text("G", color = Color(0xFF4285F4), fontSize = 21.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(11.dp))
                Column {
                    Text(
                        "Entrar con Google para elegir personaje",
                        color = Color(0xFF1F1F1F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 15.sp
                    )
                    Text(
                        "Esencial para despertar tu don mágico y resolver las pruebas",
                        color = Color(0xFF666666),
                        fontSize = 8.sp,
                        lineHeight = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeTrustNotice() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(9.dp))
            .background(Color(0xFF032A1D).copy(alpha = 0.86f))
            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.25f), RoundedCornerShape(9.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.Shield,
            contentDescription = null,
            tint = Color(0xFF34D399),
            modifier = Modifier.size(15.dp)
        )
        Text(
            "Cada ruta requiere la sintonía de un personaje mitológico para desvelar sus secretos.",
            color = Color(0xFFD1FAE5),
            fontSize = 10.sp,
            lineHeight = 13.sp
        )
    }
}

@Composable
private fun GuideDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.16f))
        Text(
            "¿ERES GUÍA LOCAL?",
            modifier = Modifier.padding(horizontal = 10.dp),
            color = TerracottaFixed.copy(alpha = 0.75f),
            fontSize = 9.sp,
            fontFamily = FontFamily.Serif,
            letterSpacing = 0.5.sp
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.16f))
    }
}

@Composable
private fun GuideCallToAction(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = TerracottaGold.copy(alpha = 0.2f),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(TerracottaGold.copy(alpha = 0.48f))
        ),
        modifier = Modifier.fillMaxWidth().height(44.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Explore,
                contentDescription = null,
                tint = TerracottaFixedDim,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Soy guía · Crea y monetiza tus rutas",
                color = SurfaceCream,
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
