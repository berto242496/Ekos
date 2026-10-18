package com.ecosdolugar.data

import com.ecosdolugar.model.CharacterItem

object CharactersRepository {
    val characters: List<CharacterItem> = listOf(
        CharacterItem(
            id = "meiga",
            name = "Meiga",
            galicianTitle = "A Meiga",
            archetype = "Mística ancestral",
            badge = "Sabedoría ancestral",
            summary = "Coñecedora das herbas, dos remedios e dos saberes máxicos.",
            lore = "Muller sabia da tradición galega, coñecedora das herbas, dos remedios e dos saberes máxicos. Entre a realidade e a lenda, é unha das figuras máis misteriosas do noso folclore.",
            abilityName = "Sabedoría entre herbas e encantamentos",
            abilityDescription = "Entre herbas e encantamentos, coñece segredos que poucos poden ver. Escoita os seus murmurios e quizais atopes a luz que precisas cando a resposta se agocha na néboa.",
            quote = "«Escoita os murmurios das herbas e atoparás o teu camiño.»",
            sacredItem = "Herbas e caldeiro",
            element = "Natureza e maxia",
            accentColorHex = 0xFFC084FCL,
            stats = emptyList(),
            avatarEmoji = "🔮"
        ),
        CharacterItem(
            id = "trasno",
            name = "Trasno",
            galicianTitle = "O Trasno",
            archetype = "Fedello e traveso",
            badge = "Astucia dos camiños",
            summary = "Pequeno ser máxico e traveso, afeito a sorprender a quen se cruza con el.",
            lore = "Pequeno ser máxico e traveso da mitoloxía galega, famoso polas súas trasnadas e por facer das súas nas casas. Gústalle enredar, agochar cousas e sorprender a quen se cruza con el.",
            abilityName = "Sorpresas en cada recuncho",
            abilityDescription = "Alí onde outros só ven un camiño, el ve unha oportunidade para facer das súas. Cada recuncho que descubras pode gardar unha pequena sorpresa agardando por ti.",
            quote = "«Se algo perdeches polo camiño, quizais o teña eu!»",
            sacredItem = "Gorro vermello",
            element = "Vento e corredoiras",
            accentColorHex = 0xFFF97316L,
            stats = emptyList(),
            avatarEmoji = "👺"
        ),
        CharacterItem(
            id = "moura",
            name = "Moura",
            galicianTitle = "A Moura",
            archetype = "Gardiá de lendas",
            badge = "Memoria dos castros",
            summary = "Figura lendaria vinculada a castros, fontes, penedos e tesouros agochados.",
            lore = "Figura lendaria de gran beleza e misterio, vinculada a castros, fontes, penedos e tesouros agochados. Nas lendas galegas, adoita gardar encantamentos e segredos ancestrais.",
            abilityName = "Tesouros e lendas antigas",
            abilityDescription = "Garda tesouros antigos e coñece o valor de cada paso dado baixo o feitizo da súa mirada. Se completas o camiño ata o final, quizais recibas unha recompensa digna dunha lenda.",
            quote = "«As pedras gardan o tesouro de quen sabe escoitar a terra.»",
            sacredItem = "Tesouros dos castros",
            element = "Pedra e encantamentos",
            accentColorHex = 0xFFFBBF24L,
            stats = emptyList(),
            avatarEmoji = "👑"
        ),
        CharacterItem(
            id = "urco",
            name = "Urco",
            galicianTitle = "O Urco",
            archetype = "Criatura da néboa",
            badge = "Maxia ancestral",
            summary = "Criatura lendaria das lendas mariñeiras e da tradición popular de Pontevedra.",
            lore = "Criatura lendaria, representada habitualmente como un enorme can negro con cornos que aparece entre a néboa. Está especialmente ligado ás lendas mariñeiras e á tradición popular de Pontevedra.",
            abilityName = "Instinto entre sombras e misterios",
            abilityDescription = "Entre sombras e misterios, percibe verdades que escapan aos demais. Cando acertes alí onde outros dubidan, sentirás como a súa maxia ancestral medra contigo.",
            quote = "«Entre a néboa, hai verdades que só o instinto pode atopar.»",
            sacredItem = "Néboa mariñeira",
            element = "Mar e misterio",
            accentColorHex = 0xFF4ADE80L,
            stats = emptyList(),
            avatarEmoji = "🐺"
        )
    )

    fun getById(id: String): CharacterItem {
        val canonicalId = when (id) {
            "lavandeira", "lobishome", "caminante", "contrabandista" -> "urco"
            else -> id
        }
        return characters.find { it.id == canonicalId } ?: characters.first()
    }
}
