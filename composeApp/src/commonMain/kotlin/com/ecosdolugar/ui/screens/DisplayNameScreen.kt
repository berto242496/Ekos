package com.ecosdolugar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.localization.AppLanguageStrings
import com.ecosdolugar.ui.theme.PineDeep
import com.ecosdolugar.ui.theme.SurfaceCream
import com.ecosdolugar.ui.theme.TerracottaGold

@Composable
fun DisplayNameScreen(
    initialName: String,
    strings: AppLanguageStrings,
    isSaving: Boolean,
    errorMessage: String?,
    onNameChanged: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PineDeep)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(strings.displayNameTitle, color = SurfaceCream, fontSize = 24.sp)
        Spacer(Modifier.height(10.dp))
        Text(strings.displayNameDescription, color = SurfaceCream.copy(alpha = 0.75f), fontSize = 14.sp)
        Spacer(Modifier.height(22.dp))
        OutlinedTextField(
            value = initialName,
            onValueChange = onNameChanged,
            label = { Text(strings.displayNameLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F2618),
                unfocusedContainerColor = Color(0xFF0F2618),
                focusedTextColor = SurfaceCream,
                unfocusedTextColor = SurfaceCream,
                focusedLabelColor = TerracottaGold,
                unfocusedLabelColor = SurfaceCream.copy(alpha = 0.7f),
                cursorColor = TerracottaGold,
                focusedBorderColor = TerracottaGold,
                unfocusedBorderColor = SurfaceCream.copy(alpha = 0.5f)
            )
        )
        if (errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(errorMessage, color = Color(0xFFFFB4AB), fontSize = 12.sp)
        }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onContinue,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TerracottaGold,
                contentColor = PineDeep
            )
        ) {
            Text(if (isSaving) strings.connecting else strings.displayNameContinue)
        }
    }
}
