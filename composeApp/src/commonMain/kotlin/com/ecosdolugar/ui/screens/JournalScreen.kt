package com.ecosdolugar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.JournalStrings
import com.ecosdolugar.localization.journalStrings
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview
import com.ecosdolugar.model.UserAchievementStatus
import com.ecosdolugar.model.UserProfile
import com.ecosdolugar.model.completedRoutesOf
import com.ecosdolugar.model.userAchievementStatuses
import com.ecosdolugar.ui.components.RouteCoverImage
import com.ecosdolugar.ui.theme.*
import com.ecosdolugar.util.ProfanityFilter

private val WebBackground = Color(0xFFFCF9F2)
private val WebInk = Color(0xFF1C1C18)
private val WebPine = Color(0xFF1B3022)
private val WebMuted = Color(0xFF47654E)
private val WebBorder = Color(0xFF1B3022).copy(alpha = 0.08f)
private val WebPassport = Color(0xFF061B0E)
private val WebTabTrack = Color(0xFFEFEBE3)
private val WebLogoutText = Color(0xFFB91C1C)
private val WebLogoutBg = Color(0xFFFEF2F2)
private val WebLogoutBorder = Color(0xFFFECACA)

private enum class JournalTab {
    SAVED,
    COMPLETED,
    ACHIEVEMENTS,
    REVIEWS
}

@Composable
fun JournalScreen(
    userProfile: UserProfile,
    routes: List<RouteItem>,
    myReviews: List<RouteReview> = emptyList(),
    language: AppLanguage,
    languageTitle: String,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    onOpenRoute: (String) -> Unit,
    onSaveDisplayName: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val character = remember(userProfile.characterId) {
        CharactersRepository.getById(userProfile.characterId)
    }
    val charColor = Color(character.accentColorHex)

    val savedRoutes = remember(userProfile.savedRouteIds, routes) {
        routes.filter { userProfile.savedRouteIds.contains(it.id) }
    }
    val completedRoutes = remember(userProfile, routes) {
        completedRoutesOf(userProfile, routes)
    }
    val strings = journalStrings(language)
    val achievements = remember(userProfile, completedRoutes, language) {
        userAchievementStatuses(userProfile, completedRoutes, language)
    }
    val unlockedCount = achievements.count { it.isUnlocked }

    var selectedTab by remember { mutableStateOf(JournalTab.SAVED) }
    var showNameDialog by remember { mutableStateOf(false) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WebBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = strings.kicker,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WebMuted,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = strings.title,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = WebPine
                    )
                }
            }

            item {
                PassportCard(
                    userProfile = userProfile,
                    characterEmoji = character.avatarEmoji,
                    characterName = "${character.name} (${character.galicianTitle})",
                    accentColor = charColor,
                    completedCount = completedRoutes.size,
                    trialsCount = userProfile.solvedEnigmaIds.size,
                    achievementsCount = unlockedCount,
                    totalScore = userProfile.totalScore,
                    canEditName = userProfile.isGoogleAuthenticated,
                    onEditName = { showNameDialog = true },
                    strings = strings
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, WebBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            languageTitle,
                            color = WebInk,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box {
                            TextButton(onClick = { languageMenuExpanded = true }) {
                                Text(language.displayName, color = WebPine, fontWeight = FontWeight.SemiBold)
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = WebPine
                                )
                            }
                            DropdownMenu(
                                expanded = languageMenuExpanded,
                                onDismissRequest = { languageMenuExpanded = false }
                            ) {
                                AppLanguage.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.displayName) },
                                        onClick = {
                                            onLanguageChange(option)
                                            languageMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                JournalTabBar(selected = selectedTab, onSelect = { selectedTab = it }, strings = strings)
            }

            when (selectedTab) {
                JournalTab.SAVED -> {
                    if (savedRoutes.isEmpty()) {
                        item { EmptyJournalMessage(strings.emptySaved) }
                    } else {
                        items(savedRoutes, key = { it.id }) { route ->
                            JournalRouteCard(route = route, onClick = { onOpenRoute(route.id) }, strings = strings)
                        }
                    }
                }
                JournalTab.COMPLETED -> {
                    if (completedRoutes.isEmpty()) {
                        item { EmptyJournalMessage(strings.emptyCompleted) }
                    } else {
                        items(completedRoutes, key = { it.id }) { route ->
                            JournalRouteCard(route = route, onClick = { onOpenRoute(route.id) }, strings = strings)
                        }
                    }
                }
                JournalTab.ACHIEVEMENTS -> {
                    items(achievements, key = { it.id }) { achievement ->
                        AchievementCard(achievement, strings)
                    }
                }
                JournalTab.REVIEWS -> {
                    if (myReviews.isEmpty()) {
                        item { EmptyJournalMessage(strings.emptyReviews) }
                    } else {
                        items(myReviews.size) { index ->
                            MyReviewCard(myReviews[index], strings)
                        }
                    }
                }
            }

            if (userProfile.isGoogleAuthenticated) {
                item {
                    Surface(
                        onClick = onSignOut,
                        shape = RoundedCornerShape(12.dp),
                        color = WebLogoutBg,
                        border = BorderStroke(1.dp, WebLogoutBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = WebLogoutText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                strings.signOut,
                                color = WebLogoutText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showNameDialog) {
        EditDisplayNameDialog(
            currentName = userProfile.name,
            onDismiss = { showNameDialog = false },
            onSave = { name ->
                onSaveDisplayName(name.trim())
                showNameDialog = false
            },
            strings = strings,
            language = language
        )
    }
}

@Composable
private fun PassportCard(
    userProfile: UserProfile,
    characterEmoji: String,
    characterName: String,
    accentColor: Color,
    completedCount: Int,
    trialsCount: Int,
    achievementsCount: Int,
    totalScore: Int,
    canEditName: Boolean,
    onEditName: () -> Unit,
    strings: JournalStrings
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = WebPassport,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(accentColor.copy(alpha = 0.2f), CircleShape)
                        .border(2.dp, accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(characterEmoji, fontSize = 28.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.passportLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TerracottaFixedDim,
                        letterSpacing = 1.5.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userProfile.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (canEditName) {
                            IconButton(onClick = onEditName, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = strings.editName,
                                    tint = TerracottaFixedDim,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = strings.companionOf(characterName),
                        fontSize = 12.sp,
                        color = accentColor
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(strings.completedMetric, completedCount.toString(), Modifier.weight(1f))
                MetricTile(strings.trialsMetric, trialsCount.toString(), Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(strings.achievementsMetric, achievementsCount.toString(), Modifier.weight(1f))
                MetricTile(strings.totalScoreMetric, totalScore.toString(), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MetricTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.06f),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TerracottaFixedDim,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun JournalTabBar(selected: JournalTab, onSelect: (JournalTab) -> Unit, strings: JournalStrings) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = WebTabTrack,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            JournalTab.entries.forEach { tab ->
                val isSelected = tab == selected
                Surface(
                    onClick = { onSelect(tab) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color.White else Color.Transparent,
                    shadowElevation = if (isSelected) 2.dp else 0.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (tab) {
                                JournalTab.SAVED -> strings.savedTab
                                JournalTab.COMPLETED -> strings.completedTab
                                JournalTab.ACHIEVEMENTS -> strings.achievementsTab
                                JournalTab.REVIEWS -> strings.reviewsTab
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) WebPine else WebMuted,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JournalRouteCard(route: RouteItem, onClick: () -> Unit, strings: JournalStrings) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, WebBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(WebTabTrack)
            ) {
                RouteCoverImage(url = route.imageUrl, modifier = Modifier.fillMaxSize())
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.location.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = WebMuted,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = route.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = WebInk,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${route.duration} • ${strings.stopsCount(route.stopsCount)}",
                    fontSize = 12.sp,
                    color = WebMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = WebMuted
            )
        }
    }
}

@Composable
private fun AchievementCard(achievement: UserAchievementStatus, strings: JournalStrings) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, WebBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (achievement.isUnlocked) TerracottaGold.copy(alpha = 0.2f) else WebTabTrack,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    achievement.icon,
                    fontSize = 24.sp,
                    modifier = Modifier.alpha(if (achievement.isUnlocked) 1f else 0.4f)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) WebInk else WebMuted
                )
                Text(
                    text = achievement.description,
                    fontSize = 12.sp,
                    color = WebMuted
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = if (achievement.isUnlocked) Color(0xFFDCFCE7) else WebTabTrack
            ) {
                Text(
                    text = if (achievement.isUnlocked) strings.unlocked else strings.pending,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) Color(0xFF166534) else WebMuted,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun MyReviewCard(review: RouteReview, strings: JournalStrings) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, WebBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = review.routeTitle.ifBlank { strings.routeFallback },
                color = WebInk,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = strings.reviewRatings(review.routeRating, review.guideRating),
                color = WebMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (review.comment.isNotBlank()) {
                Text(
                    text = review.comment,
                    color = WebInk,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyJournalMessage(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, WebBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(20.dp),
            color = WebMuted,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun EditDisplayNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    strings: JournalStrings,
    language: AppLanguage
) {
    var draft by remember { mutableStateOf(currentName) }
    val error = ProfanityFilter.displayNameError(draft, language)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = WebBackground,
        title = {
            Text(
                strings.editNameTitle,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = WebPine
            )
        },
        text = {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                singleLine = true,
                isError = error != null,
                label = { Text(strings.nameFieldLabel) },
                supportingText = {
                    Text(
                        text = error ?: "${draft.trim().length}/60",
                        color = if (error != null) WebLogoutText else WebMuted
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draft) },
                enabled = error == null
            ) {
                Text(strings.save, color = WebPine, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = WebMuted)
            }
        }
    )
}
