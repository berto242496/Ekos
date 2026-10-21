package com.ecosdolugar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import com.ecosdolugar.ui.components.RouteMapView
import com.ecosdolugar.ui.components.RouteCoverImagePicker
import com.ecosdolugar.data.RoutesRepository
import com.ecosdolugar.localization.AppLanguage
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
import com.ecosdolugar.platform.PlatformBackHandler
import com.ecosdolugar.ui.theme.PineContainer
import com.ecosdolugar.util.formatKilometers
import com.ecosdolugar.util.pathDistanceMeters
import com.ecosdolugar.ui.theme.SurfaceCream
import com.ecosdolugar.ui.theme.TerracottaGold

// Tokens de la web (GuideCreateRouteScreen.tsx) para mantener el mismo aspecto
internal val InkDeep = Color(0xFF0F2416)
private val InkLabel = Color(0xFF334B39)
internal val InkMuted = Color(0xFF47654E)
private val BadgeText = Color(0xFF8C5717)
internal val FieldBorder = PineContainer.copy(alpha = 0.2f)
private val SectionBorder = PineContainer.copy(alpha = 0.1f)
private val ErrorBackground = Color(0xFFFEF2F2)
private val ErrorBorder = Color(0xFFFECACA)
private val ErrorText = Color(0xFF991B1B)

private fun initialCategories(route: RouteItem?): List<Category> =
    route?.categories?.filter { it != Category.ALL }?.takeIf { it.isNotEmpty() }
        ?: listOf(Category.HISTORIA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteEditorScreen(
    userProfile: UserProfile,
    initialRoute: RouteItem?,
    language: AppLanguage,
    isSaving: Boolean,
    onBack: () -> Unit,
    onSave: (RouteItem, String) -> Unit
) {
    val routeId = remember(initialRoute?.id) {
        initialRoute?.id ?: "route-${Random.nextLong().toULong().toString(16)}"
    }
    val stops = remember(initialRoute?.id) {
        mutableStateListOf<StopDraft>().apply {
            initialRoute?.stops.orEmpty().forEach { add(StopDraft.fromRouteStop(it)) }
        }
    }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var title by remember(initialRoute?.id) { mutableStateOf(initialRoute?.title.orEmpty()) }
    var location by remember(initialRoute?.id) { mutableStateOf(initialRoute?.location.orEmpty()) }
    var description by remember(initialRoute?.id) { mutableStateOf(initialRoute?.description.orEmpty()) }
    var imageUrl by remember(initialRoute?.id) {
        mutableStateOf(initialRoute?.imageUrl ?: RoutesRepository.HERO_FOREST_URL)
    }
    var durationHours by remember(initialRoute?.id) {
        mutableStateOf(parseDurationPart(initialRoute?.duration, isHours = true))
    }
    var durationMinutes by remember(initialRoute?.id) {
        mutableStateOf(parseDurationPart(initialRoute?.duration, isHours = false))
    }
    var difficulty by remember(initialRoute?.id) { mutableStateOf(initialRoute?.difficulty ?: Difficulty.FACIL) }
    var selectedCategories by remember(initialRoute?.id) {
        mutableStateOf(initialCategories(initialRoute))
    }
    val stopForm = remember { StopFormState() }
    var editingStopIndex by remember { mutableStateOf<Int?>(null) }
    var validationMessage by remember { mutableStateOf<String?>(null) }
    var currentStep by remember { mutableStateOf(1) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var pendingStatus by remember { mutableStateOf<String?>(null) }

    fun toggleCategory(category: Category) {
        val next = if (category in selectedCategories) {
            selectedCategories - category
        } else {
            selectedCategories + category
        }
        if (next.isNotEmpty()) selectedCategories = next
    }

    fun continueToStops() {
        validationMessage = when {
            title.isBlank() || location.isBlank() -> "Indica el nombre y la ubicación de la ruta."
            selectedCategories.isEmpty() -> "Selecciona al menos un tipo de ruta."
            formatDuration(durationHours, durationMinutes) == null ->
                "Indica una duración válida (minutos entre 0 y 59)."
            else -> null
        }
        if (validationMessage == null) currentStep = 2
    }

    fun addStop() {
        val invalid = stopForm.validate()
        if (invalid != null) {
            validationMessage = invalid
            return
        }
        val editing = editingStopIndex
        if (editing != null && editing in stops.indices) {
            stops[editing] = stopForm.toDraft()
            editingStopIndex = null
        } else {
            stops += stopForm.toDraft()
        }
        stopForm.reset()
        validationMessage = null
        scope.launch { scrollState.animateScrollTo(0) }
    }

    fun cancelStopEdit() {
        editingStopIndex = null
        stopForm.reset()
        validationMessage = null
    }

    fun editStop(index: Int) {
        editingStopIndex = index
        stopForm.loadFrom(stops[index])
        validationMessage = null
    }

    fun save(status: String): Boolean {
        if (isSaving) return false
        if (title.isBlank() || location.isBlank()) {
            validationMessage = "Indica el nombre y la ubicación de la ruta."
            return false
        }
        if (selectedCategories.isEmpty()) {
            validationMessage = "Selecciona al menos un tipo de ruta."
            return false
        }
        val formattedDuration = formatDuration(durationHours, durationMinutes)
        if (formattedDuration == null) {
            validationMessage = "Indica una duración válida (minutos entre 0 y 59)."
            return false
        }
        if (status != "borrador" && stops.isEmpty()) {
            validationMessage = "Añade al menos una parada con su ubicación y reto."
            return false
        }
        val creatorId = userProfile.googleId
        if (creatorId.isNullOrBlank()) {
            validationMessage = "Vuelve a iniciar sesión para guardar la ruta."
            return false
        }
        val guide = GuideAuthor(
            id = creatorId,
            name = userProfile.name,
            title = "Guía local",
            bio = "Creador de rutas culturales.",
            location = location
        )
        val formattedDistance = formatKilometers(pathDistanceMeters(stops.map { it.latitude to it.longitude }))
        val routeStops = stops.mapIndexed { index, draft -> draft.toRouteStop(index, routeId) }
        val route = initialRoute?.copy(
            title = title.trim(),
            subtitle = "Ruta cultural en ${location.trim()}",
            description = description.trim(),
            location = location.trim(),
            region = location.trim().uppercase(),
            imageUrl = imageUrl.trim(),
            stopsCount = routeStops.size,
            duration = formattedDuration,
            distance = formattedDistance,
            difficulty = difficulty,
            categories = selectedCategories,
            stops = routeStops
        ) ?: RouteItem(
                id = routeId,
                title = title.trim(),
                subtitle = "Ruta cultural en ${location.trim()}",
                description = description.trim(),
                location = location.trim(),
                region = location.trim().uppercase(),
                imageUrl = imageUrl.trim(),
                stopsCount = routeStops.size,
                duration = formattedDuration,
                distance = formattedDistance,
                difficulty = difficulty,
                categories = selectedCategories,
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
        pendingStatus = status
        onSave(route, status)
        return true
    }

    val hasUnsavedData = title != initialRoute?.title.orEmpty() ||
        location != initialRoute?.location.orEmpty() ||
        description != initialRoute?.description.orEmpty() ||
        imageUrl != (initialRoute?.imageUrl ?: RoutesRepository.HERO_FOREST_URL) ||
        durationHours != parseDurationPart(initialRoute?.duration, isHours = true) ||
        durationMinutes != parseDurationPart(initialRoute?.duration, isHours = false) ||
        selectedCategories != initialCategories(initialRoute) ||
        difficulty != (initialRoute?.difficulty ?: Difficulty.FACIL) ||
        (initialRoute == null && stops.isNotEmpty()) ||
        (initialRoute != null && stops.size != initialRoute.stops.size)

    fun requestExit() {
        if (hasUnsavedData) showExitConfirmation = true else onBack()
    }

    PlatformBackHandler(onBack = ::requestExit)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (initialRoute == null) "Crear ruta" else "Continuar borrador",
                        color = InkDeep,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = ::requestExit) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = InkDeep)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceCream,
                    titleContentColor = InkDeep,
                    navigationIconContentColor = InkDeep
                )
            )
        },
        containerColor = SurfaceCream
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Paso $currentStep de 2",
                color = InkMuted,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            if (currentStep == 1) {
                EditorSection(
                    badge = "Datos generales",
                    title = "Datos de la ruta",
                    description = "Nombre, lugar, tipo, duración y dificultad de la ruta."
                ) {
                    RouteEditorTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Nombre de la ruta",
                        singleLine = true
                    )
                    RouteEditorTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Localidad o zona",
                        singleLine = true
                    )
                    RouteEditorTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Descripción",
                        minLines = 3
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldLabel("Tipo de ruta")
                        Text(
                            "Puedes seleccionar varios tipos.",
                            color = InkMuted,
                            fontFamily = FontFamily.Serif,
                            fontSize = 12.sp
                        )
                        Category.values().filter { it != Category.ALL }.chunked(2).forEach { rowOptions ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowOptions.forEach { option ->
                                    SelectableChip(
                                        text = option.displayName,
                                        selected = option in selectedCategories,
                                        onClick = { toggleCategory(option) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowOptions.size < 2) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RouteEditorTextField(
                            value = durationHours,
                            onValueChange = { durationHours = it.filter(Char::isDigit).take(2) },
                            label = "Horas",
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardType = KeyboardType.Number
                        )
                        RouteEditorTextField(
                            value = durationMinutes,
                            onValueChange = { durationMinutes = it.filter(Char::isDigit).take(2) },
                            label = "Minutos",
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldLabel("Dificultad")
                        Difficulty.values().forEach { option ->
                            DifficultyOption(
                                option = option,
                                selected = difficulty == option,
                                onClick = { difficulty = option }
                            )
                        }
                    }
                    RouteEditorTextField(
                        value = imageUrl.takeUnless { it.startsWith("data:image/") }.orEmpty(),
                        onValueChange = { imageUrl = it },
                        label = "URL de la imagen de la ruta",
                        singleLine = true
                    )
                    RouteCoverImagePicker(
                        onImageSelected = { imageUrl = it },
                        onError = { validationMessage = it }
                    )
                    Text(
                        if (imageUrl.startsWith("data:image/")) {
                            "Imagen seleccionada. Se guardará en el almacenamiento de la aplicación."
                        } else {
                            "Esta imagen aparecerá en el listado y en la cabecera de la ruta."
                        },
                        color = InkMuted,
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp
                    )
                }
                validationMessage?.let { ValidationBanner(it) }
                Button(
                    onClick = ::continueToStops,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
                ) {
                    Text("Continuar para añadir paradas", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                }
            } else {
                EditorSection(
                    badge = "Paradas y retos",
                    title = "Paradas y retos (${stops.size})",
                    description = "Cada parada tiene su ubicación y un enigma vinculado."
                ) {
                    stops.forEachIndexed { index, stop ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCream)
                                .border(1.dp, FieldBorder, RoundedCornerShape(16.dp))
                                .padding(start = 14.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${index + 1}. ${stop.title}",
                                    color = InkDeep,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "${stop.location} · ${stop.latitude}, ${stop.longitude}",
                                    color = InkMuted,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(onClick = { editStop(index) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar parada", tint = PineContainer)
                            }
                            IconButton(onClick = {
                                val editing = editingStopIndex
                                if (editing == index) cancelStopEdit()
                                else if (editing != null && editing > index) editingStopIndex = editing - 1
                                stops.removeAt(index)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar parada", tint = TerracottaGold)
                            }
                        }
                    }
                    StopEditorForm(
                        form = stopForm,
                        existingCoordinates = stops.mapIndexed { index, stop ->
                            stop.latitude to stop.longitude
                        }.filterIndexed { index, _ -> index != editingStopIndex },
                        language = language,
                        onMessage = { validationMessage = it },
                        onAddStop = ::addStop,
                        addLabel = if (editingStopIndex != null) "Guardar cambios de la parada" else "Añadir parada"
                    )
                    if (editingStopIndex != null) {
                        SecondaryButton(text = "Cancelar edición", onClick = ::cancelStopEdit)
                    }
                }
                validationMessage?.let { ValidationBanner(it) }
                SecondaryButton(
                    text = "Volver a los datos de la ruta",
                    onClick = { currentStep = 1 },
                    enabled = !isSaving
                )
                SecondaryButton(
                    text = "Guardar borrador",
                    onClick = { save("borrador") },
                    enabled = !isSaving,
                    loading = isSaving && pendingStatus == "borrador"
                )
                Button(
                    onClick = { save("pendiente") },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
                ) {
                    if (isSaving && pendingStatus == "pendiente") {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = SurfaceCream,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.size(8.dp))
                    }
                    Text("Enviar para verificación", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!isSaving) showExitConfirmation = false },
            title = { Text("Guardar cambios", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
            text = { Text("Tienes datos en la ruta. ¿Quieres guardarla como borrador antes de salir?") },
            confirmButton = {
                TextButton(
                    enabled = !isSaving,
                    onClick = {
                        if (!save("borrador")) showExitConfirmation = false
                    }
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = PineContainer,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.size(8.dp))
                        Text("Guardando…", color = InkMuted, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Guardar borrador", color = PineContainer, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                Row {
                    TextButton(enabled = !isSaving, onClick = {
                        showExitConfirmation = false
                        onBack()
                    }) {
                        Text("Salir sin guardar", color = InkMuted)
                    }
                    TextButton(enabled = !isSaving, onClick = { showExitConfirmation = false }) {
                        Text("Seguir editando", color = PineContainer, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

@Composable
private fun EditorSection(
    badge: String,
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SectionBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(TerracottaGold.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        badge.uppercase(),
                        color = BadgeText,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.6.sp
                    )
                }
                Text(
                    title,
                    color = InkDeep,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    description,
                    color = InkMuted,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SectionBorder)
            )
            Column(verticalArrangement = Arrangement.spacedBy(20.dp), content = content)
        }
    }
}

@Composable
internal fun FieldLabel(text: String) {
    Text(
        text.uppercase(),
        color = InkLabel,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.6.sp
    )
}

@Composable
internal fun RouteEditorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 15.sp, color = InkDeep),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = TerracottaGold,
                unfocusedBorderColor = FieldBorder,
                focusedTextColor = InkDeep,
                unfocusedTextColor = InkDeep,
                cursorColor = TerracottaGold
            )
        )
    }
}

@Composable
internal fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (selected) PineContainer else SurfaceCream)
            .border(1.dp, if (selected) PineContainer else FieldBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = if (selected) TerracottaGold else Color.Transparent,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.size(6.dp))
        Text(
            text,
            color = if (selected) SurfaceCream else InkDeep.copy(alpha = 0.8f),
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DifficultyOption(
    option: Difficulty,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) PineContainer else Color.White)
            .border(1.dp, if (selected) PineContainer else FieldBorder, shape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                option.label,
                color = if (selected) SurfaceCream else InkDeep,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                option.detail,
                color = if (selected) SurfaceCream.copy(alpha = 0.75f) else InkMuted,
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp
            )
        }
        if (selected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = TerracottaGold)
        }
    }
}

@Composable
internal fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = PineContainer),
        border = BorderStroke(1.dp, FieldBorder)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = PineContainer, strokeWidth = 2.dp)
            Spacer(Modifier.size(6.dp))
        } else if (icon != null) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.size(6.dp))
        }
        Text(text, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ValidationBanner(message: String) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ErrorBackground)
            .border(1.dp, ErrorBorder, shape)
            .padding(14.dp)
    ) {
        Text(message, color = ErrorText, fontFamily = FontFamily.Serif, fontSize = 13.sp)
    }
}

private fun parseDurationPart(duration: String?, isHours: Boolean): String {
    val regex = if (isHours) Regex("(\\d+)\\s*h") else Regex("(\\d+)\\s*min")
    return duration?.let { regex.find(it)?.groupValues?.get(1) }.orEmpty()
}

private fun formatDuration(hours: String, minutes: String): String? {
    val h = hours.ifBlank { "0" }.toIntOrNull() ?: return null
    val m = minutes.ifBlank { "0" }.toIntOrNull() ?: return null
    if (h < 0 || m !in 0..59 || (h == 0 && m == 0)) return null
    return when {
        h == 0 -> "$m min"
        m == 0 -> "${h}h"
        else -> "${h}h $m min"
    }
}
