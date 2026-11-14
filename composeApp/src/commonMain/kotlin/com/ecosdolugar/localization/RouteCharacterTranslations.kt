package com.ecosdolugar.localization

fun routeCharacterArchetype(characterId: String, language: AppLanguage, fallback: String): String {
    val labels = when (language) {
        AppLanguage.SPANISH -> mapOf(
            "meiga" to "Mística ancestral",
            "nubeiro" to "Señor de las nubes y las tormentas",
            "trasno" to "Travieso y juguetón",
            "urco" to "Criatura de la niebla"
        )
        AppLanguage.GALICIAN -> mapOf(
            "meiga" to "Mística ancestral",
            "nubeiro" to "Señor das nubes e das tormentas",
            "trasno" to "Fedello e traveso",
            "urco" to "Criatura da néboa"
        )
        AppLanguage.ENGLISH -> mapOf(
            "meiga" to "Ancestral mystic",
            "nubeiro" to "Lord of clouds and storms",
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
            "nubeiro" to "Viaje entre truenos y relámpagos",
            "trasno" to "Sorpresas en cada rincón",
            "urco" to "Instinto entre sombras y misterios"
        )
        AppLanguage.GALICIAN -> mapOf(
            "meiga" to "Sabedoría entre herbas e encantamentos",
            "nubeiro" to "Viaxe entre troons e lóstregos",
            "trasno" to "Sorpresas en cada recuncho",
            "urco" to "Instinto entre sombras e misterios"
        )
        AppLanguage.ENGLISH -> mapOf(
            "meiga" to "Wisdom among herbs and enchantments",
            "nubeiro" to "Journey through thunder and lightning",
            "trasno" to "Surprises around every corner",
            "urco" to "Instinct among shadows and mysteries"
        )
    }
    return labels[characterId] ?: fallback
}
