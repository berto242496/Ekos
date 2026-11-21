package com.ecosdolugar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.formatRating
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.GuideDashboardStrings
import com.ecosdolugar.localization.guideDashboardStrings
import com.ecosdolugar.localization.guideOnboardingStrings
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.GuideSalesSummary
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RoutePricing
import com.ecosdolugar.model.RouteReview
import com.ecosdolugar.model.RouteSales
import com.ecosdolugar.ui.components.GuideAvatar
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.components.RouteCoverImagePicker
import com.ecosdolugar.ui.theme.*
import com.ecosdolugar.util.ProfanityFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDashboardScreen(
    guide: GuideAuthor,
    routes: List<RouteItem>,
    guideSales: GuideSalesSummary? = null,
    reviews: List<RouteReview> = emptyList(),
    isSaving: Boolean = false,
    isSavingProfile: Boolean = false,
    profileError: String? = null,
    language: AppLanguage = AppLanguage.SPANISH,
    onBack: () -> Unit,
    onCreateNewRoute: () -> Unit,
    onEditRoute: (RouteItem) -> Unit,
    onPublishDraft: (RouteItem) -> Unit,
    onSaveProfile: (name: String, bio: String, photoDataUrl: String?) -> Unit = { _, _, _ -> }
) {
    val strings = guideDashboardStrings(language)
    var editTarget by remember { mutableStateOf<GuideEditTarget?>(null) }
    var profileSubmitted by remember { mutableStateOf(false) }
    LaunchedEffect(isSavingProfile) {
        if (profileSubmitted && !isSavingProfile) {
            profileSubmitted = false
            if (profileError == null) editTarget = null
        }
    }
    val publishedRoutes = routes.filter { it.status == "publicada" }
    val pendingRoutes = routes.filter { it.status != "publicada" }
    var selectedTab by remember { mutableStateOf(0) }
    val visibleRoutes = when (selectedTab) {
        0 -> publishedRoutes
        1 -> pendingRoutes
        else -> emptyList()
    }
    val explorers = routes.sumOf { it.startedCount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PineDeep
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = strings.back)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCream)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateNewRoute,
                containerColor = PineDeep,
                contentColor = SurfaceCream,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(strings.newRoute, fontWeight = FontWeight.SemiBold) }
            )
        },
        containerColor = SurfaceCream
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GuideIdentityCard(
                    guide = guide,
                    strings = strings,
                    onEditName = { editTarget = GuideEditTarget.NAME },
                    onEditPhoto = { editTarget = GuideEditTarget.PHOTO }
                )
            }

            item {
                GuideBioCard(
                    bio = guide.bio,
                    strings = strings,
                    onEdit = { editTarget = GuideEditTarget.BIO }
                )
                editTarget?.let { target ->
                    GuideProfileEditDialog(
                        target = target,
                        initialName = guide.name,
                        initialBio = guide.bio,
                        isSaving = isSavingProfile,
                        errorMessage = profileError,
                        language = language,
                        strings = strings,
                        onDismiss = { editTarget = null },
                        onSave = { name, bio, photo ->
                            profileSubmitted = true
                            onSaveProfile(name, bio, photo)
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTile(
                        label = strings.publishedStat,
                        value = publishedRoutes.size.toString(),
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = strings.pendingStat,
                        value = routes.count { it.status == "pendiente" }.toString(),
                        icon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTile(
                        label = strings.explorersStat,
                        value = explorers.toString(),
                        icon = Icons.Default.Explore,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = strings.ratingStat,
                        value = if (guide.reviewsCount > 0) formatRating(guide.rating) else "—",
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (guideSales != null && (guideSales.totalCents > 0L || guideSales.routes.isNotEmpty())) {
                item {
                    EarningsCard(sales = guideSales, strings = strings)
                }
            }

            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceCream,
                    contentColor = PineDeep,
                    edgePadding = 0.dp
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                strings.publishedTab(publishedRoutes.size),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                strings.pendingTab(pendingRoutes.size),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                strings.reviewsTab(reviews.size),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            if (selectedTab == 2) {
                if (reviews.isEmpty()) {
                    item {
                        Text(
                            text = strings.noReviews,
                            fontSize = 14.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
                items(reviews.size) { index ->
                    GuideReviewCard(review = reviews[index], strings = strings)
                }
            } else {
                if (visibleRoutes.isEmpty()) {
                    item {
                        Text(
                            text = if (selectedTab == 0) strings.noPublishedRoutes
                            else strings.noPendingRoutes,
                            fontSize = 14.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                items(visibleRoutes.size) { index ->
                    val route = visibleRoutes[index]
                    GuideRouteCard(
                        route = route,
                        sales = guideSales?.routes?.get(route.id),
                        isSaving = isSaving,
                        onEdit = { onEditRoute(route) },
                        onPublish = { onPublishDraft(route) },
                        strings = strings
                    )
                }
            }

            item {
                Spacer(Modifier.height(88.dp))
            }
        }
    }
}

@Composable
private fun GuideReviewCard(review: RouteReview, strings: GuideDashboardStrings) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = review.routeTitle.ifBlank { strings.defaultRouteTitle },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = OnSurfaceDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ReviewStars(label = strings.routeLabel, value = review.routeRating)
                ReviewStars(label = strings.guideLabel, value = review.guideRating)
            }
            if (review.comment.isNotBlank()) {
                Text(
                    text = review.comment,
                    fontSize = 13.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
            Text(
                text = review.createdAt.take(10),
                fontSize = 11.sp,
                color = PineMoss
            )
        }
    }
}

@Composable
private fun ReviewStars(label: String, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "$label:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PineMoss
        )
        repeat(5) { index ->
            Icon(
                imageVector = if (index < value) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = TerracottaGold,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun GuideBioCard(bio: String, strings: GuideDashboardStrings, onEdit: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.bioTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = OnSurfaceDark,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = strings.editBioAction, modifier = Modifier.size(18.dp))
                }
            }
            Text(
                text = bio.ifBlank { strings.bioEmpty },
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = if (bio.isBlank()) PineMoss else OnSurfaceDark
            )
        }
    }
}

private enum class GuideEditTarget { NAME, BIO, PHOTO }

@Composable
private fun GuideProfileEditDialog(
    target: GuideEditTarget,
    initialName: String,
    initialBio: String,
    isSaving: Boolean,
    errorMessage: String?,
    language: AppLanguage,
    strings: GuideDashboardStrings,
    onDismiss: () -> Unit,
    onSave: (name: String, bio: String, photoDataUrl: String?) -> Unit
) {
    val onboarding = guideOnboardingStrings(language)
    var name by remember { mutableStateOf(initialName) }
    var bio by remember { mutableStateOf(initialBio) }
    var photo by remember { mutableStateOf<String?>(null) }
    var photoError by remember { mutableStateOf<String?>(null) }
    val nameError = ProfanityFilter.displayNameError(name, language)
    val bioHasProfanity = ProfanityFilter.containsProfanity(bio)
    val profanityMessage = onboarding.descriptionProfanity
    val canSave = when (target) {
        GuideEditTarget.NAME -> name.isNotBlank() && nameError == null
        GuideEditTarget.BIO -> !bioHasProfanity
        GuideEditTarget.PHOTO -> photo != null
    }
    val dialogTitle = when (target) {
        GuideEditTarget.NAME -> strings.editNameAction
        GuideEditTarget.BIO -> strings.bioTitle
        GuideEditTarget.PHOTO -> strings.editPhotoAction
    }
    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor = SurfaceCream,
        title = { Text(dialogTitle, color = PineDeep, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (target == GuideEditTarget.NAME) OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 60) name = it },
                    label = { Text(onboarding.nameLabel) },
                    singleLine = true,
                    enabled = !isSaving,
                    isError = nameError != null && name.isNotEmpty(),
                    supportingText = { if (nameError != null && name.isNotEmpty()) Text(nameError) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (target == GuideEditTarget.PHOTO) Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        if (photo != null) onboarding.photoSelected else onboarding.photoAdd,
                        color = PineDeep,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    if (!isSaving) {
                        RouteCoverImagePicker(
                            onImageSelected = {
                                photo = it
                                photoError = null
                            },
                            onError = { photoError = it }
                        )
                    }
                    photoError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
                if (target == GuideEditTarget.BIO) OutlinedTextField(
                    value = bio,
                    onValueChange = { if (it.length <= 500) bio = it },
                    enabled = !isSaving,
                    minLines = 4,
                    isError = bioHasProfanity,
                    supportingText = {
                        Text(if (bioHasProfanity) profanityMessage else "${bio.length}/500 · ${strings.bioHint}")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (canSave && !isSaving) onSave(name.trim(), bio.trim(), photo) },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = SurfaceCream, strokeWidth = 2.dp)
                } else {
                    Text(strings.saveBioAction)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(strings.cancelAction)
            }
        }
    )
}

@Composable
private fun GuideIdentityCard(
    guide: GuideAuthor,
    strings: GuideDashboardStrings,
    onEditName: () -> Unit,
    onEditPhoto: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onEditPhoto)
            ) {
                GuideAvatar(name = guide.name, avatarUrl = guide.avatarUrl, size = 60.dp)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = guide.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = OnSurfaceDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    IconButton(onClick = onEditName, modifier = Modifier.size(32.dp).minimumInteractiveComponentSize()) {
                        Icon(Icons.Default.Edit, contentDescription = strings.editNameAction, modifier = Modifier.size(16.dp))
                    }
                    if (guide.verified) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = strings.verifiedGuide,
                            tint = TerracottaGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                val subtitle = listOf(guide.title, guide.location).filter { it.isNotBlank() }.joinToString(" · ")
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = PineMoss,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(14.dp))
                    Text(
                        text = if (guide.reviewsCount > 0) strings.ratingWithReviews(formatRating(guide.rating), guide.reviewsCount) else strings.noReviewsYet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceDark
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = modifier,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PineMoss,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(icon, contentDescription = null, tint = PineMoss, modifier = Modifier.size(16.dp))
            }
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceDark
            )
        }
    }
}

@Composable
private fun EarningsCard(sales: GuideSalesSummary, strings: GuideDashboardStrings) {
    var showInfo by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.earningsTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = OnSurfaceDark,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showInfo = true }, modifier = Modifier.size(32.dp).minimumInteractiveComponentSize()) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = strings.earningsInfoTitle,
                        tint = OnSurfaceDark
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(
                    label = strings.totalGeneratedStat,
                    value = RoutePricing.formatEuros(sales.totalCents.toInt()),
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = strings.pendingBalanceStat,
                    value = RoutePricing.formatEuros(sales.pendingCents.toInt()),
                    icon = Icons.Default.Schedule,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text(text = strings.earningsInfoTitle, fontWeight = FontWeight.Bold) },
            text = { Text(text = strings.earningsInfoBody, fontSize = 14.sp) },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) {
                    Text(text = strings.closeAction)
                }
            }
        )
    }
}

@Composable
private fun GuideRouteCard(
    route: RouteItem,
    sales: RouteSales?,
    isSaving: Boolean,
    onEdit: () -> Unit,
    onPublish: () -> Unit,
    strings: GuideDashboardStrings
) {
    val isDraft = route.status == "borrador"
    val isPending = route.status == "pendiente"
    val statusLabel = when {
        isDraft -> strings.statusDraft
        isPending -> strings.statusPending
        else -> strings.statusPublished
    }
    val statusColor = when {
        isDraft -> Color(0xFFFEF3C7)
        isPending -> Color(0xFFFFEDD5)
        else -> Color(0xFFD1FAE5)
    }
    val statusTextColor = when {
        isDraft -> Color(0xFF92400E)
        isPending -> Color(0xFF9A3412)
        else -> Color(0xFF065F46)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 1.dp
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                RouteCoverImage(
                    url = route.imageUrl,
                    modifier = Modifier.fillMaxSize().background(SurfaceContainerHighest)
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = statusLabel.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCream,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Text(
                        text = route.price,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PineDeep,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = route.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = OnSurfaceDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = strings.routeSummary(route.location, route.duration, route.stopsCount),
                    fontSize = 12.sp,
                    color = PineMoss,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = route.description,
                    fontSize = 13.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCream,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.startsLabel, fontSize = 10.sp, color = PineMoss, fontWeight = FontWeight.Bold)
                            Text(route.startedCount.toString(), fontWeight = FontWeight.Bold, color = OnSurfaceDark)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.stopsLabel, fontSize = 10.sp, color = PineMoss, fontWeight = FontWeight.Bold)
                            Text(route.stopsCount.toString(), fontWeight = FontWeight.Bold, color = OnSurfaceDark)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.difficultyLabel, fontSize = 10.sp, color = PineMoss, fontWeight = FontWeight.Bold)
                            Text(
                                strings.difficulty(route.difficulty),
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                if (route.priceCents > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = strings.salesCount(sales?.salesCount ?: 0),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceDark
                        )
                        Text(
                            text = strings.earnedAmount(
                                RoutePricing.formatEuros((sales?.earnedCents ?: 0L).toInt())
                            ),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PineDeep
                        )
                    }
                }
            }

            HorizontalDivider(color = SurfaceContainerHighest)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (isDraft) {
                    OutlinedButton(
                        onClick = onPublish,
                        enabled = !isSaving,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PineDeep)
                    ) {
                        Text(strings.publishAction)
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Button(
                    onClick = onEdit,
                    enabled = !isSaving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PineDeep, contentColor = SurfaceCream)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isDraft) strings.continueDraftAction else strings.editRouteAction)
                }
            }
        }
    }
}
