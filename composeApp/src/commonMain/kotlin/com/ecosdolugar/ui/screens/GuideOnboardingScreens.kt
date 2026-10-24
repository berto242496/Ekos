package com.ecosdolugar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.guideOnboardingStrings
import com.ecosdolugar.ui.components.RouteCoverImagePicker
import com.ecosdolugar.ui.theme.*
import com.ecosdolugar.util.ProfanityFilter

private data class GuideKeyPoint(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String
)

@Composable
fun GuideWelcomeScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit,
    language: AppLanguage = AppLanguage.SPANISH
) {
    val strings = guideOnboardingStrings(language)
    val points = listOf(
        GuideKeyPoint(
            Icons.Default.Explore,
            strings.keyPointCreateTitle,
            strings.keyPointCreateDescription
        ),
        GuideKeyPoint(
            Icons.Default.AutoStories,
            strings.keyPointPublishTitle,
            strings.keyPointPublishDescription
        ),
        GuideKeyPoint(
            Icons.Default.Groups,
            strings.keyPointReachTitle,
            strings.keyPointReachDescription
        ),
        GuideKeyPoint(
            Icons.Default.Payments,
            strings.keyPointMonetizeTitle,
            strings.keyPointMonetizeDescription
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back, tint = PineContainer)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            strings.welcomeEyebrow,
            color = TerracottaGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            strings.welcomeTitle,
            color = PineDeep,
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(10.dp))
        Text(
            strings.welcomeIntro,
            color = PineMoss,
            fontFamily = FontFamily.Serif,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
        Spacer(Modifier.height(24.dp))

        points.forEach { point ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PineContainer.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PineContainer.copy(alpha = 0.06f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(point.icon, contentDescription = null, tint = TerracottaGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            point.title,
                            color = PineDeep,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(point.description, color = PineMoss, fontSize = 13.sp, lineHeight = 19.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
        ) {
            Text(strings.welcomeCreateProfile, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun GuideProfileSetupScreen(
    initialName: String,
    isSaving: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSave: (name: String, bio: String, photoDataUrl: String?) -> Unit,
    language: AppLanguage = AppLanguage.SPANISH
) {
    val strings = guideOnboardingStrings(language)
    var name by remember { mutableStateOf(initialName) }
    var bio by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf<String?>(null) }
    var photoError by remember { mutableStateOf<String?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    val nameError = ProfanityFilter.displayNameError(name, language)
    val bioHasProfanity = ProfanityFilter.containsProfanity(bio)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp)
    ) {
        IconButton(onClick = onBack, enabled = !isSaving) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back, tint = PineContainer)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            strings.profileTitle,
            color = PineDeep,
            fontFamily = FontFamily.Serif,
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))
        Text(
            strings.profileSubtitle,
            color = PineMoss,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(22.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 60) name = it },
            label = { Text(strings.nameLabel) },
            singleLine = true,
            enabled = !isSaving,
            isError = nameError != null && name.isNotEmpty(),
            supportingText = {
                if (nameError != null && name.isNotEmpty()) Text(nameError)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { if (it.length <= 500) bio = it },
            label = { Text(strings.descriptionLabel) },
            enabled = !isSaving,
            minLines = 4,
            isError = bioHasProfanity,
            supportingText = {
                Text(
                    if (bioHasProfanity) strings.descriptionProfanity
                    else "${bio.length}/500"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, PineContainer.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = TerracottaGold)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (photo != null) strings.photoSelected else strings.photoAdd,
                    color = PineDeep,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))
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
                    Spacer(Modifier.height(6.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        }

        val displayedError = localError ?: errorMessage
        if (displayedError != null) {
            Spacer(Modifier.height(12.dp))
            Text(displayedError, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                val trimmedName = name.trim()
                localError = when {
                    nameError != null -> nameError
                    bioHasProfanity -> strings.descriptionProfanity
                    else -> null
                }
                if (localError == null && !isSaving) {
                    onSave(trimmedName, bio.trim(), photo)
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PineContainer, contentColor = SurfaceCream)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = SurfaceCream,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(strings.saveAndContinue, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}
