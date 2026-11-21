package com.ecosdolugar.localization

data class CharacterSelectionStrings(
    val eyebrow: String,
    val title: String,
    val introduction: String,
    val selectorLabel: String,
    val powerLabel: String,
    val description: String,
    val powerDescription: String,
    val confirmPrefix: String,
    val changeHint: String,
    val backLabel: String
)

private data class CharacterCopy(
    val description: String,
    val power: String
)

fun characterSelectionStrings(language: AppLanguage, characterId: String): CharacterSelectionStrings {
    val common = when (language) {
        AppLanguage.SPANISH -> listOf(
            "MITOLOGÍA Y LEYENDAS DE GALICIA",
            "Elige tu personaje",
            "Cuatro figuras de la tradición gallega te acompañarán en cada ruta.",
            "PERSONAJES",
            "Su poder",
            "Comenzar expedición como",
            "Podrás cambiar de personaje en cualquier momento desde tu perfil o cuaderno de campo.",
            "Volver"
        )
        AppLanguage.GALICIAN -> listOf(
            "MITOLOXÍA E LENDAS DE GALICIA",
            "Escolle o teu personaxe",
            "Catro figuras da tradición galega acompañarante en cada ruta.",
            "PERSONAXES",
            "O seu poder",
            "Comezar a expedición como",
            "Poderás cambiar de personaxe en calquera momento desde o teu perfil ou caderno de campo.",
            "Volver"
        )
        AppLanguage.ENGLISH -> listOf(
            "MYTHOLOGY AND LEGENDS OF GALICIA",
            "Choose your character",
            "Four figures from Galician tradition will accompany you on every route.",
            "CHARACTERS",
            "Their power",
            "Begin your expedition as",
            "You can change your character at any time from your profile or field journal.",
            "Back"
        )
    }

    val copy = when (language) {
        AppLanguage.SPANISH -> mapOf(
            "meiga" to CharacterCopy(
                "Mujer sabia de la tradición gallega, conocedora de las hierbas, los remedios y los saberes mágicos. Entre la realidad y la leyenda, es una de las figuras más misteriosas de nuestro folclore.",
                "Entre hierbas y encantamientos, conoce secretos que pocos pueden ver. Escucha sus murmullos y quizá encuentres la luz que necesitas cuando la respuesta se oculta en la niebla."
            ),
            "trasno" to CharacterCopy(
                "Pequeño ser mágico y travieso de la mitología gallega, famoso por sus trastadas y por hacer de las suyas en las casas. Le gusta enredar, esconder cosas y sorprender a quien se cruza con él.",
                "Allí donde otros solo ven un camino, él ve una oportunidad para hacer de las suyas. Cada rincón que descubras puede guardar una pequeña sorpresa esperándote."
            ),
            "nubeiro" to CharacterCopy(
                "Figura legendaria de la tradición gallega, señor de las nubes y las tormentas. Se dice que recorre los cielos provocando truenos, rayos y lluvia, envuelto en nieblas y vientos misteriosos.",
                "Recorre los cielos entre truenos y relámpagos, y sabe que todo viaje tiene su destino. Si sigues su rastro hasta el final, quizás las nubes se abran para ti y revelen un regalo digno de los dioses del cielo."
            ),
            "urco" to CharacterCopy(
                "Criatura legendaria, representada habitualmente como un enorme perro negro con cuernos que aparece entre la niebla. Está especialmente ligado a las leyendas marineras y a la tradición popular de Pontevedra.",
                "Entre sombras y misterios, percibe verdades que escapan a los demás. Cuando aciertes allí donde otros dudan, sentirás cómo crece contigo su magia ancestral."
            )
        )
        AppLanguage.GALICIAN -> mapOf(
            "meiga" to CharacterCopy(
                "Muller sabia da tradición galega, coñecedora das herbas, dos remedios e dos saberes máxicos. Entre a realidade e a lenda, é unha das figuras máis misteriosas do noso folclore.",
                "Entre herbas e encantamentos, coñece segredos que poucos poden ver. Escoita os seus murmurios e quizais atopes a luz que precisas cando a resposta se agocha na néboa."
            ),
            "trasno" to CharacterCopy(
                "Pequeno ser máxico e traveso da mitoloxía galega, famoso polas súas trasnadas e por facer das súas nas casas. Gústalle enredar, agochar cousas e sorprender a quen se cruza con el.",
                "Alí onde outros só ven un camiño, el ve unha oportunidade para facer das súas. Cada recuncho que descubras pode gardar unha pequena sorpresa agardando por ti."
            ),
            "nubeiro" to CharacterCopy(
                "Figura lendaria da tradición galega, señor das nubes e das tormentas. Dise que percorre os ceos provocando treboadas, raios e choiva, envolto en néboas e ventos misteriosos.",
                "Percorre os ceos entre tronos e lóstregos, e sabe que toda viaxe ten o seu destino. Se segues o seu ronsel ata o final, quizais as nubes se abran para ti e revelen un agasallo digno dos deuses do ceo."
            ),
            "urco" to CharacterCopy(
                "Criatura lendaria, representada habitualmente como un enorme can negro con cornos que aparece entre a néboa. Está especialmente ligado ás lendas mariñeiras e á tradición popular de Pontevedra.",
                "Entre sombras e misterios, percibe verdades que escapan aos demais. Cando acertes alí onde outros dubidan, sentirás como a súa maxia ancestral medra contigo."
            )
        )
        AppLanguage.ENGLISH -> mapOf(
            "meiga" to CharacterCopy(
                "A wise woman from Galician tradition, knowledgeable about herbs, remedies and magical lore. Between reality and legend, she is one of the most mysterious figures in our folklore.",
                "Among herbs and enchantments, she knows secrets few can see. Listen to her whispers and you may find the light you need when the answer is hidden in the mist."
            ),
            "trasno" to CharacterCopy(
                "A small, mischievous magical being from Galician mythology, famous for his pranks and antics around the home. He loves to meddle, hide things and surprise anyone he meets.",
                "Where others see only a path, he sees a chance to cause a little mischief. Every corner you discover may hold a small surprise waiting for you."
            ),
            "nubeiro" to CharacterCopy(
                "A legendary figure of Galician tradition, lord of the clouds and storms. He is said to roam the skies bringing thunder, lightning and rain, wrapped in mists and mysterious winds.",
                "He travels the skies between thunder and lightning, and knows that every journey has its destination. If you follow his trail to the end, the clouds may open for you and reveal a gift worthy of the gods of the sky."
            ),
            "urco" to CharacterCopy(
                "A legendary creature, usually depicted as a huge black dog with horns emerging from the mist. He is especially associated with seafaring legends and the folk tradition of Pontevedra.",
                "Among shadows and mysteries, he senses truths that escape others. When you succeed where others hesitate, you will feel his ancestral magic grow within you."
            )
        )
    }

    val selectedCopy = copy[characterId] ?: copy.getValue("meiga")
    return CharacterSelectionStrings(
        eyebrow = common[0],
        title = common[1],
        introduction = common[2],
        selectorLabel = common[3],
        powerLabel = common[4],
        description = selectedCopy.description,
        powerDescription = selectedCopy.power,
        confirmPrefix = common[5],
        changeHint = common[6],
        backLabel = common[7]
    )
}
