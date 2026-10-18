package com.ecosdolugar.localization

fun routeCharacterArchetype(characterId: String, language: AppLanguage, fallback: String): String {
    val labels = when (language) {
        AppLanguage.SPANISH -> mapOf(
            "meiga" to "Mística ancestral",
            "moura" to "Guardiana de leyendas",
            "trasno" to "Travieso y juguetón",
            "urco" to "Criatura de la niebla"
        )
        AppLanguage.GALICIAN -> mapOf(
            "meiga" to "Mística ancestral",
            "moura" to "Gardiá de lendas",
            "trasno" to "Fedello e traveso",
            "urco" to "Criatura da néboa"
        )
        AppLanguage.ENGLISH -> mapOf(
            "meiga" to "Ancestral mystic",
            "moura" to "Guardian of legends",
            "trasno" to "Mischievous trickster",
            "urco" to "Creature of the mist"
        )
    }
    return labels[characterId] ?: fallback
}

fun routeCharacterAbility(characterId: String, language: AppLanguage, fallback: String): String {
    val labels = when (language) {
        AppLanguage.SPANISH -> mapOf(
            "meiga" to "Sabiduría entre hierbas y encantamientos",
            "moura" to "Tesoros y leyendas antiguas",
            "trasno" to "Sorpresas en cada rincón",
            "urco" to "Instinto entre sombras y misterios"
        )
        AppLanguage.GALICIAN -> mapOf(
            "meiga" to "Sabedoría entre herbas e encantamentos",
            "moura" to "Tesouros e lendas antigas",
            "trasno" to "Sorpresas en cada recuncho",
            "urco" to "Instinto entre sombras e misterios"
        )
        AppLanguage.ENGLISH -> mapOf(
            "meiga" to "Wisdom among herbs and enchantments",
            "moura" to "Ancient treasures and legends",
            "trasno" to "Surprises around every corner",
            "urco" to "Instinct among shadows and mysteries"
        )
    }
    return labels[characterId] ?: fallback
}
