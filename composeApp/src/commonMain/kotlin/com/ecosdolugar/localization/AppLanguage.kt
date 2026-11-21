package com.ecosdolugar.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

enum class AppLanguage(val code: String, val displayName: String) {
    SPANISH("es", "Español"),
    GALICIAN("gl", "Galego"),
    ENGLISH("en", "English");

    companion object {
        fun fromLanguageTag(languageTag: String?): AppLanguage =
            when (languageTag?.substringBefore('-')?.substringBefore('_')?.lowercase()) {
                "es" -> SPANISH
                "gl" -> GALICIAN
                else -> ENGLISH
            }
    }
}

class AppLanguageController internal constructor(
    private val state: MutableState<AppLanguage>,
    private val persist: (AppLanguage) -> Unit
) {
    val current: AppLanguage
        get() = state.value

    fun select(language: AppLanguage) {
        state.value = language
        persist(language)
    }
}

@Composable
expect fun rememberAppLanguageController(): AppLanguageController
