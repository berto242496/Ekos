package com.ecosdolugar.localization

data class ComponentStrings(
    val continueWithGoogle: String,
    val connectingWithGoogle: String
)

fun componentStrings(language: AppLanguage): ComponentStrings =
    when (language) {
        AppLanguage.SPANISH -> ComponentStrings(
            continueWithGoogle = "Continuar con Google",
            connectingWithGoogle = "Conectando con Google..."
        )
        AppLanguage.GALICIAN -> ComponentStrings(
            continueWithGoogle = "Continuar con Google",
            connectingWithGoogle = "Conectando con Google..."
        )
        AppLanguage.ENGLISH -> ComponentStrings(
            continueWithGoogle = "Continue with Google",
            connectingWithGoogle = "Connecting with Google..."
        )
    }
