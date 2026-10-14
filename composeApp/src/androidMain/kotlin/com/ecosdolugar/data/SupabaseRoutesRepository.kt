package com.ecosdolugar.data

import com.ecosdolugar.model.Category
import com.ecosdolugar.model.Difficulty
import com.ecosdolugar.model.FieldEnigma
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.OverviewFeature
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.model.RuneGlyph
import com.ecosdolugar.model.StopTrial
import com.ecosdolugar.model.TrialType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

data class SupabaseAuthSession(
    val accessToken: String,
    val userId: String,
    val email: String,
    val displayName: String
)

class SupabaseRoutesRepository(
    private val projectUrl: String,
    private val publishableKey: String
) : RouteDataSource {
    override suspend fun loadRoutes(accessToken: String?): List<RouteItem> = withContext(Dispatchers.IO) {
        requireConfigured()
        val selection = "id,title,status,location,metadata," +
            "route_stops(id,position,latitude,longitude,map_x,map_y,content," +
            "route_challenges(id,type,payload))"
        val response = request(
            method = "GET",
            path = "/rest/v1/routes?select=${encode(selection)}&order=created_at.desc",
            accessToken = accessToken
        )
        val rows = JSONArray(response)
        (0 until rows.length()).map { index -> rows.getJSONObject(index).toRoute() }
    }

    override suspend fun saveRoute(route: RouteItem, status: String, accessToken: String): RouteItem =
        withContext(Dispatchers.IO) {
            requireConfigured()
            require(accessToken.isNotBlank()) { "Inicia sesión para guardar una ruta." }
            val savedRoute = route.withPersistentIds()
            require(status == "publicada" || status == "pendiente" || status == "borrador") {
                "Estado de ruta no válido."
            }
            val persistedStatus = if (status == "publicada") "pendiente" else status
            val payload = savedRoute.toRouteJson(persistedStatus)
            val response = request(
                method = "POST",
                path = "/rest/v1/rpc/save_route",
                body = JSONObject().put("route_data", payload).toString(),
                accessToken = accessToken,
                contentType = "application/json"
            )
            if (response.isNotBlank() && response != JSONObject.quote(savedRoute.id)) {
                throw IllegalStateException("Supabase devolvió un identificador inesperado al guardar la ruta.")
            }
            savedRoute.copy(status = persistedStatus)
        }

    override suspend fun signOut(accessToken: String) = withContext(Dispatchers.IO) {
        if (accessToken.isNotBlank()) {
            request(
                method = "POST",
                path = "/auth/v1/logout",
                accessToken = accessToken
            )
        }
    }

    override suspend fun saveChallengeProgress(
        challengeId: String,
        attempts: Int,
        score: Int,
        completed: Boolean,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        requireConfigured()
        require(accessToken.isNotBlank()) { "Inicia sesión para sincronizar el progreso del reto." }
        require(attempts >= 0) { "El número de intentos no puede ser negativo." }
        require(score in 0..100) { "La puntuación debe estar entre 0 y 100." }
        val progress = JSONObject()
            .put("challenge_id", challengeId)
            .put("status", if (completed) "completado" else "iniciado")
            .put("attempts", attempts)
            .put("score", score)
            .put(
                "completed_at",
                if (completed) {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                        .apply { timeZone = TimeZone.getTimeZone("UTC") }
                        .format(Date())
                } else {
                    JSONObject.NULL
                }
            )
        request(
            method = "POST",
            path = "/rest/v1/user_challenge_progress?on_conflict=user_id,challenge_id",
            body = JSONArray().put(progress).toString(),
            accessToken = accessToken,
            contentType = "application/json",
            prefer = "resolution=merge-duplicates,return=minimal"
        )
        Unit
    }

    override suspend fun loadCompletedChallenges(accessToken: String): Set<String> =
        withContext(Dispatchers.IO) {
            requireConfigured()
            require(accessToken.isNotBlank()) { "Inicia sesión para cargar el progreso de retos." }
            val response = request(
                method = "GET",
                path = "/rest/v1/user_challenge_progress?select=challenge_id&status=eq.completado",
                accessToken = accessToken
            )
            val rows = JSONArray(response)
            (0 until rows.length()).mapTo(mutableSetOf()) { index ->
                rows.getJSONObject(index).getString("challenge_id")
            }
        }

    suspend fun exchangeGoogleIdToken(idToken: String): SupabaseAuthSession =
        withContext(Dispatchers.IO) {
            requireConfigured()
            require(idToken.isNotBlank()) { "Google no proporcionó un token de identidad." }
            val response = request(
                method = "POST",
                path = "/auth/v1/token?grant_type=id_token",
                body = JSONObject()
                    .put("provider", "google")
                    .put("id_token", idToken)
                    .toString(),
                contentType = "application/json"
            )
            val json = JSONObject(response)
            val user = json.getJSONObject("user")
            val metadata = user.optJSONObject("user_metadata") ?: JSONObject()
            SupabaseAuthSession(
                accessToken = json.getString("access_token"),
                userId = user.getString("id"),
                email = user.optString("email"),
                displayName = metadata.optString(
                    "full_name",
                    metadata.optString("name", user.optString("email"))
                )
            )
        }

    private fun requireConfigured() {
        require(projectUrl.startsWith("https://") && publishableKey.isNotBlank()) {
            "Configura SUPABASE_URL y SUPABASE_PUBLISHABLE_KEY en .env.local para Android."
        }
    }

    private fun request(
        method: String,
        path: String,
        body: String? = null,
        accessToken: String? = null,
        contentType: String? = null,
        prefer: String? = null
    ): String {
        val connection = (URL(projectUrl.trimEnd('/') + path).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 30_000
            setRequestProperty("apikey", publishableKey)
            setRequestProperty("Authorization", "Bearer ${accessToken?.takeIf { it.isNotBlank() } ?: publishableKey}")
            contentType?.let { setRequestProperty("Content-Type", it) }
            prefer?.let { setRequestProperty("Prefer", it) }
            if (body != null) doOutput = true
        }
        try {
            if (body != null) {
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(body) }
            }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (status !in 200..299) {
                val errorBody = runCatching { JSONObject(response) }.getOrNull()
                val detail = errorBody?.optString("message")?.ifBlank {
                    errorBody.optString("msg", response)
                } ?: response
                val code = errorBody?.optString("code").orEmpty()
                if (status == 409 && code == "23505" && detail.contains("routes_pkey")) {
                    throw IllegalStateException(
                        "Supabase aún tiene la función save_route antigua, que intenta insertar " +
                            "el borrador como ruta nueva. Ejecuta la migración " +
                            "supabase/migrations/20261013235500_upsert_owned_routes.sql en el SQL Editor."
                    )
                }
                throw IllegalStateException("Supabase respondió HTTP $status: $detail")
            }
            return response
        } finally {
            connection.disconnect()
        }
    }
}

private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")

private fun RouteItem.withPersistentIds(): RouteItem {
    val stableRouteId = id.ifBlank { "route-${System.currentTimeMillis()}" }
    val stableStops = stops.mapIndexed { index, stop ->
        stop.copy(
            id = stop.id.ifBlank { "$stableRouteId-stop-${index + 1}" },
            order = index + 1,
            challengeId = if (stop.trial != null) {
                stop.challengeId ?: UUID.randomUUID().toString()
            } else {
                null
            }
        )
    }
    return copy(id = stableRouteId, stopsCount = stableStops.size, stops = stableStops)
}

private fun JSONObject.toRoute(): RouteItem {
    val metadata = getJSONObject("metadata")
    val guideJson = metadata.optJSONObject("guide") ?: JSONObject()
    val features = metadata.optJSONArray("overviewFeatures") ?: JSONArray()
    val stopRows = optJSONArray("route_stops") ?: JSONArray()
    val stops = (0 until stopRows.length())
        .map { stopRows.getJSONObject(it) }
        .sortedBy { it.optInt("position") }
        .map { row ->
            val content = row.optJSONObject("content") ?: JSONObject()
            val trialRows = row.optJSONArray("route_challenges")
            val challenge = if (trialRows != null && trialRows.length() > 0) {
                trialRows.optJSONObject(0)
            } else {
                row.optJSONObject("route_challenges")
            }
            content.toStop(
                id = row.getString("id"),
                order = row.getInt("position"),
                latitude = row.getDouble("latitude"),
                longitude = row.getDouble("longitude"),
                mapX = row.getDouble("map_x").toFloat(),
                mapY = row.getDouble("map_y").toFloat(),
                challenge = challenge
            )
        }
    return RouteItem(
        id = getString("id"),
        title = getString("title"),
        subtitle = metadata.optString("subtitle"),
        description = metadata.optString("description"),
        location = optString("location"),
        region = metadata.optString("region"),
        imageUrl = metadata.optString("image", metadata.optString("imageUrl")),
        isFeatured = metadata.optBoolean("isFeatured"),
        stopsCount = stops.size,
        duration = metadata.optString("duration"),
        distance = metadata.optString("distance"),
        difficulty = metadata.optString("difficulty").toDifficulty(),
        categories = metadata.optJSONArray("categories").toCategories(),
        audioTag = metadata.optString("audioTag"),
        tomoRef = metadata.optString("tomoRef"),
        price = metadata.optString("price", "Gratis"),
        isFree = metadata.optBoolean("isFree", true),
        guide = guideJson.toGuide(),
        overviewFeatures = features.toFeatures(),
        stops = stops,
        status = optString("status")
    )
}

private fun JSONObject.toStop(
    id: String,
    order: Int,
    latitude: Double,
    longitude: Double,
    mapX: Float,
    mapY: Float,
    challenge: JSONObject?
): RouteStop {
    val enigmaJson = optJSONObject("enigma") ?: JSONObject()
    val payload = challenge?.optJSONObject("payload")
    return RouteStop(
        id = id,
        order = order,
        title = optString("title"),
        subtitle = optString("subtitle"),
        locationName = optString("locationName"),
        story = optString("story"),
        historicalNote = optString("historicalNote"),
        enigma = FieldEnigma(
            question = enigmaJson.optString("question"),
            hint = enigmaJson.optString("hint"),
            answer = enigmaJson.optString("answer")
        ),
        trial = payload?.toTrial(),
        challengeId = challenge?.optString("id")?.takeIf { !it.isNullOrBlank() },
        audioDuration = optString("audioDuration"),
        audioNarration = optString("audioNarration").takeIf { it.isNotBlank() },
        latitude = latitude,
        longitude = longitude,
        mapPositionX = mapX,
        mapPositionY = mapY
    )
}

private fun JSONObject.toTrial(): StopTrial {
    val runesJson = optJSONArray("runes") ?: JSONArray()
    val cluesJson = optJSONObject("characterClues") ?: JSONObject()
    val clues = buildMap {
        val keys = cluesJson.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            put(key, cluesJson.optString(key))
        }
    }
    return StopTrial(
        type = optString("type").toTrialType(),
        title = optString("title"),
        loreContext = optString("loreContext"),
        question = optString("question"),
        options = optJSONArray("options").toStringList(),
        correctOptionIndex = optInt("correctOptionIndex"),
        runes = (0 until runesJson.length()).map { index ->
            val rune = runesJson.getJSONObject(index)
            RuneGlyph(
                id = rune.optString("id"),
                glyph = rune.optString("glyph"),
                name = rune.optString("name"),
                desc = rune.optString("desc")
            )
        },
        correctRuneOrder = optJSONArray("correctRuneOrder").toStringList(),
        cipherWord = optString("cipherWord"),
        scrambledLetters = optJSONArray("scrambledLetters").toStringList(),
        cipherClue = optString("cipherClue"),
        generalHint = optString("generalHint"),
        characterClues = clues,
        solutionExplanation = optString("solutionExplanation"),
        magicalReward = optString("magicalReward")
    )
}

private fun JSONObject.toGuide() = GuideAuthor(
    id = optString("id").ifBlank { "guia_oficial" },
    name = optString("name").ifBlank { "Guía local" },
    title = optString("title"),
    bio = optString("bio"),
    rating = optDouble("rating", 4.9),
    reviewsCount = optInt("reviewsCount", 0),
    location = optString("location"),
    verified = optBoolean("verified"),
    routesCount = optInt("routesCount", 0)
)

private fun JSONArray?.toCategories(): List<Category> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { index ->
        when (optString(index).lowercase()) {
            "historia" -> Category.HISTORIA
            "leyendas" -> Category.LEYENDAS
            "familias" -> Category.FAMILIAS
            "patrimonio" -> Category.PATRIMONIO
            "naturaleza" -> Category.NATURALEZA
            else -> null
        }
    }
}

private fun JSONArray?.toFeatures(): List<OverviewFeature> {
    if (this == null) return emptyList()
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        OverviewFeature(
            iconName = item.optString("icon", item.optString("iconName")),
            title = item.optString("title"),
            description = item.optString("description"),
            accentTag = item.optString("accentColor", item.optString("accentTag"))
        )
    }
}

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return (0 until length()).map { optString(it) }
}

private fun String.toDifficulty(): Difficulty = when (lowercase()) {
    "media", "medium" -> Difficulty.MEDIA
    "exigente", "dificil", "difícil", "hard" -> Difficulty.EXIGENTE
    else -> Difficulty.FACIL
}

private fun String.toTrialType(): TrialType = when (lowercase()) {
    "puzle_runico" -> TrialType.PUZLE_RUNICO
    "cifrado_magico" -> TrialType.CIFRADO_MAGICO
    "alineacion_simbolos" -> TrialType.ALINEACION_SIMBOLOS
    else -> TrialType.ACERTIJO
}

private fun RouteItem.toRouteJson(status: String): JSONObject = JSONObject()
    .put("id", id)
    .put("title", title)
    .put("subtitle", subtitle)
    .put("description", description)
    .put("location", location)
    .put("region", region)
    .put("image", imageUrl)
    .put("isFeatured", isFeatured)
    .put("stopsCount", stops.size)
    .put("duration", duration)
    .put("distance", distance)
    .put("difficulty", difficulty.label)
    .put("categories", JSONArray(categories.map { it.displayName.lowercase() }))
    .put("audioTag", audioTag)
    .put("tomoRef", tomoRef)
    .put("price", price)
    .put("isFree", isFree)
    .put("status", status)
    .put("guide", guide.toJson())
    .put("overviewFeatures", JSONArray(overviewFeatures.map { it.toJson() }))
    .put("stops", JSONArray(stops.map { it.toJson() }))

private fun GuideAuthor.toJson() = JSONObject()
    .put("id", id)
    .put("name", name)
    .put("title", title)
    .put("bio", bio)
    .put("rating", rating)
    .put("reviewsCount", reviewsCount)
    .put("location", location)
    .put("verified", verified)
    .put("routesCount", routesCount)

private fun OverviewFeature.toJson() = JSONObject()
    .put("icon", iconName)
    .put("title", title)
    .put("description", description)
    .put("accentColor", accentTag)

private fun RouteStop.toJson(): JSONObject {
    val json = JSONObject()
        .put("id", id)
        .put("order", order)
        .put("title", title)
        .put("subtitle", subtitle)
        .put("locationName", locationName)
        .put("story", story)
        .put("historicalNote", historicalNote)
        .put(
            "enigma",
            JSONObject()
                .put("question", enigma.question)
                .put("hint", enigma.hint)
                .put("answer", enigma.answer)
        )
        .put("audioDuration", audioDuration)
        .put("audioNarration", audioNarration ?: JSONObject.NULL)
        .put(
            "coordinates",
            JSONObject()
                .put("lat", latitude)
                .put("lng", longitude)
                .put("x", mapPositionX)
                .put("y", mapPositionY)
        )
    if (trial != null) {
        json.put("trial", trial.toJson())
        json.put("challengeId", challengeId)
    }
    return json
}

private fun StopTrial.toJson() = JSONObject()
    .put("type", type.name.lowercase())
    .put("title", title)
    .put("loreContext", loreContext)
    .put("question", question)
    .put("options", JSONArray(options))
    .put("correctOptionIndex", correctOptionIndex)
    .put(
        "runes",
        JSONArray(runes.map { rune ->
            JSONObject()
                .put("id", rune.id)
                .put("glyph", rune.glyph)
                .put("name", rune.name)
                .put("desc", rune.desc)
        })
    )
    .put("correctRuneOrder", JSONArray(correctRuneOrder))
    .put("cipherWord", cipherWord)
    .put("scrambledLetters", JSONArray(scrambledLetters))
    .put("cipherClue", cipherClue)
    .put("generalHint", generalHint)
    .put("characterClues", JSONObject(characterClues))
    .put("solutionExplanation", solutionExplanation)
    .put("magicalReward", magicalReward)
