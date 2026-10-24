package com.ecosdolugar.localization

data class AppLanguageStrings(
    val brandName: String,
    val brandSubtitle: String,
    val headline: String,
    val description: String,
    val availableRoutesLabel: String,
    val activeGuidesLabel: String,
    val connecting: String,
    val signInTitle: String,
    val signInSubtitle: String,
    val guidePrompt: String,
    val guideCallToAction: String,
    val guideConnecting: String,
    val displayNameTitle: String,
    val displayNameDescription: String,
    val displayNameLabel: String,
    val displayNameContinue: String,
    val displayNameRequired: String,
    val locationLine: String,
    val languageTitle: String
)

fun appLanguageStrings(language: AppLanguage): AppLanguageStrings =
    when (language) {
        AppLanguage.SPANISH -> AppLanguageStrings(
            brandName = "ECOS DO LUGAR",
            brandSubtitle = "Explora lugares. Descubre historias.",
            headline = "Cada lugar esconde una historia.",
            description = "Recorre rutas únicas creadas por guías locales, descubre secretos y supera desafíos a tu propio ritmo.",
            availableRoutesLabel = "RUTAS DISPONIBLES",
            activeGuidesLabel = "GUÍAS ACTIVOS",
            connecting = "Conectando con Google...",
            signInTitle = "Empezar a explorar.",
            signInSubtitle = "Esencial para despertar tu don mágico y resolver las pruebas",
            guidePrompt = "¿Quieres crear tus propias rutas?",
            guideCallToAction = "Acceder como guía.",
            guideConnecting = "Conectando con Google...",
            displayNameTitle = "¿Qué nombre quieres mostrar?",
            displayNameDescription = "Este nombre aparecerá en tu perfil y en las rutas que publiques.",
            displayNameLabel = "Nombre para mostrar",
            displayNameContinue = "Continuar",
            displayNameRequired = "Escribe un nombre de hasta 60 caracteres.",
            locationLine = "Historias que se recorren.",
            languageTitle = "Idioma"
        )
        AppLanguage.GALICIAN -> AppLanguageStrings(
            brandName = "ECOS DO LUGAR",
            brandSubtitle = "Explora lugares. Descobre historias.",
            headline = "Cada lugar esconde unha historia.",
            description = "Percorre rutas únicas creadas por guías locais, descobre segredos e supera desafíos ao teu propio ritmo.",
            availableRoutesLabel = "RUTAS DISPOÑIBLES",
            activeGuidesLabel = "GUÍAS ACTIVOS",
            connecting = "Conectando con Google...",
            signInTitle = "Comezar a explorar.",
            signInSubtitle = "Esencial para espertar o teu don máxico e resolver as probas",
            guidePrompt = "Queres crear as túas propias rutas?",
            guideCallToAction = "Acceder como guía.",
            guideConnecting = "Conectando con Google...",
            displayNameTitle = "Que nome queres amosar?",
            displayNameDescription = "Este nome aparecerá no teu perfil e nas rutas que publiques.",
            displayNameLabel = "Nome para amosar",
            displayNameContinue = "Continuar",
            displayNameRequired = "Escribe un nome de ata 60 caracteres.",
            locationLine = "Historias que se percorren.",
            languageTitle = "Idioma"
        )
        AppLanguage.ENGLISH -> AppLanguageStrings(
            brandName = "ECOS DO LUGAR",
            brandSubtitle = "Explore places. Discover stories.",
            headline = "Every place hides a story.",
            description = "Walk unique routes created by local guides, discover secrets and overcome challenges at your own pace.",
            availableRoutesLabel = "AVAILABLE ROUTES",
            activeGuidesLabel = "ACTIVE GUIDES",
            connecting = "Connecting to Google...",
            signInTitle = "Start exploring.",
            signInSubtitle = "Required to awaken your magical gift and solve the challenges",
            guidePrompt = "Want to create your own routes?",
            guideCallToAction = "Access as a guide.",
            guideConnecting = "Connecting to Google...",
            displayNameTitle = "What name would you like to show?",
            displayNameDescription = "This name will appear on your profile and on the routes you publish.",
            displayNameLabel = "Display name",
            displayNameContinue = "Continue",
            displayNameRequired = "Enter a name up to 60 characters long.",
            locationLine = "Stories to explore.",
            languageTitle = "Language"
        )
    }
