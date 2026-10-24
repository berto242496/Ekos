package com.ecosdolugar.util

import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.appMessageStrings

private const val MAX_DISPLAY_NAME_LENGTH = 60

object ProfanityFilter {
    private val blockedWords = setOf(
        // Castellano
        "puta", "putas", "puto", "putos", "puton", "putona", "joputa", "hijoputa", "hijaputa",
        "mierda", "mierdas", "cabron", "cabrones", "cabrona", "cabronas",
        "gilipollas", "gilipolla", "gilipollez", "subnormal", "subnormales", "imbecil", "imbeciles",
        "idiota", "idiotas", "capullo", "capullos", "joder", "jodida", "jodido", "jodete",
        "hostia", "hostias", "coño", "cono", "conos", "pendejo", "pendejos", "maricon", "maricones",
        "marica", "maricas", "zorra", "zorras", "chinga", "chingada", "mamon", "mamones", "mamada",
        "verga", "vergas", "pinche", "culo", "culos", "polla", "pollas", "ojete", "pene", "follar",
        "cagada", "cagar", "pajero", "pajillero", "retrasado", "retrasada", "tarado", "tarada",
        "nazi", "negrata", "sudaca",
        // Galego
        "merda", "merdas", "cona", "conas", "carallo", "carallos", "caralho", "foder", "fodendo",
        "xilipollas", "xilipolla", "fillodaputa", "fillaputa", "cagalar", "cagalhon",
        // English
        "fuck", "fucker", "fucking", "shit", "bitch", "bitches", "asshole", "bastard", "dick",
        "pussy", "cunt", "whore", "slut", "motherfucker", "nigger", "faggot"
    ).map { normalize(it) }.toSet()

    fun displayNameError(name: String, language: AppLanguage = AppLanguage.SPANISH): String? {
        val trimmed = name.trim()
        val strings = appMessageStrings(language)
        return when {
            trimmed.isEmpty() -> strings.displayNameEmpty
            trimmed.length > MAX_DISPLAY_NAME_LENGTH -> strings.displayNameTooLong(MAX_DISPLAY_NAME_LENGTH)
            containsProfanity(trimmed) -> strings.displayNameProfanity
            else -> null
        }
    }

    fun containsProfanity(text: String): Boolean {
        // "@" and "$" are kept because normalize() maps them to letters (leetspeak).
        val words = text.split(Regex("[^\\p{L}\\p{N}@$]+"))
            .map { normalize(it) }
            .filter { it.isNotEmpty() }
        if (words.any { it in blockedWords }) return true
        // Catches letters separated by spaces or punctuation, such as "p u t a".
        val joined = words.joinToString("")
        return joined in blockedWords
    }

    private fun normalize(raw: String): String {
        val lower = raw.lowercase()
            .replace('á', 'a').replace('à', 'a').replace('ä', 'a').replace('â', 'a')
            .replace('é', 'e').replace('è', 'e').replace('ë', 'e').replace('ê', 'e')
            .replace('í', 'i').replace('ì', 'i').replace('ï', 'i').replace('î', 'i')
            .replace('ó', 'o').replace('ò', 'o').replace('ö', 'o').replace('ô', 'o')
            .replace('ú', 'u').replace('ù', 'u').replace('ü', 'u').replace('û', 'u')
            .replace('ñ', 'n').replace('ç', 'c')
            .replace('0', 'o').replace('1', 'i').replace('3', 'e')
            .replace('4', 'a').replace('5', 's').replace('7', 't')
            .replace('@', 'a').replace('$', 's')
        // Collapses repeated letters so "puuuta" matches "puta".
        return lower.filter { it.isLetter() }.replace(Regex("(.)\\1+"), "$1")
    }
}
