package com.ecosdolugar.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults

@Composable
actual fun rememberAppLanguageController(): AppLanguageController {
    val preferences = NSUserDefaults.standardUserDefaults
    val state = remember {
        mutableStateOf(
            AppLanguage.fromLanguageTag(
                preferences.stringForKey("app_language")
                    ?: NSLocale.currentLocale.languageCode
            )
        )
    }
    return remember(state) {
        AppLanguageController(state) { language ->
            preferences.setObject(language.code, forKey = "app_language")
        }
    }
}
