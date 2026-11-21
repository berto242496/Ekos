package com.ecosdolugar.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.SafetyStrings
import com.ecosdolugar.localization.safetyStrings
import com.ecosdolugar.model.ReportReason
import com.ecosdolugar.ui.theme.OnSurfaceVariant

const val PRIVACY_POLICY_URL = "https://berto242496.github.io/Ekos/legal/privacy-policy"
const val TERMS_OF_USE_URL = "https://berto242496.github.io/Ekos/legal/terms-of-use"

@Composable
fun ReportDialog(
    strings: SafetyStrings,
    onDismiss: () -> Unit,
    onSubmit: (ReportReason) -> Unit
) {
    var selected by remember { mutableStateOf(ReportReason.INAPPROPRIATE) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.reportDialogTitle) },
        text = {
            Column {
                Text(strings.reportDialogBody, fontSize = 13.sp, color = OnSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                ReportReason.entries.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = reason == selected, role = Role.RadioButton) { selected = reason },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = reason == selected, onClick = null)
                        Text(strings.reasonLabel(reason), modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSubmit(selected) }) { Text(strings.send) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } }
    )
}

@Composable
fun ConfirmActionDialog(
    title: String,
    body: String,
    confirmLabel: String,
    cancelLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(cancelLabel) } }
    )
}

@Composable
fun LegalLinks(
    language: AppLanguage,
    modifier: Modifier = Modifier,
    color: Color = OnSurfaceVariant
) {
    val strings = safetyStrings(language)
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegalLink(strings.privacyPolicy, color) { uriHandler.openUri(PRIVACY_POLICY_URL) }
        Text("·", color = color, modifier = Modifier.padding(horizontal = 8.dp))
        LegalLink(strings.termsOfService, color) { uriHandler.openUri(TERMS_OF_USE_URL) }
    }
}

@Composable
private fun LegalLink(text: String, color: Color, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = color,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 8.dp)
    )
}
