package com.ecosdolugar.data

import com.ecosdolugar.model.CharacterItem
import com.ecosdolugar.model.CharacterStat

object CharactersRepository {
    val characters: List<CharacterItem> = listOf(
        CharacterItem(
            id = "meiga",
            name = "Meiga",
            galicianTitle = "A Sabia dos Meigallos e Herbas",
            archetype = "Mística Ancestral",
            badge = "Sabedoría Ancestral",
            summary = "Mestra do lume purificador, das fontes sagradas e das herbas medicinais dos bosques galegos.",
            lore = "As meigas non son malas nin boas: son as custodias do coñecemento esquecido que a terra garda. Saben escoitar o murmurio dos regatos, curar con herba de San Xoán e afastar o mal de ollo antes de cruzar as corredoiras nocturnas da Raia.",
            abilityName = "Visión dos Meigallos",
            abilityDescription = "Revela pistas automáticas nos enigmas nocturnos e descifra inscricións antigas nas pedras dos outeiros.",
            quote = "«Eu non creo nas bruxas, pero habelas, hainas.»",
            sacredItem = "Herba de San Xoán & Caldeiro de Cobre",
            element = "Fogo Sagrado & Auga de Ronsel",
            accentColorHex = 0xFFC084FCL,
            stats = listOf(
                CharacterStat("Sabedoría Ancestral", 96),
                CharacterStat("Enerxía Mística", 98),
                CharacterStat("Conexión coa Terra", 92),
                CharacterStat("Furtividade", 78)
            ),
            avatarEmoji = "🔮"
        ),
        CharacterItem(
            id = "trasno",
            name = "Trasno",
            galicianTitle = "O Fedello das Corredoiras",
            archetype = "Guía Travieso e Rápido",
            badge = "Mestre dos Atallos",
            summary = "Espírito pillabán, ledo e incansable que coñece cada paso secreto e burato nos muros da fronteira.",
            lore = "Pequeno, áxil e imprevisible. Se che cae unha moeda, se tropezas cun toxo ou se encontras un atallo impensable polo medio das viñas, é cousa do trasno. Cando se fai o teu amigo, non hai camiño pechado nin barreira na fronteira que non che faga pasar.",
            abilityName = "Paso dos Atallos",
            abilityDescription = "Permite descubrir carreiros ocultos, pasos de contrabandistas e atallar entre paradas das rutas históricas.",
            quote = "«Se algo perdeches polo camiño, búscao no meu peto.»",
            sacredItem = "Gorro Vermello & Castaña Furada",
            element = "Vento de Corredoira & Toxos",
            accentColorHex = 0xFFF97316L,
            stats = listOf(
                CharacterStat("Agilidade e Velocidade", 99),
                CharacterStat("Astucia da Raia", 95),
                CharacterStat("Furtividade Nocturna", 94),
                CharacterStat("Paciencia", 45)
            ),
            avatarEmoji = "👺"
        ),
        CharacterItem(
            id = "moura",
            name = "Moura dos Castros",
            galicianTitle = "A Gardiá das Pedras Milenarias",
            archetype = "Nobreza Telúrica",
            badge = "Memoria dos Castros",
            summary = "Espírito encantado que vive baixo os castros e penedos celtas, peiteando os seus cabelos de ouro ao solpor.",
            lore = "As mouras son donas dos tesouros subterráneos, das lendas que dormen baixo os muros de pedra seca de Santa Trega e dos castros do Miño. Falan a lingua esquecida dos celtas e agasallan con revelacións a quen viaxa con respecto e humildade.",
            abilityName = "Memoria dos Petróglifos",
            abilityDescription = "Desvela a historia oculta dos xacementos arqueolóxicos e duplica as recompensas nos selos do pasaporte.",
            quote = "«As pedras gardan o tesouro de quen sabe escoitar o rumor da terra.»",
            sacredItem = "Peite de Ouro & Fuso de Liño",
            element = "Pedra Granítica & Ouro Telúrico",
            accentColorHex = 0xFFFBBF24L,
            stats = listOf(
                CharacterStat("Memoria Histórica", 98),
                CharacterStat("Presenza Rexia", 94),
                CharacterStat("Vínculo Arqueolóxico", 97),
                CharacterStat("Adaptabilidade", 72)
            ),
            avatarEmoji = "👑"
        ),
        CharacterItem(
            id = "lavandeira",
            name = "Lavandeira",
            galicianTitle = "O Eco das Augas do Miño",
            archetype = "Espírito Fluvial",
            badge = "Voz das Correntes",
            summary = "Figura lendaria que lava os seus panos baixo a néboa do río Miño cantando historias de mariñeiros e pescadores.",
            lore = "Vaga entre os muíños de auga e os lavadoiros de pedra da comarca do Baixo Miño. Se un viaxeiro a axuda a escorrer os panos xirando cara ao lado correcto, a Lavandeira outorgaralle a capacidade de escoitar o pasado a través do son da corrente.",
            abilityName = "Eco Fluvial Inmersivo",
            abilityDescription = "Mellora a claridade dos relatos de audio e permite sintonizar frecuencias orais ocultas xunto ao río.",
            quote = "«A auga que corre leva a mágoa e devolve a memoria dos nosos devanceiros.»",
            sacredItem = "Pano de Liño Crú & Cuncha do Miño",
            element = "Néboa Fluvial & Auga Doce",
            accentColorHex = 0xFF38BDF8L,
            stats = listOf(
                CharacterStat("Intuición e Escoita", 97),
                CharacterStat("Sensibilidade Lírica", 95),
                CharacterStat("Harmonía co Río", 99),
                CharacterStat("Forza Bruta", 50)
            ),
            avatarEmoji = "🌊"
        ),
        CharacterItem(
            id = "lobishome",
            name = "Lobishome",
            galicianTitle = "O Explorador dos Bosques Profundos",
            archetype = "Rastreador Salvaxe",
            badge = "Espírito da Fraga",
            summary = "O fillo do bosque que camiña polas serras do Xurés e da Peneda baixo a lúa chea protexendo os carballais.",
            lore = "Non é unha besta asasina como nas fábulas estrañas, senón un ser que conserva o instinto puro dos carballais milenarios galegos. Coñece as pegadas dos animais, os refuxios de pastores na serra e como sobrevivir cando cae a neboeira máis espesa.",
            abilityName = "Rastro Salvaxe",
            abilityDescription = "Orientación infalible mesmo sen cobertura nin GPS e resistencia aumentada en sendeiros escarpados.",
            quote = "«Na espesura da fraga ningún sendeiro é descoñecido para quen ten ollos de lobo.»",
            sacredItem = "Amuleto de Hastas & Dente de Lobo",
            element = "Carballal Centenario & Terra Húmida",
            accentColorHex = 0xFF4ADE80L,
            stats = listOf(
                CharacterStat("Instinto de Rastrexo", 99),
                CharacterStat("Vigor e Resistencia", 96),
                CharacterStat("Sentidos Agudos", 98),
                CharacterStat("Diplomacia Social", 40)
            ),
            avatarEmoji = "🐺"
        ),
        CharacterItem(
            id = "contrabandista",
            name = "Contrabandista da Raia",
            galicianTitle = "O Mestre da Noite e dos Marcos",
            archetype = "Estratega da Fronteira",
            badge = "Mestre da Noite",
            summary = "O audaz camiñante que cruzaba a raia seca e o río de noite con sacos de café, pana e fariña unindo dúas beiras irmás.",
            lore = "A Raia nunca foi unha fenda, senón un punto de encontro. O contrabandista coñece os pactos tácitos, os sinais con farol de carburo a través do Miño e a localización exacta de cada marco de pedra entre Galicia e o Norte de Portugal.",
            abilityName = "Paso Franco Fronteirizo",
            abilityDescription = "Identifica ao instante os antigos refuxios de contrabando, pasos secretos e marcos territoriais históricos.",
            quote = "«Dúas beiras, un mesmo río; dous países, unha mesma xente que se axuda na noite.»",
            sacredItem = "Farol de Carburo & Saco de Café Torrefacto",
            element = "Noite Pecha & Café da Fronteira",
            accentColorHex = 0xFFFABA71L,
            stats = listOf(
                CharacterStat("Coñecemento da Raia", 99),
                CharacterStat("Furtividade e Discreción", 96),
                CharacterStat("Empatía Fronteiriza", 95),
                CharacterStat("Coraxe Nocturno", 92)
            ),
            avatarEmoji = "🕯️"
        )
    )

    fun getById(id: String): CharacterItem {
        return characters.find { it.id == id } ?: characters.first()
    }
}
