package com.ecosdolugar.ui.screens

import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.stopTrialStrings

internal fun stopCompletionTexts(language: AppLanguage): List<String> =
    stopTrialStrings(language).completionMessages
