package com.ecosdolugar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.PointsGain
import com.ecosdolugar.localization.ActiveExpeditionStrings
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.activeExpeditionStrings
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.model.StopAudioMode
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.model.routeScore
import com.ecosdolugar.platform.PlatformBackHandler
import androidx.compose.material3.CircularProgressIndicator
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.components.RouteImageState
import com.ecosdolugar.ui.components.rememberRouteImage
import com.ecosdolugar.ui.components.rememberAudioClipPlayer
import com.ecosdolugar.ui.components.rememberVoicePreviewer
import com.ecosdolugar.ui.theme.MossContainer
import com.ecosdolugar.ui.theme.PineContainer
import com.ecosdolugar.ui.theme.SurfaceCream
import com.ecosdolugar.ui.theme.TerracottaGold
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

private enum class ExpeditionPhase { INFO, TRIAL, SUMMARY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveExpeditionScreen(
    route: RouteItem,
    userProfile: UserProfile,
    initialStopId: String? = null,
    pointsGain: PointsGain? = null,
    onPointsGainShown: () -> Unit = {},
    routeBonusGain: PointsGain? = null,
    onRouteBonusShown: () -> Unit = {},
    onBack: () -> Unit,
    onSolveEnigma: (String, Int, Int) -> Unit,
    onSubmitReview: (routeRating: Int, guideRating: Int, comment: String) -> Unit,
    language: AppLanguage = AppLanguage.SPANISH
) {
    val strings = activeExpeditionStrings(language)
    val stop = route.stops.firstOrNull { it.id == initialStopId } ?: route.stops.firstOrNull()
    if (stop == null) {
        PlatformBackHandler(onBack = onBack)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(strings.expeditionTitle, color = InkDeep) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = strings.back, tint = InkDeep)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCream)
                )
            },
            containerColor = SurfaceCream
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(strings.noStops, color = InkMuted)
            }
        }
        return
    }

    var phase by remember(stop.id) { mutableStateOf(ExpeditionPhase.INFO) }
    val isCompleted = stop.id in userProfile.solvedEnigmaIds
    val completedStops = route.stops.count { it.id in userProfile.solvedEnigmaIds }
    val isRouteCompleted = completedStops == route.stops.size
    val stopImage by rememberRouteImage(stop.imageUrl)
    val trialImage by rememberRouteImage(stop.trial?.imageUrl?.takeIf { it.isNotBlank() } ?: stop.imageUrl)
    val imagesLoading = stopImage is RouteImageState.Loading || trialImage is RouteImageState.Loading
    val goBack: () -> Unit = {
        if (phase == ExpeditionPhase.TRIAL) phase = ExpeditionPhase.INFO else onBack()
    }
    PlatformBackHandler(onBack = goBack)

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    if (phase != ExpeditionPhase.SUMMARY) {
                        Surface(
                            modifier = Modifier.padding(end = 12.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = PineContainer.copy(alpha = 0.92f),
                            shadowElevation = 6.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(18.dp))
                                Text(
                                    userProfile.routeScore(route).toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                },
                title = {
                    Text(
                        text = when (phase) {
                            ExpeditionPhase.INFO, ExpeditionPhase.TRIAL -> "${strings.stopWord} ${stop.order} ${strings.ofWord} ${route.stops.size}"
                            ExpeditionPhase.SUMMARY -> strings.routeFinishedTitle
                        },
                        color = InkDeep,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
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
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (imagesLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = TerracottaGold
                )
            } else when (phase) {
                ExpeditionPhase.INFO -> StopInfoContent(
                    stop = stop,
                    isCompleted = isCompleted,
                    onStartTrial = { phase = ExpeditionPhase.TRIAL },
                    strings = strings
                )
                ExpeditionPhase.TRIAL -> StopTrialContent(
                    stop = stop,
                    characterId = userProfile.characterId,
                    isSolved = isCompleted,
                    onSolved = { attempts, hintsUsed -> onSolveEnigma(stop.id, attempts, hintsUsed) },
                    onFinish = { if (isRouteCompleted) phase = ExpeditionPhase.SUMMARY else onBack() },
                    isRouteCompleted = isRouteCompleted,
                    language = language
                )
                ExpeditionPhase.SUMMARY -> RouteSummaryContent(
                    route = route,
                    completedStops = completedStops,
                    points = userProfile.routeScore(route),
                    onSubmitReview = onSubmitReview,
                    onDone = onBack,
                    strings = strings
                )
            }
            PointsGainChip(
                gain = pointsGain.takeIf { phase != ExpeditionPhase.SUMMARY },
                onShown = onPointsGainShown,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp)
            )
            PointsGainChip(
                gain = routeBonusGain.takeIf { phase == ExpeditionPhase.SUMMARY },
                onShown = onRouteBonusShown,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp)
            )
        }
    }
}

/** Shows "+X" for a new [gain] for 1.5 s. [onShown] consumes the gain so it never re-appears. */
@Composable
private fun PointsGainChip(gain: PointsGain?, onShown: () -> Unit, modifier: Modifier = Modifier) {
    var shown by remember { mutableStateOf<PointsGain?>(null) }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(gain?.serial) {
        if (gain != null) {
            shown = gain
            onShown()
        }
    }
    LaunchedEffect(shown?.serial) {
        if (shown == null) return@LaunchedEffect
        visible = true
        delay(1500)
        visible = false
    }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut()
    ) {
        Surface(shape = RoundedCornerShape(16.dp), color = TerracottaGold) {
            Text(
                text = "+${shown?.points ?: 0}",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                color = SurfaceCream,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RouteSummaryContent(
    route: RouteItem,
    completedStops: Int,
    points: Int,
    onSubmitReview: (routeRating: Int, guideRating: Int, comment: String) -> Unit,
    onDone: () -> Unit,
    strings: ActiveExpeditionStrings
) {
    var routeRating by remember(route.id) { mutableIntStateOf(0) }
    var guideRating by remember(route.id) { mutableIntStateOf(0) }
    var comment by remember(route.id) { mutableStateOf("") }
    var submitted by remember(route.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(route.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = InkDeep)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, FieldBorder)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryStat(label = strings.statStops, value = "$completedStops/${route.stops.size}")
                SummaryStat(label = strings.routeTotalScore, value = points.toString())
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, FieldBorder)
        ) {
            if (submitted) {
                Text(
                    strings.thanksForRating,
                    modifier = Modifier.padding(18.dp),
                    color = PineContainer,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(strings.leaveRating, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkDeep)
                    StarRatingRow(label = strings.routeRatingLabel, value = routeRating, strings = strings) { routeRating = it }
                    StarRatingRow(label = "${strings.guideRatingLabel} (${route.guide.name})", value = guideRating, strings = strings) { guideRating = it }
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { if (it.length <= 500) comment = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(strings.commentPlaceholder) },
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            onSubmitReview(routeRating, guideRating, comment)
                            submitted = true
                        },
                        enabled = routeRating > 0 && guideRating > 0,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
                    ) {
                        Text(strings.submitRating, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(strings.backToMap, color = InkDeep, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SummaryStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = InkDeep)
        Text(label.uppercase(), fontSize = 11.sp, color = InkMuted)
    }
}

@Composable
private fun StarRatingRow(label: String, value: Int, strings: ActiveExpeditionStrings, onChange: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, color = InkDeep)
        Row {
            (1..5).forEach { star ->
                IconButton(onClick = { onChange(star) }) {
                    Icon(
                        imageVector = if (star <= value) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "$star ${if (star == 1) strings.starSingular else strings.starPlural}",
                        tint = TerracottaGold
                    )
                }
            }
        }
    }
}

@Composable
private fun StopInfoContent(
    stop: RouteStop,
    isCompleted: Boolean,
    onStartTrial: () -> Unit,
    strings: ActiveExpeditionStrings
) {
    val voice = rememberVoicePreviewer()
    val clipPlayer = rememberAudioClipPlayer()
    var narrating by remember(stop.id) { mutableStateOf(false) }
    var playingRecording by remember(stop.id) { mutableStateOf(false) }
    DisposableEffect(stop.id) {
        onDispose {
            voice.stop()
            clipPlayer.stop()
        }
    }

    val narrationText = stop.audioNarration?.takeIf { it.isNotBlank() } ?: stop.story
    val recordedUrl = stop.recordedAudioUrl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (stop.imageUrl.isNotBlank()) {
            RouteCoverImage(
                url = stop.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        Text(
            text = stop.title,
            color = InkDeep,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        if (stop.locationName.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TerracottaGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(stop.locationName, color = InkMuted, fontSize = 14.sp)
            }
        }

        if (stop.audioMode == StopAudioMode.TTS) {
            NarrationToggleButton(
                playing = narrating,
                label = strings.listenStory,
                stopLabel = strings.stopPlayback,
                onClick = {
                    if (narrating) {
                        voice.stop()
                        narrating = false
                    } else {
                        clipPlayer.stop()
                        playingRecording = false
                        voice.speak(narrationText, stop.narrationVoice)
                        narrating = true
                    }
                }
            )
        } else if (stop.audioMode == StopAudioMode.RECORDED && !recordedUrl.isNullOrBlank()) {
            NarrationToggleButton(
                playing = playingRecording,
                label = strings.listenRecording,
                stopLabel = strings.stopPlayback,
                onClick = {
                    if (playingRecording) {
                        clipPlayer.stop()
                        playingRecording = false
                    } else {
                        voice.stop()
                        narrating = false
                        clipPlayer.play(recordedUrl)
                        playingRecording = true
                    }
                }
            )
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, FieldBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stop.story,
                color = InkDeep,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(18.dp)
            )
        }

        Button(
            onClick = onStartTrial,
            enabled = !isCompleted,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PineContainer,
                contentColor = SurfaceCream,
                disabledContainerColor = MossContainer,
                disabledContentColor = SurfaceCream
            )
        ) {
            Text(
                text = if (isCompleted) strings.challengeCompleted else strings.goToChallenge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NarrationToggleButton(
    playing: Boolean,
    label: String,
    stopLabel: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, PineContainer),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = PineContainer)
    ) {
        Icon(
            if (playing) Icons.Default.Stop else Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (playing) stopLabel else label,
            fontWeight = FontWeight.SemiBold
        )
    }
}
