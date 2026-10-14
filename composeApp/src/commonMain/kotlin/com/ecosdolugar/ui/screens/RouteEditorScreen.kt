package com.ecosdolugar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import com.ecosdolugar.data.RoutesRepository
import com.ecosdolugar.model.Category
import com.ecosdolugar.model.Difficulty
import com.ecosdolugar.model.FieldEnigma
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.OverviewFeature
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.model.StopTrial
import com.ecosdolugar.model.TrialType
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.ui.theme.PineDeep
import com.ecosdolugar.ui.theme.SurfaceCream
import com.ecosdolugar.ui.theme.TerracottaGold

private data class StopDraft(
    val title: String,
    val location: String,
    val story: String,
    val latitude: Double,
    val longitude: Double,
    val question: String,
    val hint: String,
    val answer: String,
    val wrongAnswer: String,
    val source: RouteStop? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteEditorScreen(
    userProfile: UserProfile,
    initialRoute: RouteItem?,
    onBack: () -> Unit,
    onSave: (RouteItem, String) -> Unit
) {
    val routeId = remember(initialRoute?.id) {
        initialRoute?.id ?: "route-${Random.nextLong().toULong().toString(16)}"
    }
    val stops = remember(initialRoute?.id) {
        mutableStateListOf<StopDraft>().apply {
            initialRoute?.stops.orEmpty().forEach { stop ->
                val trial = stop.trial
                val wrongAnswerIndex = trial?.options?.indices?.firstOrNull {
                    it != trial.correctOptionIndex
                }
                add(
                    StopDraft(
                        title = stop.title,
                        location = stop.locationName,
                        story = stop.story,
                        latitude = stop.latitude,
                        longitude = stop.longitude,
                        question = trial?.question?.takeIf { it.isNotBlank() } ?: stop.enigma.question,
                        hint = trial?.generalHint?.takeIf { it.isNotBlank() } ?: stop.enigma.hint,
                        answer = trial?.options?.getOrNull(trial.correctOptionIndex)
                            ?: stop.enigma.answer,
                        wrongAnswer = trial?.options?.getOrNull(wrongAnswerIndex ?: -1)
                            ?: "No lo sé",
                        source = stop
                    )
                )
            }
        }
    }
    val scrollState = rememberScrollState()
    var title by remember(initialRoute?.id) { mutableStateOf(initialRoute?.title.orEmpty()) }
    var location by remember(initialRoute?.id) { mutableStateOf(initialRoute?.location.orEmpty()) }
    var description by remember(initialRoute?.id) { mutableStateOf(initialRoute?.description.orEmpty()) }
    var stopTitle by remember { mutableStateOf("") }
    var stopLocation by remember { mutableStateOf("") }
    var story by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var wrongAnswer by remember { mutableStateOf("") }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    fun addStop() {
        val lat = latitude.toDoubleOrNull()
        val lng = longitude.toDoubleOrNull()
        val invalid = when {
            stopTitle.isBlank() -> "Pon un nombre a la parada."
            stopLocation.isBlank() -> "Indica el lugar de la parada."
            lat == null || lat !in -90.0..90.0 -> "La latitud debe estar entre -90 y 90."
            lng == null || lng !in -180.0..180.0 -> "La longitud debe estar entre -180 y 180."
            question.isBlank() -> "Escribe el enigma de esta parada."
            answer.isBlank() || wrongAnswer.isBlank() || answer == wrongAnswer ->
                "Indica una respuesta correcta y otra incorrecta distintas."
            else -> null
        }
        if (invalid != null) {
            validationMessage = invalid
            return
        }
        stops += StopDraft(
            title = stopTitle.trim(),
            location = stopLocation.trim(),
            story = story.trim(),
            latitude = lat!!,
            longitude = lng!!,
            question = question.trim(),
            hint = hint.trim(),
            answer = answer.trim(),
            wrongAnswer = wrongAnswer.trim()
        )
        stopTitle = ""
        stopLocation = ""
        story = ""
        latitude = ""
        longitude = ""
        question = ""
        hint = ""
        answer = ""
        wrongAnswer = ""
        validationMessage = null
    }

    fun save(status: String) {
        if (title.isBlank() || location.isBlank()) {
            validationMessage = "Indica el nombre y la ubicación de la ruta."
            return
        }
        if (stops.isEmpty()) {
            validationMessage = "Añade al menos una parada con su ubicación y reto."
            return
        }
        val creatorId = userProfile.googleId
        if (creatorId.isNullOrBlank()) {
            validationMessage = "Vuelve a iniciar sesión para guardar la ruta."
            return
        }
        val guide = GuideAuthor(
            id = creatorId,
            name = userProfile.name,
            title = "Guía local",
            bio = "Creador de rutas culturales.",
            location = location
        )
        val routeStops = stops.mapIndexed { index, draft ->
            val trial = StopTrial(
                type = TrialType.ACERTIJO,
                title = "Enigma: ${draft.title}",
                loreContext = draft.story,
                question = draft.question,
                options = listOf(draft.answer, draft.wrongAnswer),
                correctOptionIndex = 0,
                generalHint = draft.hint,
                solutionExplanation = draft.answer,
                magicalReward = "Reto superado"
            )
            val source = draft.source
            val existingTrial = source?.trial
            val compiledTrial = existingTrial?.copy(
                title = "Enigma: ${draft.title}",
                loreContext = draft.story,
                question = draft.question,
                options = listOf(draft.answer, draft.wrongAnswer),
                correctOptionIndex = 0,
                generalHint = draft.hint,
                solutionExplanation = draft.answer
            ) ?: trial
            source?.copy(
                order = index + 1,
                title = draft.title,
                subtitle = draft.location,
                locationName = draft.location,
                story = draft.story,
                enigma = FieldEnigma(draft.question, draft.hint, draft.answer),
                trial = compiledTrial,
                latitude = draft.latitude,
                longitude = draft.longitude,
                mapPositionX = (20 + index * 15).coerceAtMost(85).toFloat(),
                mapPositionY = (30 + index * 12).coerceAtMost(85).toFloat()
            ) ?: RouteStop(
                id = "$routeId-stop-${index + 1}",
                order = index + 1,
                title = draft.title,
                subtitle = draft.location,
                locationName = draft.location,
                story = draft.story,
                historicalNote = "",
                enigma = FieldEnigma(draft.question, draft.hint, draft.answer),
                trial = compiledTrial,
                audioDuration = "Lectura",
                latitude = draft.latitude,
                longitude = draft.longitude,
                mapPositionX = (20 + index * 15).coerceAtMost(85).toFloat(),
                mapPositionY = (30 + index * 12).coerceAtMost(85).toFloat()
            )
        }
        val route = initialRoute?.copy(
            title = title.trim(),
            subtitle = "Ruta cultural en ${location.trim()}",
            description = description.trim(),
            location = location.trim(),
            region = location.trim().uppercase(),
            stopsCount = routeStops.size,
            stops = routeStops
        ) ?: RouteItem(
                id = routeId,
                title = title.trim(),
                subtitle = "Ruta cultural en ${location.trim()}",
                description = description.trim(),
                location = location.trim(),
                region = location.trim().uppercase(),
                imageUrl = RoutesRepository.HERO_FOREST_URL,
                stopsCount = routeStops.size,
                duration = "1h 30 min",
                distance = "${(routeStops.size * 0.8).toString().replace('.', ',')} km",
                difficulty = Difficulty.FACIL,
                categories = listOf(Category.HISTORIA),
                audioTag = "Retos interactivos",
                tomoRef = "Cuaderno del Guía",
                guide = guide,
                overviewFeatures = listOf(
                    OverviewFeature(
                        iconName = "explore",
                        title = "${routeStops.size} paradas",
                        description = "Enigmas geolocalizados creados por un guía local.",
                        accentTag = "primary"
                    )
                ),
                stops = routeStops
            )
        onSave(route, status)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (initialRoute == null) "Crear ruta" else "Continuar borrador",
                        color = SurfaceCream
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = SurfaceCream)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = PineDeep
                )
            )
        },
        containerColor = PineDeep
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "DATOS DE LA RUTA",
                color = TerracottaGold,
                fontSize = 12.sp
            )
            RouteEditorTextField(
                value = title,
                onValueChange = { title = it },
                label = "Nombre de la ruta",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            RouteEditorTextField(
                value = location,
                onValueChange = { location = it },
                label = "Localidad o zona",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            RouteEditorTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción",
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(4.dp))
            Text(
                "PARADAS Y RETOS",
                color = TerracottaGold,
                fontSize = 12.sp
            )
            stops.forEachIndexed { index, stop ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF183321)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${index + 1}. ${stop.title}", color = SurfaceCream)
                            Text(
                                "${stop.location} · ${stop.latitude}, ${stop.longitude}",
                                color = SurfaceCream.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = { stops.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar parada", tint = TerracottaGold)
                        }
                    }
                }
            }
            RouteEditorTextField(
                value = stopTitle,
                onValueChange = { stopTitle = it },
                label = "Nombre de la parada",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            RouteEditorTextField(
                value = stopLocation,
                onValueChange = { stopLocation = it },
                label = "Lugar o dirección",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            RouteEditorTextField(
                value = story,
                onValueChange = { story = it },
                label = "Relato de la parada",
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RouteEditorTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = "Latitud",
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                RouteEditorTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = "Longitud",
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Text(
                "El reto se vincula a esta parada. Introduce las coordenadas reales del punto.",
                color = SurfaceCream.copy(alpha = 0.75f),
                fontSize = 12.sp
            )
            RouteEditorTextField(
                value = question,
                onValueChange = { question = it },
                label = "Pregunta del reto",
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            RouteEditorTextField(
                value = hint,
                onValueChange = { hint = it },
                label = "Pista",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            RouteEditorTextField(
                value = answer,
                onValueChange = { answer = it },
                label = "Respuesta correcta",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            RouteEditorTextField(
                value = wrongAnswer,
                onValueChange = { wrongAnswer = it },
                label = "Respuesta incorrecta",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedButton(
                onClick = ::addStop,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SurfaceCream),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(SurfaceCream.copy(alpha = 0.65f))
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(" Añadir parada y reto")
            }
            validationMessage?.let {
                Text(it, color = Color(0xFFFFB4AB), fontSize = 12.sp)
            }
            Button(
                onClick = { save("borrador") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34563C))
            ) {
                Text("Guardar borrador")
            }
            Button(
                onClick = { save("pendiente") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaGold, contentColor = PineDeep)
            ) {
                Text("Enviar para verificación")
            }
        }
    }
}

@Composable
private fun RouteEditorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = singleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SurfaceCream,
            unfocusedTextColor = SurfaceCream,
            focusedContainerColor = Color(0xFF0F2618),
            unfocusedContainerColor = Color(0xFF0F2618),
            focusedBorderColor = TerracottaGold,
            unfocusedBorderColor = SurfaceCream.copy(alpha = 0.55f),
            focusedLabelColor = TerracottaGold,
            unfocusedLabelColor = SurfaceCream.copy(alpha = 0.8f),
            cursorColor = TerracottaGold,
            focusedPlaceholderColor = SurfaceCream.copy(alpha = 0.55f),
            unfocusedPlaceholderColor = SurfaceCream.copy(alpha = 0.55f)
        )
    )
}
