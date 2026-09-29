package com.ecosdolugar.data

import com.ecosdolugar.model.*

object RoutesRepository {
    const val LOGO_URL = "https://lh3.googleusercontent.com/aida/AEtjO1Ui7VA-PCAed6sDnmiNOlEeYWhzlK6Ey1rsYOYc3Hdo8n0lWp5tvYyQW0U5-QIPQ6nFjBhnBS1JRIbfTw8746rxPOXoegLBhHHP_aLR179pbqBrBtowtdLQgby4rIt6-YK-OMysksGI762fHTG79PLzV1bcMKQ_z7XRBAxZaaJ37tGRHetofB84RIJoqqnnQpeh2VQypTn_GRM9Uq03NJYVpyg6AjdqHZLeS6GG_GxJ6_pKxXUFSPBGX94"
    const val HERO_FOREST_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuDJsD_GV4LqUWigjOfMKmrw3LLdS0cAoJ19hNaLjgc8jYswZ7LWbrYTeMZtv9kVHadZLF8ZqemZrtu0g28o3U2fNN7Hj3mkxsj8kfsaUOa9FZPIMkLcHWuKiwX0EnXb1QDItbRYe4-1BygCfvvWAgPBi062_ThnaTbOqxv7UF1BKJVFSwlN3NAHaHouKIRXzHEvVkTr95gxfZpl-SNzIGnfqRjiI4MFpnNQJZRPLwm9vBI4QYzX28GDfw"
    const val HERO_CONTRABANDO_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuArehOjJRSLoB92TmGgRWPYTLoDkjiIljkqHRE5yvpOdwYKG1QJ50nLeKS8MKqaUIWEYIXGVfdccqiJ58yH1cVkDrEwr2NzR5EZCOODJt-hvxYO8ECqAd1NZqrCu7_aMFGOcz5DCZndbixfyXRoBoWnV9RjzOVUE4Q2lvEBywD2colZlzhIaXiZYp99f3qPUuGYDl2DcWN3sJI1umanW5jB_ZjNLiueMyhQy5G88XRXM_6hohI5VgVS2Q"
    const val HERO_COAST_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuDZPX9XqxSZK7KajA3QRMtnX8sS3XOmv8_i6qb2ijQnObaChy5HYVUzmjM0sIrT4s7xWg20l1bRxnPaUyatSCqM8w-j9Uppw-Zw0yWF2zWoUCauv_QjTFVaONuhkVAdFgnfnD2ItlbStpbsh8yy3prFXhagnf2NSF0EvOS0X3JrrdAyxiGDBHuxDOkzsbzHi7S5f2gbKfjgL5BTvs0GGjUyZBPTX47X9uAj6DxBcHYjGZZjuVbx0eRG4Q"

    val routes = listOf(
        RouteItem(
            id = "contrabando",
            title = "Ruta del Contrabando",
            subtitle = "Cruce fluvial del Puente Internacional",
            description = "Cruza historias de frontera, caminos ocultos y memoria local junto al Miño. Un itinerario por el rumor del agua y los pactos velados entre orillas.",
            location = "Tui / Valença",
            region = "TUI (GALICIA) · VALENÇA DO MINHO (PORTUGAL)",
            imageUrl = HERO_CONTRABANDO_URL,
            isFeatured = true,
            stopsCount = 8,
            duration = "1h 30 min",
            distance = "3,5 km",
            difficulty = Difficulty.FACIL,
            categories = listOf(Category.HISTORIA, Category.LEYENDAS),
            audioTag = "Relato inmersivo",
            tomoRef = "Arqueoloxía do Silencio · Tomo IV",
            overviewFeatures = listOf(
                OverviewFeature("mic", "Historias ocultas", "Testimonios reales de contrabandistas de café y medicinas durante las noches de niebla en el Miño.", "secondary"),
                OverviewFeature("psychology", "Retos interactivos", "Audio-guías geolocalizadas y enigmas visuales sobre las piedras milenarias de la fortaleza.", "primary"),
                OverviewFeature("search", "Pistas del estraperlo", "Señales sutiles grabadas en los muros fluviales para identificar antiguos escondites y pasos seguros.", "tertiary"),
                OverviewFeature("fort", "Patrimonio local", "Paso por la Fortaleza abaluartada de Valença y el mítico puente internacional diseñado por discípulos de Eiffel.", "primary")
            ),
            stops = listOf(
                RouteStop(
                    id = "stop-c1",
                    order = 1,
                    title = "Porta da Pía (Tui)",
                    subtitle = "El umbral de granito y las sombras de medianoche",
                    locationName = "Casco Histórico de Tui",
                    story = "Aquí comenzaba la bajada sigilosa. Los portadores envolvían los zuecos con arpillera húmeda para amortiguar el sonido sobre los cantos rodados mientras los carabineros hacían el cambio de guardia.",
                    historicalNote = "Durante los años 40 y 50, el café en grano y la penicilina cruzaban de sur a norte, mientras que el wolframio y telas viajaban en sentido inverso.",
                    enigma = FieldEnigma(
                        question = "¿Qué animal esculpido en la piedra servía de referencia secreta para los estraperlistas en este arco?",
                        hint = "Busca en la jamba izquierda a media altura, cerca del desgaste de los carros.",
                        answer = "Un búho velado por el musgo (símbolo de la noche silenciosa)."
                    ),
                    audioDuration = "2:45",
                    latitude = 42.0461,
                    longitude = -8.6445,
                    mapPositionX = 28f,
                    mapPositionY = 38f
                ),
                RouteStop(
                    id = "stop-c2",
                    order = 2,
                    title = "Paseo Fluvial y Varadero de Barcas",
                    subtitle = "El rumor del Miño y los bateleros cómplices",
                    locationName = "Ribeira de Tui",
                    story = "Los remeros conocían cada remolino y bajío de arena. Una luz de carburo titilando tres veces desde la orilla portuguesa indicaba que la patrulla de la Guarda Fiscal estaba lejos.",
                    historicalNote = "El paso en barca de fondo plano se hacía en menos de cuatro minutos bajo la espesa niebla de invierno.",
                    enigma = FieldEnigma(
                        question = "¿Qué nombre recibían popularmente los sacos impermeables de tela embreada?",
                        hint = "Aludía al lomo de las cabras monteses.",
                        answer = "Pellizas de estraperlo."
                    ),
                    audioDuration = "3:10",
                    latitude = 42.0435,
                    longitude = -8.6432,
                    mapPositionX = 38f,
                    mapPositionY = 44f
                ),
                RouteStop(
                    id = "stop-c3",
                    order = 3,
                    title = "Puente Internacional Eiffel",
                    subtitle = "La celosía de hierro sobre el abismo fronterizo",
                    locationName = "Frontera Miño Tui-Valença",
                    story = "Inaugurado en 1886, este coloso de hierro forjado une dos naciones. De noche, los paquetes más pequeños pasaban suspendidos por cuerdas desde los pilares centrales hacia barcazas que esperaban en la penumbra.",
                    historicalNote = "Fue diseñado por la compañía de Gustave Eiffel, combinando paso ferroviario en la parte superior y carretero abajo.",
                    enigma = FieldEnigma(
                        question = "¿Cuántos remaches unen la viga maestra en el tramo central de la línea divisoria?",
                        hint = "Cuenta la hilera triple que cruza la placa conmemorativa de 1886.",
                        answer = "Doce remaches de forja doble."
                    ),
                    audioDuration = "3:30",
                    latitude = 42.0390,
                    longitude = -8.6420,
                    mapPositionX = 50f,
                    mapPositionY = 50f
                ),
                RouteStop(
                    id = "stop-c4",
                    order = 4,
                    title = "Porta do Meio y Baluarte de Valença",
                    subtitle = "Las murallas abaluartadas y el contrabando de café",
                    locationName = "Fortaleza de Valença do Minho",
                    story = "Al franquear los fosos de la fortaleza, el aroma a café torrefacto portugués impregnaba las callejuelas. Comerciantes y familias compartían pan de maíz y toallas en trueques pacíficos de frontera.",
                    historicalNote = "La fortaleza de Valença es una de las mayores joyas de arquitectura militar barroca de Europa, con más de 5 km de muralla en estrella.",
                    enigma = FieldEnigma(
                        question = "¿Hacia qué constelación apunta la tronera del baluarte occidental en el solsticio de invierno?",
                        hint = "Guiaba a los pastores que bajaban de la Serra d'Arga.",
                        answer = "La constelación de Orión (el Cazador del Miño)."
                    ),
                    audioDuration = "2:55",
                    latitude = 42.0315,
                    longitude = -8.6440,
                    mapPositionX = 62f,
                    mapPositionY = 58f
                )
            )
        ),
        RouteItem(
            id = "leyendas",
            title = "Leyendas del Miño",
            subtitle = "Mitos de meigas, mouros y ritos ancestrales",
            description = "Mitos de meigas, mouros y ritos ancestrales en los pasos del bosque. Un recorrido de sombra, piedra tallada y manantiales sagrados.",
            location = "Frontera del Miño",
            region = "FRONTERA DEL MIÑO · BOSQUES DE COVELO Y MONÇÃO",
            imageUrl = HERO_FOREST_URL,
            stopsCount = 6,
            duration = "2h",
            distance = "4,8 km",
            difficulty = Difficulty.MEDIA,
            categories = listOf(Category.LEYENDAS, Category.PATRIMONIO, Category.NATURALEZA),
            audioTag = "Folclore vivo",
            tomoRef = "Mitoloxía da Raia · Caderno III",
            overviewFeatures = listOf(
                OverviewFeature("nature", "Mitos vivos", "Relatos de mouros guardianes de tesoros bajo túmulos megalíticos ocultos.", "secondary"),
                OverviewFeature("water", "Fuentes y ritos", "Aguas medicinales donde las ancianas de la frontera acudían a bendecir el lino.", "primary")
            ),
            stops = emptyList()
        ),
        RouteItem(
            id = "marinera",
            title = "Memoria Marinera",
            subtitle = "El salitre de antiguos puertos y naufragios",
            description = "El salitre de los antiguos puertos balleneros y naufragios. Historias de mujeres de costa, redes de esparto y vigías en los acantilados atlánticos.",
            location = "Costa Atlántica",
            region = "BAIXO MIÑO · A GUARDA & CAMINHA",
            imageUrl = HERO_COAST_URL,
            stopsCount = 5,
            duration = "1h 15 min",
            distance = "2,9 km",
            difficulty = Difficulty.FACIL,
            categories = listOf(Category.HISTORIA, Category.FAMILIAS, Category.PATRIMONIO),
            audioTag = "Apta para todas las edades",
            tomoRef = "Crónicas do Mar de Fora · Fascículo I",
            overviewFeatures = listOf(
                OverviewFeature("waves", "Costa de naufragios", "Las campanas de niebla y los relatos de rescate de marineros en la desembocadura.", "secondary")
            ),
            stops = emptyList()
        )
    )
}
