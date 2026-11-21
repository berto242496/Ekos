package com.ecosdolugar.data

import ecosdolugar.composeapp.generated.resources.Res
import ecosdolugar.composeapp.generated.resources.character_meiga
import ecosdolugar.composeapp.generated.resources.character_nubeiro
import ecosdolugar.composeapp.generated.resources.character_trasno
import ecosdolugar.composeapp.generated.resources.character_urco
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
            avatarImage = Res.drawable.character_meiga
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
            avatarImage = Res.drawable.character_trasno
        ),
        CharacterItem(
            id = "nubeiro",
            name = "Nubeiro",
            galicianTitle = "O Nubeiro",
            archetype = "Señor das nubes e das tormentas",
            badge = "Señor das tormentas",
            summary = "Figura lendaria da tradición galega, señor das nubes e das tormentas.",
            lore = "Figura lendaria da tradición galega, señor das nubes e das tormentas. Dise que percorre os ceos provocando treboadas, raios e choiva, envolto en néboas e ventos misteriosos.",
            abilityName = "Viaxe entre troons e lóstregos",
            abilityDescription = "Percorre os ceos entre tronos e lóstregos, e sabe que toda viaxe ten o seu destino. Se segues o seu ronsel ata o final, quizais as nubes se abran para ti e revelen un agasallo digno dos deuses do ceo.",
            quote = "«Quen segue o rastro das nubes chega sempre ao seu destino.»",
            sacredItem = "Lóstrego e néboa",
            element = "Ceo e tormenta",
            accentColorHex = 0xFF60A5FAL,
            stats = emptyList(),
            avatarImage = Res.drawable.character_nubeiro
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
            avatarImage = Res.drawable.character_urco
        )
    )

    fun getById(id: String): CharacterItem {
        return characters.find { it.id == id } ?: characters.first()
    }
}
