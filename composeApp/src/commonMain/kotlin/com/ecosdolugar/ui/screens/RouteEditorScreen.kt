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
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image
import ecosdolugar.composeapp.generated.resources.Res
import ecosdolugar.composeapp.generated.resources.parada_01
import ecosdolugar.composeapp.generated.resources.parada_02
import ecosdolugar.composeapp.generated.resources.parada_03
import ecosdolugar.composeapp.generated.resources.parada_04
import ecosdolugar.composeapp.generated.resources.parada_05
import ecosdolugar.composeapp.generated.resources.parada_06
import ecosdolugar.composeapp.generated.resources.parada_07
import ecosdolugar.composeapp.generated.resources.parada_08
import ecosdolugar.composeapp.generated.resources.parada_09
import ecosdolugar.composeapp.generated.resources.parada_10
import ecosdolugar.composeapp.generated.resources.parada_11
import ecosdolugar.composeapp.generated.resources.parada_12
import ecosdolugar.composeapp.generated.resources.parada_13
import ecosdolugar.composeapp.generated.resources.parada_14
import ecosdolugar.composeapp.generated.resources.parada_15
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
import androidx.compose.runtime.LaunchedEffect
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
import com.ecosdolugar.localization.categoryLabel
import com.ecosdolugar.localization.difficultyDetail
import com.ecosdolugar.localization.difficultyLabel
import com.ecosdolugar.localization.routeEditorStrings
import com.ecosdolugar.localization.stopEditorStrings
import com.ecosdolugar.model.Category
import com.ecosdolugar.model.Difficulty
import com.ecosdolugar.model.FieldEnigma
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.OverviewFeature
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RoutePricing
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
    val strings = routeEditorStrings(language)
    val stopStrings = stopEditorStrings(language)
    val routeId = remember(initialRoute?.id) {
        initialRoute?.id ?: "route-${Random.nextLong().toULong().toString(16)}"
    }
    val stops = remember(initialRoute?.id) {
        mutableStateListOf<StopDraft>().apply {
            initialRoute?.stops.orEmpty().forEach { add(StopDraft.fromRouteStop(it, stopStrings)) }
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
    LaunchedEffect(currentStep) { scrollState.scrollTo(0) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var pendingStatus by remember { mutableStateOf<String?>(null) }
    var isPaid by remember(initialRoute?.id) { mutableStateOf((initialRoute?.priceCents ?: 0) > 0) }
    var selectedPriceCents by remember(initialRoute?.id) {
        mutableStateOf(initialRoute?.priceCents?.takeIf { it > 0 } ?: RoutePricing.TIERS_CENTS.first())
    }

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
            title.isBlank() || location.isBlank() -> strings.routeNameAndLocationRequired
            selectedCategories.isEmpty() -> strings.routeTypeRequired
            formatDuration(durationHours, durationMinutes) == null ->
                strings.invalidDuration
            else -> null
        }
        if (validationMessage == null) currentStep = 2
    }

    fun addStop() {
        val invalid = stopForm.validate(stopStrings)
        if (invalid != null) {
            validationMessage = invalid
            return
        }
        val editing = editingStopIndex
        if (editing != null && editing in stops.indices) {
            stops[editing] = stopForm.toDraft()
            editingStopIndex = null
        } else {
            if (stops.size >= MAX_STOPS) {
                validationMessage = strings.maxStopsReached
                return
            }
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

    fun moveStop(from: Int, to: Int) {
        if (to !in stops.indices) return
        val item = stops.removeAt(from)
        stops.add(to, item)
        val editing = editingStopIndex
        if (editing != null) {
            editingStopIndex = when (editing) {
                from -> to
                in (minOf(from, to)..maxOf(from, to)) -> if (from < to) editing - 1 else editing + 1
                else -> editing
            }
        }
    }

    fun save(status: String): Boolean {
        if (isSaving) return false
        if (title.isBlank() || location.isBlank()) {
            validationMessage = strings.routeNameAndLocationRequired
        }
        if (selectedCategories.isEmpty()) {
            validationMessage = strings.routeTypeRequired
            return false
        }
        val formattedDuration = formatDuration(durationHours, durationMinutes)
        if (formattedDuration == null) {
            validationMessage = strings.invalidDuration
            return false
        }
        if (status != "borrador" && stops.isEmpty()) {
            validationMessage = strings.stopRequired
            return false
        }
        val priceCents = if (isPaid) selectedPriceCents else 0
        val priceLabel = if (isPaid) RoutePricing.formatEuros(selectedPriceCents) else "Gratis"
        val creatorId = userProfile.googleId
        if (creatorId.isNullOrBlank()) {
            validationMessage = strings.loginRequired
            return false
        }
        val guide = GuideAuthor(
            id = creatorId,
            name = userProfile.name,
            title = strings.guideTitle,
            bio = strings.guideBio,
            location = location
        )
        val formattedDistance = formatKilometers(pathDistanceMeters(stops.map { it.latitude to it.longitude }))
        val routeStops = stops.mapIndexed { index, draft -> draft.toRouteStop(index, routeId, stopStrings) }
        val route = initialRoute?.copy(
            title = title.trim(),
            subtitle = strings.routeSubtitle(location.trim()),
            description = description.trim(),
            location = location.trim(),
            region = location.trim().uppercase(),
            imageUrl = imageUrl.trim(),
            stopsCount = routeStops.size,
            duration = formattedDuration,
            distance = formattedDistance,
            difficulty = difficulty,
            categories = selectedCategories,
            stops = routeStops,
            priceCents = priceCents,
            price = priceLabel,
            isFree = !isPaid
        ) ?: RouteItem(
                id = routeId,
                title = title.trim(),
                subtitle = strings.routeSubtitle(location.trim()),
                description = description.trim(),
                location = location.trim(),
                region = location.trim().uppercase(),
                imageUrl = imageUrl.trim(),
                stopsCount = routeStops.size,
                duration = formattedDuration,
                distance = formattedDistance,
                difficulty = difficulty,
                categories = selectedCategories,
                audioTag = strings.audioTag,
                tomoRef = strings.tomoRef,
                guide = guide,
                overviewFeatures = listOf(
                    OverviewFeature(
                        iconName = "explore",
                        title = strings.stopsOverviewTitle(routeStops.size),
                        description = strings.stopsOverviewDescription,
                        accentTag = "primary"
                    )
                ),
                stops = routeStops,
                priceCents = priceCents,
                price = priceLabel,
                isFree = !isPaid
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
        isPaid != ((initialRoute?.priceCents ?: 0) > 0) ||
        (isPaid && selectedPriceCents != initialRoute?.priceCents) ||
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
                        if (initialRoute == null) strings.createRoute else strings.continueDraft,
                        color = InkDeep,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = ::requestExit) {
                        Icon(Icons.Default.ArrowBack, contentDescription = strings.back, tint = InkDeep)
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
                strings.stepOf(currentStep),
                color = InkMuted,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            if (currentStep == 1) {
                EditorSection(
                    badge = strings.generalDetailsBadge,
                    title = strings.routeDetailsTitle,
                    description = strings.routeDetailsDescription
                ) {
                    RouteEditorTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = strings.routeNameLabel,
                        singleLine = true
                    )
                    RouteEditorTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = strings.localityLabel,
                        singleLine = true
                    )
                    RouteEditorTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = strings.descriptionLabel,
                        minLines = 3
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldLabel(strings.routeTypeLabel)
                        Text(
                            strings.multipleTypesHelper,
                            color = InkMuted,
                            fontFamily = FontFamily.Serif,
                            fontSize = 12.sp
                        )
                        Category.values().filter { it != Category.ALL }.chunked(2).forEach { rowOptions ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowOptions.forEach { option ->
                                    SelectableChip(
                                        text = categoryLabel(option, language),
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
                            label = strings.hoursLabel,
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardType = KeyboardType.Number
                        )
                        RouteEditorTextField(
                            value = durationMinutes,
                            onValueChange = { durationMinutes = it.filter(Char::isDigit).take(2) },
                            label = strings.minutesLabel,
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldLabel(strings.difficultyLabel)
                        Difficulty.values().forEach { option ->
                            DifficultyOption(
                                option = option,
                                language = language,
                                selected = difficulty == option,
                                onClick = { difficulty = option }
                            )
                        }
                    }
                    RouteEditorTextField(
                        value = imageUrl.takeUnless { it.startsWith("data:image/") }.orEmpty(),
                        onValueChange = { imageUrl = it },
                        label = strings.routeImageUrlLabel,
                        singleLine = true
                    )
                    RouteCoverImagePicker(
                        onImageSelected = { imageUrl = it },
                        onError = { validationMessage = it }
                    )
                    Text(
                        if (imageUrl.startsWith("data:image/")) {
                            strings.selectedImageHelper
                        } else {
                            strings.routeImageHelper
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
                    Text(strings.continueToStops, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                }
            } else if (currentStep == 2) {
                EditorSection(
                    badge = strings.stopsAndChallengesBadge,
                    title = strings.stopsAndChallengesTitle(stops.size),
                    description = strings.stopsAndChallengesDescription
                ) {
                    stops.forEachIndexed { index, stop ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCream)
                                .border(1.dp, FieldBorder, RoundedCornerShape(16.dp))
                                .padding(start = 8.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icono numerado de la parada
                            Image(
                                painter = stopIconPainter(index),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(Modifier.size(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stop.title,
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
                            // Botones de reordenación (subir/bajar)
                            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                IconButton(
                                    onClick = { moveStop(index, index - 1) },
                                    enabled = index > 0,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowUp,
                                        contentDescription = strings.moveStopUp,
                                        tint = if (index > 0) PineContainer else InkMuted.copy(alpha = 0.3f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { moveStop(index, index + 1) },
                                    enabled = index < stops.size - 1,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = strings.moveStopDown,
                                        tint = if (index < stops.size - 1) PineContainer else InkMuted.copy(alpha = 0.3f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            IconButton(onClick = { editStop(index) }) {
                                Icon(Icons.Default.Edit, contentDescription = strings.editStop, tint = PineContainer)
                            }
                            IconButton(onClick = {
                                val editing = editingStopIndex
                                if (editing == index) cancelStopEdit()
                                else if (editing != null && editing > index) editingStopIndex = editing - 1
                                stops.removeAt(index)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = strings.deleteStop, tint = TerracottaGold)
                            }
                        }
                    }
                    if (stops.size < MAX_STOPS || editingStopIndex != null) {
                        StopEditorForm(
                            form = stopForm,
                            existingCoordinates = stops.mapIndexed { index, stop ->
                                stop.latitude to stop.longitude
                            }.filterIndexed { index, _ -> index != editingStopIndex },
                            language = language,
                            onMessage = { validationMessage = it },
                            onAddStop = ::addStop,
                            addLabel = if (editingStopIndex != null) strings.saveStopChanges else strings.addStop
                        )
                        if (editingStopIndex != null) {
                            SecondaryButton(text = strings.cancelEdit, onClick = ::cancelStopEdit)
                        }
                    } else {
                        ValidationBanner(strings.maxStopsReached)
                    }
                }
                validationMessage?.let { ValidationBanner(it) }
                SecondaryButton(
                    text = strings.backToRouteDetails,
                    onClick = { currentStep = 1 },
                    enabled = !isSaving
                )
                Button(
                    onClick = { currentStep = 3 },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
                ) {
                    Text(strings.continueToPricing, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                }
            } else {
                EditorSection(
                    badge = strings.pricingBadge,
                    title = strings.pricingTitle,
                    description = strings.pricingDescription
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SelectableChip(
                            text = strings.freeRouteLabel,
                            selected = !isPaid,
                            onClick = { isPaid = false },
                            modifier = Modifier.weight(1f)
                        )
                        SelectableChip(
                            text = strings.paidRouteLabel,
                            selected = isPaid,
                            onClick = { isPaid = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        if (isPaid) strings.paidRouteHelper else strings.freeRouteHelper,
                        color = InkMuted,
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp
                    )
                    if (isPaid) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FieldLabel(strings.priceTierLabel)
                            RoutePricing.TIERS_CENTS.chunked(3).forEach { rowTiers ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    rowTiers.forEach { cents ->
                                        SelectableChip(
                                            text = RoutePricing.formatEuros(cents),
                                            selected = selectedPriceCents == cents,
                                            onClick = { selectedPriceCents = cents },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    repeat(3 - rowTiers.size) { Spacer(Modifier.weight(1f)) }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(PineContainer.copy(alpha = 0.08f))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                strings.guideEarningsLabel(
                                    RoutePricing.formatEuros(RoutePricing.guideShareCents(selectedPriceCents))
                                ),
                                color = InkDeep,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                validationMessage?.let { ValidationBanner(it) }
                SecondaryButton(
                    text = strings.backToStops,
                    onClick = { currentStep = 2 },
                    enabled = !isSaving
                )
                SecondaryButton(
                    text = strings.saveDraft,
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
                    Text(strings.submitForVerification, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!isSaving) showExitConfirmation = false },
            title = { Text(strings.saveChanges, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
            text = { Text(strings.unsavedRouteMessage) },
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
                        Text(strings.saving, color = InkMuted, fontWeight = FontWeight.Bold)
                    } else {
                        Text(strings.saveDraft, color = PineContainer, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                Row {
                    TextButton(enabled = !isSaving, onClick = {
                        showExitConfirmation = false
                        onBack()
                    }) {
                        Text(strings.leaveWithoutSaving, color = InkMuted)
                    }
                    TextButton(enabled = !isSaving, onClick = { showExitConfirmation = false }) {
                        Text(strings.keepEditing, color = PineContainer, fontWeight = FontWeight.Bold)
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
    language: AppLanguage,
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
                "${difficultyEmoji(option)} ${difficultyLabel(option, language)}",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                difficultyDetail(option, language),
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

private fun difficultyEmoji(option: Difficulty): String = when (option) {
    Difficulty.FACIL -> "🟢"
    Difficulty.MEDIA -> "🟡"
    Difficulty.EXIGENTE -> "🔴"
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

private const val MAX_STOPS = 15

@Composable
private fun stopIconPainter(index: Int): androidx.compose.ui.graphics.painter.Painter =
    painterResource(
        when (index) {
            0 -> Res.drawable.parada_01
            1 -> Res.drawable.parada_02
            2 -> Res.drawable.parada_03
            3 -> Res.drawable.parada_04
            4 -> Res.drawable.parada_05
            5 -> Res.drawable.parada_06
            6 -> Res.drawable.parada_07
            7 -> Res.drawable.parada_08
            8 -> Res.drawable.parada_09
            9 -> Res.drawable.parada_10
            10 -> Res.drawable.parada_11
            11 -> Res.drawable.parada_12
            12 -> Res.drawable.parada_13
            13 -> Res.drawable.parada_14
            else -> Res.drawable.parada_15
        }
    )

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
