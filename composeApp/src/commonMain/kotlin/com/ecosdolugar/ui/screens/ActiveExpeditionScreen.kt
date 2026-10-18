package com.ecosdolugar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActiveExpeditionScreen(
    route: RouteItem,
    userProfile: UserProfile,
    initialStopId: String? = null,
    onBack: () -> Unit,
    onCheckInStop: (String) -> Unit,
    onSolveEnigma: (String, Int) -> Unit,
    onAddNote: (RouteStop) -> Unit,
    onCompleteRoute: (String) -> Unit
) {
    var currentStopIndex by remember(route.id, initialStopId) {
        mutableIntStateOf(route.stops.indexOfFirst { it.id == initialStopId }.coerceAtLeast(0))
    }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var isDonActive by remember { mutableStateOf(false) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var attempts by remember(currentStopIndex) { mutableIntStateOf(0) }
    var showExplanation by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    val challengeBringIntoViewRequester = remember { BringIntoViewRequester() }

    val character = remember(userProfile.characterId) {
        CharactersRepository.getById(userProfile.characterId)
    }
    val charColor = Color(character.accentColorHex)

    val currentStop = route.stops.getOrElse(currentStopIndex) { route.stops.first() }
    val isVisited = userProfile.visitedStopIds.contains(currentStop.id)
    val isSolved = userProfile.solvedEnigmaIds.contains(currentStop.id)

    val scrollState = rememberScrollState()

    // Reset trial selections when stop changes
    LaunchedEffect(currentStopIndex) {
        isPlayingAudio = false
        isDonActive = false
        selectedOptionIndex = null
        showExplanation = false
        isAnswerCorrect = false
    }
    LaunchedEffect(currentStop.id, initialStopId) {
        if (currentStop.id == initialStopId) {
            challengeBringIntoViewRequester.bringIntoView()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PineDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "EXPEDICIÓN EN VIVO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TerracottaFixedDim,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = route.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                // Companion Avatar Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(charColor.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, charColor, CircleShape)
                        .clickable { isDonActive = !isDonActive },
                    contentAlignment = Alignment.Center
                ) {
                    Text(character.avatarEmoji, fontSize = 18.sp)
                }
            }

            // Route Stops Stepper Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(route.stops) { idx, stop ->
                    val isActive = idx == currentStopIndex
                    val visited = userProfile.visitedStopIds.contains(stop.id)

                    Surface(
                        onClick = { currentStopIndex = idx },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isActive) charColor.copy(alpha = 0.25f)
                        else if (visited) PineMoss.copy(alpha = 0.4f)
                        else Color.Black.copy(alpha = 0.3f),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (isActive) charColor else Color.White.copy(alpha = 0.15f)
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${idx + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) charColor else Color.White
                            )
                            if (visited) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Completada",
                                    tint = MossContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Stop Info Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F2618),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.verticalGradient(
                        listOf(charColor.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Stop
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = charColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "PARADA ${currentStopIndex + 1} DE ${route.stops.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = charColor,
                                letterSpacing = 1.sp
                            )
                        }

                        // Audio button
                        Button(
                            onClick = { isPlayingAudio = !isPlayingAudio },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingAudio) charColor else Color.White.copy(alpha = 0.12f),
                                contentColor = if (isPlayingAudio) Color.Black else Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                if (isPlayingAudio) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isPlayingAudio) "Pausar voz" else "Escuchar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = currentStop.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = currentStop.locationName,
                        fontSize = 12.sp,
                        color = TerracottaFixedDim
                    )

                    // Story Narrative
                    Text(
                        text = currentStop.story,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = SurfaceCream
                    )

                    // Historical Note
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.25f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = TerracottaGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "NOTA HISTÓRICA DO LUGAR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TerracottaGold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = currentStop.historicalNote,
                                    fontSize = 12.sp,
                                    color = SurfaceContainerHighest,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    // Check-in button
                    if (!isVisited) {
                        Button(
                            onClick = { onCheckInStop(currentStop.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MossContainer,
                                contentColor = PineDeep
                            )
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Registrar llegada a este punto", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MossContainer.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MossContainer, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Punto visitado y registrado", color = MossContainer, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Character Companion advice card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.3f),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(charColor.copy(alpha = 0.3f))
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(character.avatarEmoji, fontSize = 28.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CONSELLO DE ${character.name.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = charColor
                        )
                        Text(
                            text = character.quote,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = SurfaceCream
                        )
                    }
                    IconButton(
                        onClick = { isDonActive = !isDonActive },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = charColor)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = "Activar Don")
                    }
                }
            }

            // Don Activo Banner
            AnimatedVisibility(visible = isDonActive) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = charColor.copy(alpha = 0.15f),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(charColor)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "✨ DON ACTIVADO: ${character.abilityName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = charColor
                        )
                        Text(
                            text = "Pista arcana: Observa detenidamente las piedras talladas y las inscripciones del contorno.",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Enigma / Challenge Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .bringIntoViewRequester(challengeBringIntoViewRequester),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF141F17),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(TerracottaGold.copy(alpha = 0.4f))
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🧩", fontSize = 20.sp)
                        Text(
                            text = "DESAFÍO RAIA: ENIGMA DO LUGAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TerracottaGold,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = currentStop.enigma.question,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        lineHeight = 20.sp
                    )

                    val trial = currentStop.trial
                    if (trial != null && trial.options.isNotEmpty()) {
                        trial.options.forEachIndexed { optIndex, optionText ->
                            val isSelected = selectedOptionIndex == optIndex
                            val isCorrectOption = optIndex == trial.correctOptionIndex

                            Surface(
                                onClick = {
                                    if (!isSolved) {
                                        selectedOptionIndex = optIndex
                                        attempts += 1
                                        val correct = optIndex == trial.correctOptionIndex
                                        isAnswerCorrect = correct
                                        showExplanation = true
                                        if (correct) {
                                            onSolveEnigma(currentStop.id, attempts)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) {
                                    if (isCorrectOption) MossContainer.copy(alpha = 0.2f)
                                    else Color(0xFF6B1D1D).copy(alpha = 0.3f)
                                } else Color.Black.copy(alpha = 0.25f),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        if (isSelected) {
                                            if (isCorrectOption) MossContainer else Color(0xFFFF6B6B)
                                        } else Color.White.copy(alpha = 0.1f)
                                    )
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "${('A' + optIndex)}.",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TerracottaFixedDim
                                    )
                                    Text(
                                        text = optionText,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            if (isCorrectOption) Icons.Default.CheckCircle else Icons.Default.Close,
                                            contentDescription = null,
                                            tint = if (isCorrectOption) MossContainer else Color(0xFFFF6B6B)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Simple fallback enigma
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Pista para resolver:", fontSize = 11.sp, color = TerracottaFixedDim, fontWeight = FontWeight.Bold)
                                Text(currentStop.enigma.hint, fontSize = 12.sp, color = SurfaceCream)
                            }
                        }

                        if (!isSolved) {
                            Button(
                                onClick = {
                                    attempts += 1
                                    onSolveEnigma(currentStop.id, attempts)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaGold, contentColor = PineDeep)
                            ) {
                                Text("Revelar solución y resolver", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (isSolved || showExplanation) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MossContainer.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MossContainer, modifier = Modifier.size(16.dp))
                                    Text("¡Enigma superado con éxito!", color = MossContainer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Text("Respuesta: ${currentStop.enigma.answer}", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Expedition Navigation Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (currentStopIndex > 0) currentStopIndex--
                    },
                    enabled = currentStopIndex > 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Anterior")
                }

                if (currentStopIndex < route.stops.size - 1) {
                    Button(
                        onClick = { currentStopIndex++ },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = charColor, contentColor = Color.Black)
                    ) {
                        Text("Siguiente parada", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                } else {
                    Button(
                        onClick = { onCompleteRoute(route.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MossContainer, contentColor = PineDeep)
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Completar expedición", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
