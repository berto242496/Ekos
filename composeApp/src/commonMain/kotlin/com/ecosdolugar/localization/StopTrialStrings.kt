package com.ecosdolugar.localization

data class StopTrialStrings(
    val untitledTrial: String,
    val noImageConfigured: String,
    val imageLoadError: String,
    val notCorrectYet: String,
    val hideHint: String,
    val showHint: String,
    val challengeCompleted: String,
    val finishRoute: String,
    val finishChallenge: String,
    val yourAnswer: String,
    val check: String,
    val missingWordLabel: String,
    val tapLettersInOrder: String,
    val clear: String,
    val completionMessages: List<String>
)

fun stopTrialStrings(language: AppLanguage): StopTrialStrings =
    when (language) {
        AppLanguage.SPANISH -> StopTrialStrings(
            untitledTrial = "Prueba",
            noImageConfigured = "Esta prueba no tiene imagen configurada.",
            imageLoadError = "No se pudo cargar la imagen",
            notCorrectYet = "Todavía no es correcto. Puedes volver a intentarlo.",
            hideHint = "Ocultar pista",
            showHint = "Ver pista",
            challengeCompleted = "Prueba completada",
            finishRoute = "Finalizar ruta",
            finishChallenge = "Finalizar prueba",
            yourAnswer = "Tu respuesta",
            check = "Comprobar",
            missingWordLabel = "Palabra que falta",
            tapLettersInOrder = "Toca las letras en orden",
            clear = "Borrar",
            completionMessages = listOf(
                "¡Muy bien! Has descubierto un secreto de esta parada.",
                "Excelente trabajo, explorador. El camino sigue.",
                "Lo has conseguido. Sigue así.",
                "¡Brillante! Los antiguos estarían orgullosos.",
                "Has resuelto el reto. Aún quedan más sorpresas."
            )
        )
        AppLanguage.GALICIAN -> StopTrialStrings(
            untitledTrial = "Proba",
            noImageConfigured = "Esta proba non ten imaxe configurada.",
            imageLoadError = "Non se puido cargar a imaxe",
            notCorrectYet = "Aínda non é correcto. Podes volver intentalo.",
            hideHint = "Ocultar pista",
            showHint = "Ver pista",
            challengeCompleted = "Proba completada",
            finishRoute = "Finalizar ruta",
            finishChallenge = "Finalizar proba",
            yourAnswer = "A túa resposta",
            check = "Comprobar",
            missingWordLabel = "Palabra que falta",
            tapLettersInOrder = "Toca as letras en orde",
            clear = "Borrar",
            completionMessages = listOf(
                "¡Moi ben! Descubriches un segredo desta parada.",
                "Excelente traballo, explorador. O camiño segue.",
                "Conseguícheno. Segue así.",
                "¡Brillante! Os antigos estarían orgullosos.",
                "Resolveches o reto. Aínda quedan máis sorpresas."
            )
        )
        AppLanguage.ENGLISH -> StopTrialStrings(
            untitledTrial = "Challenge",
            noImageConfigured = "This challenge has no image configured.",
            imageLoadError = "The image could not be loaded",
            notCorrectYet = "Not correct yet. You can try again.",
            hideHint = "Hide hint",
            showHint = "Show hint",
            challengeCompleted = "Challenge completed",
            finishRoute = "Finish route",
            finishChallenge = "Finish challenge",
            yourAnswer = "Your answer",
            check = "Check",
            missingWordLabel = "Missing word",
            tapLettersInOrder = "Tap the letters in order",
            clear = "Clear",
            completionMessages = listOf(
                "Well done! You have discovered a secret of this stop.",
                "Excellent work, explorer. The path goes on.",
                "You did it. Keep it up.",
                "Brilliant! The ancients would be proud.",
                "You have solved the challenge. More surprises still await."
            )
        )
    }
