package com.ecosdolugar.localization

import com.ecosdolugar.model.ReportReason

data class SafetyStrings(
    val report: String,
    val reportRoute: String,
    val reportGuide: String,
    val reportReview: String,
    val reportDialogTitle: String,
    val reportDialogBody: String,
    val reportSent: String,
    val reportFailed: String,
    val signInToReport: String,
    val reasonInappropriate: String,
    val reasonHarassment: String,
    val reasonSpam: String,
    val reasonInaccurate: String,
    val reasonOther: String,
    val send: String,
    val cancel: String,
    val blockGuide: String,
    val blockDialogTitle: (name: String) -> String,
    val blockDialogBody: String,
    val blockConfirm: String,
    val userBlocked: String,
    val blockFailed: String,
    val moreOptions: String,
    val deleteAccount: String,
    val deleteAccountDialogTitle: String,
    val deleteAccountDialogBody: String,
    val deleteAccountConfirm: String,
    val deleteAccountFailed: String,
    val accountDeleted: String,
    val privacyPolicy: String,
    val termsOfService: String
) {
    fun reasonLabel(reason: ReportReason): String = when (reason) {
        ReportReason.INAPPROPRIATE -> reasonInappropriate
        ReportReason.HARASSMENT -> reasonHarassment
        ReportReason.SPAM -> reasonSpam
        ReportReason.INACCURATE -> reasonInaccurate
        ReportReason.OTHER -> reasonOther
    }
}

fun safetyStrings(language: AppLanguage): SafetyStrings =
    when (language) {
        AppLanguage.SPANISH -> SafetyStrings(
            report = "Reportar",
            reportRoute = "Reportar ruta",
            reportGuide = "Reportar guía",
            reportReview = "Reportar reseña",
            reportDialogTitle = "¿Por qué quieres reportarlo?",
            reportDialogBody = "Revisaremos el contenido y actuaremos si incumple las normas.",
            reportSent = "Gracias, hemos recibido tu reporte.",
            reportFailed = "No se pudo enviar el reporte.",
            signInToReport = "Inicia sesión para reportar contenido.",
            reasonInappropriate = "Contenido inapropiado u ofensivo",
            reasonHarassment = "Acoso o discurso de odio",
            reasonSpam = "Spam o publicidad",
            reasonInaccurate = "Información falsa o engañosa",
            reasonOther = "Otro motivo",
            send = "Enviar",
            cancel = "Cancelar",
            blockGuide = "Bloquear guía",
            blockDialogTitle = { name -> "¿Bloquear a $name?" },
            blockDialogBody = "Dejarás de ver sus rutas y reseñas.",
            blockConfirm = "Bloquear",
            userBlocked = "Usuario bloqueado.",
            blockFailed = "No se pudo bloquear al usuario.",
            moreOptions = "Más opciones",
            deleteAccount = "Eliminar mi cuenta",
            deleteAccountDialogTitle = "¿Eliminar tu cuenta?",
            deleteAccountDialogBody = "Se borrarán de forma permanente tu perfil, tu progreso, tus reseñas, " +
                "tus rutas y los archivos que hayas subido. Esta acción no se puede deshacer.",
            deleteAccountConfirm = "Eliminar definitivamente",
            deleteAccountFailed = "No se pudo eliminar la cuenta. Inténtalo de nuevo.",
            accountDeleted = "Tu cuenta ha sido eliminada.",
            privacyPolicy = "Política de privacidad",
            termsOfService = "Términos de uso"
        )
        AppLanguage.GALICIAN -> SafetyStrings(
            report = "Denunciar",
            reportRoute = "Denunciar ruta",
            reportGuide = "Denunciar guía",
            reportReview = "Denunciar avaliación",
            reportDialogTitle = "Por que queres denuncialo?",
            reportDialogBody = "Revisaremos o contido e actuaremos se incumpre as normas.",
            reportSent = "Grazas, recibimos a túa denuncia.",
            reportFailed = "Non se puido enviar a denuncia.",
            signInToReport = "Inicia sesión para denunciar contido.",
            reasonInappropriate = "Contido inapropiado ou ofensivo",
            reasonHarassment = "Acoso ou discurso de odio",
            reasonSpam = "Spam ou publicidade",
            reasonInaccurate = "Información falsa ou enganosa",
            reasonOther = "Outro motivo",
            send = "Enviar",
            cancel = "Cancelar",
            blockGuide = "Bloquear guía",
            blockDialogTitle = { name -> "Bloquear a $name?" },
            blockDialogBody = "Deixarás de ver as súas rutas e avaliacións.",
            blockConfirm = "Bloquear",
            userBlocked = "Usuario bloqueado.",
            blockFailed = "Non se puido bloquear o usuario.",
            moreOptions = "Máis opcións",
            deleteAccount = "Eliminar a miña conta",
            deleteAccountDialogTitle = "Eliminar a túa conta?",
            deleteAccountDialogBody = "Borraranse de forma permanente o teu perfil, o teu progreso, as túas " +
                "avaliacións, as túas rutas e os ficheiros que subises. Esta acción non se pode desfacer.",
            deleteAccountConfirm = "Eliminar definitivamente",
            deleteAccountFailed = "Non se puido eliminar a conta. Téntao de novo.",
            accountDeleted = "A túa conta foi eliminada.",
            privacyPolicy = "Política de privacidade",
            termsOfService = "Termos de uso"
        )
        AppLanguage.ENGLISH -> SafetyStrings(
            report = "Report",
            reportRoute = "Report route",
            reportGuide = "Report guide",
            reportReview = "Report review",
            reportDialogTitle = "Why are you reporting this?",
            reportDialogBody = "We will review the content and act if it breaks the rules.",
            reportSent = "Thanks, we received your report.",
            reportFailed = "Could not send the report.",
            signInToReport = "Sign in to report content.",
            reasonInappropriate = "Inappropriate or offensive content",
            reasonHarassment = "Harassment or hate speech",
            reasonSpam = "Spam or advertising",
            reasonInaccurate = "False or misleading information",
            reasonOther = "Other",
            send = "Send",
            cancel = "Cancel",
            blockGuide = "Block guide",
            blockDialogTitle = { name -> "Block $name?" },
            blockDialogBody = "You will no longer see their routes and reviews.",
            blockConfirm = "Block",
            userBlocked = "User blocked.",
            blockFailed = "Could not block the user.",
            moreOptions = "More options",
            deleteAccount = "Delete my account",
            deleteAccountDialogTitle = "Delete your account?",
            deleteAccountDialogBody = "Your profile, progress, reviews, routes and uploaded files will be " +
                "permanently deleted. This cannot be undone.",
            deleteAccountConfirm = "Delete permanently",
            deleteAccountFailed = "Could not delete the account. Please try again.",
            accountDeleted = "Your account has been deleted.",
            privacyPolicy = "Privacy policy",
            termsOfService = "Terms of use"
        )
    }
