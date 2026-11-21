package com.ecosdolugar.model

import org.jetbrains.compose.resources.DrawableResource

data class CharacterStat(
    val label: String,
    val value: Int // 0 to 100
)

data class CharacterItem(
    val id: String,
    val name: String,
    val galicianTitle: String,
    val archetype: String,
    val badge: String,
    val summary: String,
    val lore: String,
    val abilityName: String,
    val abilityDescription: String,
    val quote: String,
    val sacredItem: String,
    val element: String,
    val accentColorHex: Long, // e.g. 0xFFC084FC
    val stats: List<CharacterStat>,
    val avatarImage: DrawableResource
)
