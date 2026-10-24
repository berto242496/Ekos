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
import com.ecosdolugar.localization.AppLanguage
import ecosdolugar.composeapp.generated.resources.Res
import ecosdolugar.composeapp.generated.resources.hero_forest
import ecosdolugar.composeapp.generated.resources.google_g_logo
import org.jetbrains.compose.resources.painterResource
import com.ecosdolugar.localization.AppLanguageStrings
import com.ecosdolugar.localization.appLanguageStrings
import com.ecosdolugar.ui.theme.*

@Composable
fun WelcomeScreen(
    onSignInWithGoogle: () -> Unit,
    isSignInLoading: Boolean,
    isGuideSignInLoading: Boolean,
    signInError: String?,
    guideSignInError: String?,
    language: AppLanguage,
    availableRoutesCount: Int,
    activeGuidesCount: Int,
    completedChallengesCount: Int,
    onOpenGuideMode: () -> Unit
) {
    val strings = appLanguageStrings(language)
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
                strings = strings,
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
                    text = strings.headline,
                    color = SurfaceCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 33.sp,
                    lineHeight = 37.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(11.dp))
                Text(
                    text = strings.description,
                    color = SurfaceContainerHigh,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(20.dp))
                WelcomeMetrics(
                    strings = strings,
                    availableRoutesCount = availableRoutesCount,
                    activeGuidesCount = activeGuidesCount,
                    completedChallengesCount = completedChallengesCount
                )
                Spacer(Modifier.height(20.dp))
                GoogleSignInButton(strings, onClick = onSignInWithGoogle, isLoading = isSignInLoading)
                if (signInError != null) {
                    Text(
                        text = signInError,
                        color = Color(0xFFFFD5D5),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
                Spacer(Modifier.height(14.dp))
                GuideDivider(strings)
                Spacer(Modifier.height(10.dp))
                GuideCallToAction(
                    strings,
                    isLoading = isGuideSignInLoading,
                    onClick = onOpenGuideMode
                )
                if (guideSignInError != null) {
                    Text(
                        text = guideSignInError,
                        color = Color(0xFFFFD5D5),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
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
                        strings.locationLine,
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
private fun WelcomeHeader(
    strings: AppLanguageStrings,
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
                    strings.brandName,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    maxLines = 1
                )
                Text(
                    strings.brandSubtitle,
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
private fun WelcomeMetrics(
    strings: AppLanguageStrings,
    availableRoutesCount: Int,
    activeGuidesCount: Int,
    completedChallengesCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetricColumn(strings.availableRoutesLabel, availableRoutesCount.toString(), Modifier.weight(1f).padding(start = 10.dp))
        Box(Modifier.width(1.dp).height(38.dp).background(Color.White.copy(alpha = 0.12f)))
        MetricColumn(strings.activeGuidesLabel, activeGuidesCount.toString(), Modifier.weight(1f).padding(start = 10.dp))
        Box(Modifier.width(1.dp).height(38.dp).background(Color.White.copy(alpha = 0.12f)))
        MetricColumn(strings.completedChallengesLabel, completedChallengesCount.toString(), Modifier.weight(1f).padding(start = 10.dp))
    }
}

@Composable
private fun MetricColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            color = SurfaceContainerHigh,
            fontSize = 9.sp,
            lineHeight = 11.sp,
            minLines = 2,
            maxLines = 2
        )
        Text(
            value,
            color = Color.White,
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun GoogleSignInButton(
    strings: AppLanguageStrings,
    onClick: () -> Unit,
    isLoading: Boolean
) {
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
                    strings.connecting,
                    color = Color(0xFF1F1F1F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.google_g_logo),
                    contentDescription = "Google",
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(11.dp))
                Text(
                    strings.signInTitle,
                    color = Color(0xFF1F1F1F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun GuideDivider(strings: AppLanguageStrings) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.16f))
        Text(
            strings.guidePrompt,
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
private fun GuideCallToAction(
    strings: AppLanguageStrings,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = !isLoading,
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(15.dp),
                    color = TerracottaFixedDim,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    strings.guideConnecting,
                    color = SurfaceCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Icon(
                    Icons.Default.Explore,
                    contentDescription = null,
                    tint = TerracottaFixedDim,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    strings.guideCallToAction,
                    color = SurfaceCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
