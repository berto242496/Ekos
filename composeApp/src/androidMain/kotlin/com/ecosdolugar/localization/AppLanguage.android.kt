package com.ecosdolugar.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
actual fun rememberAppLanguageController(): AppLanguageController {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
    }
    val state = remember(preferences) {
        mutableStateOf(
            AppLanguage.fromLanguageTag(
                preferences.getString("language", null) ?: Locale.getDefault().toLanguageTag()
            )
        )
    }
    return remember(state, preferences) {
        AppLanguageController(state) { language ->
            preferences.edit().putString("language", language.code).apply()
        }
    }
}
