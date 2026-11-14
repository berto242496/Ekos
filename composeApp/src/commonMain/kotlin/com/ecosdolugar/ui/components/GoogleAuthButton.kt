package com.ecosdolugar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.componentStrings

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier,
    text: String = componentStrings(language).continueWithGoogle,
    isLoading: Boolean = false
) {
    val strings = componentStrings(language)
    Surface(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDADCE0))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color(0xFF1B3022),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = strings.connectingWithGoogle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3C4043)
                )
            } else {
                // Official Google G branding representation
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("G", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color(0xFF4285F4))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3C4043)
                )
            }
        }
    }
}
