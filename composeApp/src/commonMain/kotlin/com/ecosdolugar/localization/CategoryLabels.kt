package com.ecosdolugar.localization

import com.ecosdolugar.model.Category
import com.ecosdolugar.model.Difficulty

fun categoryLabel(category: Category, language: AppLanguage): String =
    when (language) {
        AppLanguage.SPANISH -> when (category) {
            Category.ALL -> "Todas"
            Category.HISTORIA -> "Historia"
            Category.LEYENDAS -> "Leyendas"
            Category.NATURALEZA -> "Naturaleza"
            Category.GASTRONOMIA -> "Gastronomía"
            Category.ARTE -> "Arte"
            Category.TRADICIONES -> "Tradiciones"
        }
        AppLanguage.GALICIAN -> when (category) {
            Category.ALL -> "Todas"
            Category.HISTORIA -> "Historia"
            Category.LEYENDAS -> "Lendas"
            Category.NATURALEZA -> "Natureza"
            Category.GASTRONOMIA -> "Gastronomía"
            Category.ARTE -> "Arte"
            Category.TRADICIONES -> "Tradicións"
        }
        AppLanguage.ENGLISH -> when (category) {
            Category.ALL -> "All"
            Category.HISTORIA -> "History"
            Category.LEYENDAS -> "Legends"
            Category.NATURALEZA -> "Nature"
            Category.GASTRONOMIA -> "Gastronomy"
            Category.ARTE -> "Art"
            Category.TRADICIONES -> "Traditions"
        }
    }

fun difficultyDetail(difficulty: Difficulty, language: AppLanguage): String =
    when (language) {
        AppLanguage.SPANISH -> when (difficulty) {
            Difficulty.FACIL -> "Recorrido corto, terreno sencillo y poco esfuerzo físico."
            Difficulty.MEDIA -> "Recorrido con algunas pendientes o tramos irregulares."
            Difficulty.EXIGENTE -> "Recorrido largo, con desniveles o terreno exigente."
        }
        AppLanguage.GALICIAN -> when (difficulty) {
            Difficulty.FACIL -> "Percorrido curto, terreo sinxelo e pouco esforzo físico."
            Difficulty.MEDIA -> "Percorrido con algunhas pendentes ou tramos irregulares."
            Difficulty.EXIGENTE -> "Percorrido longo, con desniveis ou terreo esixente."
        }
        AppLanguage.ENGLISH -> when (difficulty) {
            Difficulty.FACIL -> "Short route, easy terrain and little physical effort."
            Difficulty.MEDIA -> "Route with some slopes or uneven sections."
            Difficulty.EXIGENTE -> "Long route, with steep changes in elevation or demanding terrain."
        }
    }
