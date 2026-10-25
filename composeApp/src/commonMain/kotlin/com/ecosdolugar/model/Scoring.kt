package com.ecosdolugar.model

/** Reglas de puntuación centralizadas. Todas las funciones son puras. */
object Scoring {
    const val STOP_ARRIVAL_POINTS = 50
    const val TRASNO_STOP_ARRIVAL_POINTS = 70
    const val TRIAL_BASE_POINTS = 100
    const val TRIAL_MIN_POINTS = 20
    const val FAILED_ATTEMPT_PENALTY = 20
    const val HINT_PENALTY = 15
    const val URCO_TRIAL_BONUS = 15
    const val ROUTE_COMPLETION_POINTS = 200
    const val NUBEIRO_ROUTE_COMPLETION_POINTS = 300

    const val CHARACTER_MEIGA = "meiga"
    const val CHARACTER_TRASNO = "trasno"
    const val CHARACTER_NUBEIRO = "nubeiro"
    const val CHARACTER_URCO = "urco"
}

fun stopArrivalPoints(characterId: String): Int =
    if (characterId == Scoring.CHARACTER_TRASNO) Scoring.TRASNO_STOP_ARRIVAL_POINTS else Scoring.STOP_ARRIVAL_POINTS

/** Puntos de una prueba resuelta. Llamar solo cuando la prueba está resuelta. */
fun trialPoints(characterId: String, failedAttempts: Int, hintsUsed: Int): Int {
    val hintPenalty = if (characterId == Scoring.CHARACTER_MEIGA) 0 else hintsUsed * Scoring.HINT_PENALTY
    val penalties = failedAttempts.coerceAtLeast(0) * Scoring.FAILED_ATTEMPT_PENALTY + hintPenalty
    val base = (Scoring.TRIAL_BASE_POINTS - penalties).coerceAtLeast(Scoring.TRIAL_MIN_POINTS)
    return base + if (characterId == Scoring.CHARACTER_URCO) Scoring.URCO_TRIAL_BONUS else 0
}

fun routeCompletionPoints(characterId: String): Int =
    if (characterId == Scoring.CHARACTER_NUBEIRO) Scoring.NUBEIRO_ROUTE_COMPLETION_POINTS else Scoring.ROUTE_COMPLETION_POINTS

/** Puntuación de una ruta: suma solo las paradas de esa ruta. El total global está en [UserProfile.totalScore]. */
fun UserProfile.routeScore(route: RouteItem): Int = route.stops.sumOf { stopScores[it.id] ?: 0 }

/** Guarda la puntuación acumulada de una parada y recalcula el total. */
fun UserProfile.withStopScore(stopId: String, points: Int): UserProfile {
    val scores = stopScores + (stopId to points)
    return copy(stopScores = scores, totalScore = scores.values.sum())
}

