package com.ecosdolugar.localization

import com.ecosdolugar.model.Category
import com.ecosdolugar.model.Difficulty

fun categoryLabel(category: Category, language: AppLanguage): String =
    when (language) {
        AppLanguage.SPANISH -> when (category) {
            Category.ALL -> "Todas"
            Category.HISTORIA -> "Historia"
            Category.LEYENDAS -> "Leyendas"
            Category.FAMILIAS -> "Familias"
            Category.PATRIMONIO -> "Patrimonio"
            Category.NATURALEZA -> "Naturaleza"
        }
        AppLanguage.GALICIAN -> when (category) {
            Category.ALL -> "Todas"
            Category.HISTORIA -> "Historia"
            Category.LEYENDAS -> "Lendas"
            Category.FAMILIAS -> "Familias"
            Category.PATRIMONIO -> "Patrimonio"
            Category.NATURALEZA -> "Natureza"
        }
        AppLanguage.ENGLISH -> when (category) {
            Category.ALL -> "All"
            Category.HISTORIA -> "History"
            Category.LEYENDAS -> "Legends"
            Category.FAMILIAS -> "Families"
            Category.PATRIMONIO -> "Heritage"
            Category.NATURALEZA -> "Nature"
        }
    }

fun difficultyDetail(difficulty: Difficulty, language: AppLanguage): String =
    when (language) {
        AppLanguage.SPANISH -> when (difficulty) {
            Difficulty.FACIL -> "Paseo accesible"
            Difficulty.MEDIA -> "Camino tradicional"
            Difficulty.EXIGENTE -> "Costa o desnivel"
        }
        AppLanguage.GALICIAN -> when (difficulty) {
            Difficulty.FACIL -> "Paseo accesible"
            Difficulty.MEDIA -> "Camiño tradicional"
            Difficulty.EXIGENTE -> "Costa ou desnivel"
        }
        AppLanguage.ENGLISH -> when (difficulty) {
            Difficulty.FACIL -> "Accessible walk"
            Difficulty.MEDIA -> "Traditional path"
            Difficulty.EXIGENTE -> "Coastal or steep terrain"
        }
    }
