package com.ecosdolugar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.localization.AppLanguageStrings
import com.ecosdolugar.ui.theme.PineDeep
import com.ecosdolugar.ui.theme.SurfaceContainerHigh
import com.ecosdolugar.ui.theme.SurfaceCream
import com.ecosdolugar.ui.theme.TerracottaFixedDim
import com.ecosdolugar.ui.theme.TerracottaGold
import ecosdolugar.composeapp.generated.resources.Res
import ecosdolugar.composeapp.generated.resources.hero_forest
import org.jetbrains.compose.resources.painterResource

@Composable
fun DisplayNameScreen(
    initialName: String,
    strings: AppLanguageStrings,
    isSaving: Boolean,
    errorMessage: String?,
    onNameChanged: (String) -> Unit,
    onContinue: () -> Unit
) {
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
                            0.42f to PineDeep.copy(alpha = 0.6f),
                            1f to PineDeep
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = strings.displayNameTitle,
                color = SurfaceCream,
                fontFamily = FontFamily.Serif,
                fontSize = 33.sp,
                lineHeight = 37.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(11.dp))
            Text(
                text = strings.displayNameDescription,
                color = SurfaceContainerHigh,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
            Spacer(Modifier.height(22.dp))
            OutlinedTextField(
                value = initialName,
                onValueChange = onNameChanged,
                label = { Text(strings.displayNameLabel) },
                singleLine = true,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.12f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                    disabledContainerColor = Color.White.copy(alpha = 0.08f),
                    focusedTextColor = SurfaceCream,
                    unfocusedTextColor = SurfaceCream,
                    disabledTextColor = SurfaceCream.copy(alpha = 0.6f),
                    focusedLabelColor = TerracottaFixedDim,
                    unfocusedLabelColor = SurfaceContainerHigh,
                    cursorColor = TerracottaGold,
                    focusedBorderColor = TerracottaGold,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.16f)
                )
            )
            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMessage, color = Color(0xFFFFD5D5), fontSize = 11.sp, lineHeight = 15.sp)
            }
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onContinue,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TerracottaGold,
                    contentColor = PineDeep
                )
            ) {
                Text(
                    if (isSaving) strings.connecting else strings.displayNameContinue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
