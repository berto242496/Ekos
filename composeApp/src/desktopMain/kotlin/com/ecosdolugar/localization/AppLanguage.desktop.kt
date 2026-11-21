package com.ecosdolugar.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.util.Locale
import java.util.prefs.Preferences

@Composable
actual fun rememberAppLanguageController(): AppLanguageController {
    val preferences = remember {
        Preferences.userNodeForPackage(AppLanguageController::class.java)
    }
    val state = remember {
        mutableStateOf(
            AppLanguage.fromLanguageTag(
                preferences.get("language", Locale.getDefault().toLanguageTag())
            )
        )
    }
    return remember(state, preferences) {
        AppLanguageController(state) { language ->
            preferences.put("language", language.code)
        }
    }
}
