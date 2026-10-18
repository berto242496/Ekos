package com.ecosdolugar.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.CircularProgressIndicator
import kotlin.math.roundToInt
import com.ecosdolugar.ui.components.RouteImageState
import com.ecosdolugar.ui.components.rememberRouteImage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.model.TrialType
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.theme.PineContainer
import com.ecosdolugar.ui.theme.SurfaceCream
import com.ecosdolugar.ui.theme.TerracottaGold

private val FeedbackError = Color(0xFFB3261E)
private val FeedbackErrorBackground = Color(0xFFFBE9E7)

@Composable
internal fun StopTrialContent(
    stop: RouteStop,
    characterId: String,
    isSolved: Boolean,
    onSolved: (attempts: Int) -> Unit,
    onFinish: () -> Unit,
    isRouteCompleted: Boolean = false
) {
    if (isSolved) {
        TrialCompletedContent(stop.id, characterId, onFinish, isRouteCompleted)
        return
    }

    val trial = stop.trial
    val type = trial?.type ?: TrialType.ACERTIJO
    var attempts by remember(stop.id) { mutableIntStateOf(0) }
    var feedback by remember(stop.id) { mutableStateOf<String?>(null) }
    var showHint by remember(stop.id) { mutableStateOf(false) }
    val hint = trial?.generalHint?.takeIf { it.isNotBlank() } ?: stop.enigma.hint
    val title = trial?.title?.takeIf { it.isNotBlank() } ?: "Prueba"
    val question = trial?.question?.takeIf { it.isNotBlank() } ?: stop.enigma.question

    val onAnswer: (Boolean) -> Unit = { correct ->
        attempts += 1
        if (correct) {
            onSolved(attempts)
        } else {
            feedback = "Todavía no es correcto. Puedes volver a intentarlo."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = InkDeep
        )
        Text(text = question, fontSize = 15.sp, lineHeight = 22.sp, color = InkDeep)

        when (type) {
            TrialType.PUZLE_IMAGEN -> {
                val imageUrl = trial?.imageUrl?.takeIf { it.isNotBlank() } ?: stop.imageUrl
                if (imageUrl.isBlank()) {
                    MissingDataNotice("Esta prueba no tiene imagen configurada.")
                } else {
                    PuzzleTrial(
                        stopId = stop.id,
                        imageUrl = imageUrl,
                        gridSize = trial?.puzzleGridSize ?: 3,
                        onAnswer = onAnswer
                    )
                }
            }
            TrialType.ORDENAR_PALABRA -> {
                val answer = trial?.cipherWord?.takeIf { it.isNotBlank() } ?: stop.enigma.answer
                val letters = remember(stop.id) {
                    trial?.scrambledLetters?.takeIf { it.isNotEmpty() } ?: scrambleLetters(answer)
                }
                WordTrial(stopId = stop.id, letters = letters, answer = answer, onAnswer = onAnswer)
            }
            TrialType.COMPLETAR_FRASE -> {
                val sentence = trial?.sentence?.takeIf { it.isNotBlank() } ?: stop.enigma.question
                val missing = trial?.missingWord?.takeIf { it.isNotBlank() } ?: stop.enigma.answer
                SentenceTrial(stopId = stop.id, sentence = sentence, missingWord = missing, onAnswer = onAnswer)
            }
            else -> {
                val options = trial?.options.orEmpty()
                if (options.isNotEmpty()) {
                    OptionsTrial(
                        stopId = stop.id,
                        options = options,
                        correctIndex = trial?.correctOptionIndex ?: 0,
                        onAnswer = onAnswer
                    )
                } else {
                    TextAnswerTrial(
                        stopId = stop.id,
                        expected = stop.enigma.answer,
                        label = "Tu respuesta",
                        onAnswer = onAnswer
                    )
                }
            }
        }

        feedback?.let { message ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = FeedbackErrorBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message,
                    color = FeedbackError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        if (hint.isNotBlank()) {
            TextButton(onClick = { showHint = !showHint }) {
                Text(if (showHint) "Ocultar pista" else "Ver pista", color = PineContainer, fontWeight = FontWeight.SemiBold)
            }
            if (showHint) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, FieldBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(hint, color = InkDeep, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
private fun MissingDataNotice(message: String) {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = BorderStroke(1.dp, FieldBorder), modifier = Modifier.fillMaxWidth()) {
        Text(message, color = InkMuted, fontSize = 13.sp, modifier = Modifier.padding(14.dp))
    }
}

@Composable
private fun TrialCompletedContent(stopId: String, characterId: String, onFinish: () -> Unit, isRouteCompleted: Boolean) {
    val character = remember(characterId) { CharactersRepository.getById(characterId) }
    val accent = Color(character.accentColorHex)
    val message = remember(stopId) { stopCompletionTexts.random() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(accent.copy(alpha = 0.2f), CircleShape)
                .border(2.dp, accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(character.avatarEmoji, fontSize = 56.sp)
        }
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, FieldBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(character.name.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = InkMuted)
                Spacer(Modifier.height(6.dp))
                Text(message, fontSize = 16.sp, color = InkDeep, textAlign = TextAlign.Center, lineHeight = 22.sp)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PineContainer, modifier = Modifier.size(18.dp))
            Text("Prueba completada", color = PineContainer, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
        ) {
            Text(if (isRouteCompleted) "Finalizar ruta" else "Finalizar prueba", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OptionsTrial(
    stopId: String,
    options: List<String>,
    correctIndex: Int,
    onAnswer: (Boolean) -> Unit
) {
    val order = remember(stopId) { options.indices.shuffled() }
    val wrong = remember(stopId) { mutableStateListOf<Int>() }

    order.forEachIndexed { position, optionIndex ->
        val isWrong = optionIndex in wrong
        Surface(
            onClick = {
                if (optionIndex == correctIndex) {
                    onAnswer(true)
                } else {
                    wrong.add(optionIndex)
                    onAnswer(false)
                }
            },
            enabled = !isWrong,
            shape = RoundedCornerShape(12.dp),
            color = if (isWrong) FeedbackErrorBackground else Color.White,
            border = BorderStroke(1.dp, if (isWrong) FeedbackError else FieldBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("${'A' + position}.", fontWeight = FontWeight.Bold, color = InkMuted)
                Text(options[optionIndex], color = InkDeep, modifier = Modifier.weight(1f))
                if (isWrong) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = FeedbackError)
                }
            }
        }
    }
}

@Composable
private fun TextAnswerTrial(
    stopId: String,
    expected: String,
    label: String,
    onAnswer: (Boolean) -> Unit
) {
    var input by remember(stopId) { mutableStateOf("") }
    OutlinedTextField(
        value = input,
        onValueChange = { input = it },
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Button(
        onClick = { onAnswer(normalizeAnswer(input) == normalizeAnswer(expected)) },
        enabled = input.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
    ) {
        Text("Comprobar", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SentenceTrial(
    stopId: String,
    sentence: String,
    missingWord: String,
    onAnswer: (Boolean) -> Unit
) {
    val index = if (missingWord.isBlank()) -1 else sentence.indexOf(missingWord, ignoreCase = true)
    val text = if (index >= 0) {
        sentence.substring(0, index) + "_____" + sentence.substring(index + missingWord.length)
    } else {
        sentence
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, FieldBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            lineHeight = 26.sp,
            color = InkDeep,
            modifier = Modifier.padding(18.dp)
        )
    }
    TextAnswerTrial(stopId = stopId, expected = missingWord, label = "Palabra que falta", onAnswer = onAnswer)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordTrial(
    stopId: String,
    letters: List<String>,
    answer: String,
    onAnswer: (Boolean) -> Unit
) {
    var picked by remember(stopId) { mutableStateOf(listOf<Int>()) }
    val built = picked.joinToString("") { letters[it] }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, FieldBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (picked.isEmpty()) {
                Text("Toca las letras en orden", color = InkMuted, fontSize = 13.sp, modifier = Modifier.padding(8.dp))
            }
            picked.forEachIndexed { slot, letterIndex ->
                LetterTile(letter = letters[letterIndex], placed = true) {
                    picked = picked.filterIndexed { i, _ -> i != slot }
                }
            }
        }
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        letters.forEachIndexed { index, letter ->
            val used = index in picked
            LetterTile(letter = letter, used = used) {
                picked = picked + index
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextButton(onClick = { picked = emptyList() }, enabled = picked.isNotEmpty()) {
            Text("Borrar", color = PineContainer, fontWeight = FontWeight.SemiBold)
        }
        Button(
            onClick = { onAnswer(normalizeAnswer(built) == normalizeAnswer(answer)) },
            enabled = picked.size == letters.size,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
        ) {
            Text("Comprobar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LetterTile(
    letter: String,
    placed: Boolean = false,
    used: Boolean = false,
    onClick: () -> Unit
) {
    val container = when {
        placed -> Color.White
        used -> PineContainer.copy(alpha = 0.25f)
        else -> PineContainer
    }
    Surface(
        onClick = onClick,
        enabled = !used,
        shape = RoundedCornerShape(10.dp),
        color = container,
        contentColor = if (placed) InkDeep else SurfaceCream,
        border = if (placed) BorderStroke(1.dp, PineContainer) else null,
        modifier = Modifier.size(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(letter.uppercase(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun PuzzleTrial(
    stopId: String,
    imageUrl: String,
    gridSize: Int,
    onAnswer: (Boolean) -> Unit
) {
    val grid = gridSize.coerceIn(2, 5)
    val total = grid * grid
    val solvedOrder = remember(total) { (0 until total).toList() }
    var order by remember(stopId, total) { mutableStateOf(shuffledPuzzle(total)) }
    var dragFrom by remember(stopId, total) { mutableIntStateOf(-1) }
    var dragOffsetX by remember(stopId, total) { mutableFloatStateOf(0f) }
    var dragOffsetY by remember(stopId, total) { mutableFloatStateOf(0f) }
    val imageState by rememberRouteImage(imageUrl)
    val density = LocalDensity.current

    val ready = imageState as? RouteImageState.Ready
    if (ready == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(PuzzleBackground),
            contentAlignment = Alignment.Center
        ) {
            if (imageState is RouteImageState.Failed) {
                Text("No se pudo cargar la imagen", color = SurfaceCream)
            } else {
                CircularProgressIndicator(color = TerracottaGold)
            }
        }
    } else {
        val bitmap = ready.bitmap
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, FieldBorder, RoundedCornerShape(16.dp))
        ) {
            val tile = maxWidth / grid
            val tilePx = with(density) { tile.toPx() }
            val tileSource = minOf(bitmap.width, bitmap.height) / grid
            val originX = (bitmap.width - tileSource * grid) / 2
            val originY = (bitmap.height - tileSource * grid) / 2
            Column {
                for (row in 0 until grid) {
                    Row {
                        for (col in 0 until grid) {
                            val position = row * grid + col
                            val tileId = order[position]
                            val srcX = originX + (tileId % grid) * tileSource
                            val srcY = originY + (tileId / grid) * tileSource
                            val isDragged = dragFrom == position
                            Canvas(
                                modifier = Modifier
                                    .size(tile)
                                    .zIndex(if (isDragged) 1f else 0f)
                                    .offset {
                                        IntOffset(
                                            if (isDragged) dragOffsetX.roundToInt() else 0,
                                            if (isDragged) dragOffsetY.roundToInt() else 0
                                        )
                                    }
                                    .pointerInput(position, grid, tilePx) {
                                        detectDragGestures(
                                            onDragStart = {
                                                dragFrom = position
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                            },
                                            onDrag = { change, amount ->
                                                change.consume()
                                                dragOffsetX += amount.x
                                                dragOffsetY += amount.y
                                            },
                                            onDragEnd = {
                                                val absX = kotlin.math.abs(dragOffsetX)
                                                val absY = kotlin.math.abs(dragOffsetY)
                                                val threshold = tilePx * 0.3f
                                                val row = position / grid
                                                val col = position % grid
                                                val target = when {
                                                    absX >= absY && absX > threshold && dragOffsetX < 0 && col > 0 -> position - 1
                                                    absX >= absY && absX > threshold && dragOffsetX > 0 && col < grid - 1 -> position + 1
                                                    absY > absX && absY > threshold && dragOffsetY < 0 && row > 0 -> position - grid
                                                    absY > absX && absY > threshold && dragOffsetY > 0 && row < grid - 1 -> position + grid
                                                    else -> -1
                                                }
                                                if (target >= 0) {
                                                    val swapped = order.toMutableList()
                                                    swapped[position] = order[target]
                                                    swapped[target] = order[position]
                                                    order = swapped
                                                }
                                                dragFrom = -1
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                            },
                                            onDragCancel = {
                                                dragFrom = -1
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                            }
                                        )
                                    }
                            ) {
                                drawImage(
                                    image = bitmap,
                                    srcOffset = IntOffset(srcX, srcY),
                                    srcSize = IntSize(tileSource, tileSource),
                                    dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                                )
                                drawRect(
                                    color = FieldBorder,
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Button(
        onClick = { onAnswer(order == solvedOrder) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
    ) {
        Text("Comprobar", fontWeight = FontWeight.Bold)
    }
}

private val PuzzleBackground = Color(0xFF183321)

private fun shuffledPuzzle(total: Int): List<Int> {
    val identity = (0 until total).toList()
    repeat(30) {
        val shuffled = identity.shuffled()
        if (shuffled != identity) return shuffled
    }
    return identity.reversed()
}

private fun normalizeAnswer(value: String): String =
    value.trim().lowercase().replace(Regex("\\s+"), " ")
