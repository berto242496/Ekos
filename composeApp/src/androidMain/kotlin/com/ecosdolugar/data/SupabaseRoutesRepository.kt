package com.ecosdolugar.data

import com.ecosdolugar.model.Category
import com.ecosdolugar.model.Difficulty
import com.ecosdolugar.model.FieldEnigma
import com.ecosdolugar.model.GuideAuthor
import com.ecosdolugar.model.GuideProfileInfo
import com.ecosdolugar.model.NarrationVoice
import com.ecosdolugar.model.OverviewFeature
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteReview
import com.ecosdolugar.model.RouteStop
import com.ecosdolugar.model.RuneGlyph
import com.ecosdolugar.model.StopAudioMode
import com.ecosdolugar.model.StopTrial
import com.ecosdolugar.model.TrialType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

data class SupabaseAuthSession(
    val accessToken: String,
    val refreshToken: String,
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

    override suspend fun loadRouteReviews(accessToken: String?): List<RouteReview> = withContext(Dispatchers.IO) {
        requireConfigured()
        val selection = "route_id,guide_id,user_id,route_rating,guide_rating,comment,created_at"
        val response = request(
            method = "GET",
            path = "/rest/v1/route_reviews?select=${encode(selection)}&order=created_at.desc",
            accessToken = accessToken
        )
        val rows = JSONArray(response)
        (0 until rows.length()).map { index ->
            val row = rows.getJSONObject(index)
            RouteReview(
                routeId = row.getString("route_id"),
                guideId = row.getString("guide_id"),
                userId = row.getString("user_id"),
                routeRating = row.optInt("route_rating", 0),
                guideRating = row.optInt("guide_rating", 0),
                comment = row.nullableString("comment"),
                createdAt = row.nullableString("created_at")
            )
        }
    }

    override suspend fun loadGuideProfiles(guideIds: List<String>, accessToken: String): List<GuideProfileInfo> =
        withContext(Dispatchers.IO) {
            requireConfigured()
            val validIds = guideIds.filter { UUID_PATTERN.matches(it) }.distinct()
            if (validIds.isEmpty()) return@withContext emptyList()
            val body = JSONObject().put("guide_ids", JSONArray(validIds)).toString()
            val response = request(
                method = "POST",
                path = "/rest/v1/rpc/get_guide_profiles",
                body = body,
                accessToken = accessToken,
                contentType = "application/json"
            )
            val rows = JSONArray(response)
            (0 until rows.length()).map { index ->
                val row = rows.getJSONObject(index)
                GuideProfileInfo(
                    userId = row.getString("user_id"),
                    displayName = row.nullableString("display_name"),
                    bio = row.nullableString("bio"),
                    avatarUrl = row.nullableString("avatar_url").ifBlank { null }
                )
            }
        }

    override suspend fun saveRoute(route: RouteItem, status: String, accessToken: String): RouteItem =
        withContext(Dispatchers.IO) {
            requireConfigured()
            require(accessToken.isNotBlank()) { "Inicia sesión para guardar una ruta." }
            require(status == "publicada" || status == "pendiente" || status == "borrador") {
                "Estado de ruta no válido."
            }
            val routeWithPersistedImage = persistRouteMedia(route.withPersistentIds(), accessToken)
            val persistedStatus = if (status == "publicada") "pendiente" else status
            val payload = routeWithPersistedImage.toRouteJson(persistedStatus)
            val response = request(
                method = "POST",
                path = "/rest/v1/rpc/save_route",
                body = JSONObject().put("route_data", payload).toString(),
                accessToken = accessToken,
                contentType = "application/json"
            )
            if (response.isNotBlank() && response != JSONObject.quote(routeWithPersistedImage.id)) {
                throw IllegalStateException("Supabase devolvió un identificador inesperado al guardar la ruta.")
            }
            routeWithPersistedImage.copy(status = persistedStatus)
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
        userId: String,
        challengeId: String,
        attempts: Int,
        score: Int,
        completed: Boolean,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        requireConfigured()
        require(accessToken.isNotBlank()) { "Inicia sesión para sincronizar el progreso del reto." }
        require(userId.isNotBlank()) { "Vuelve a iniciar sesión para sincronizar el progreso del reto." }
        require(attempts >= 0) { "El número de intentos no puede ser negativo." }
        require(score in 0..100) { "La puntuación debe estar entre 0 y 100." }
        val progress = JSONObject()
            .put("user_id", userId)
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

    override suspend fun saveRouteReview(
        userId: String,
        routeId: String,
        guideId: String,
        routeRating: Int,
        guideRating: Int,
        comment: String,
        accessToken: String
    ) = withContext(Dispatchers.IO) {
        requireConfigured()
        require(accessToken.isNotBlank()) { "Inicia sesión para dejar una valoración." }
        require(routeRating in 1..5 && guideRating in 1..5) { "Las valoraciones deben estar entre 1 y 5." }
        val review = JSONObject()
            .put("user_id", userId)
            .put("route_id", routeId)
            .put("guide_id", guideId)
            .put("route_rating", routeRating)
            .put("guide_rating", guideRating)
            .put("comment", comment.trim().take(500))
        request(
            method = "POST",
            path = "/rest/v1/route_reviews?on_conflict=user_id,route_id",
            body = JSONArray().put(review).toString(),
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

    override suspend fun loadTotalChallengeScore(accessToken: String): Int =
        withContext(Dispatchers.IO) {
            requireConfigured()
            require(accessToken.isNotBlank()) { "Inicia sesión para cargar tu puntuación." }
            val response = request(
                method = "GET",
                path = "/rest/v1/user_challenge_progress?select=score&status=eq.completado",
                accessToken = accessToken
            )
            val rows = JSONArray(response)
            (0 until rows.length()).sumOf { index ->
                rows.getJSONObject(index).optInt("score", 0)
            }
        }

    override suspend fun saveDisplayName(userId: String, displayName: String, accessToken: String) =
        withContext(Dispatchers.IO) {
            requireConfigured()
            val normalizedName = displayName.trim()
            require(normalizedName.isNotEmpty()) { "Escribe el nombre que quieres mostrar." }
            require(normalizedName.length <= 60) { "El nombre no puede superar los 60 caracteres." }
            require(userId.isNotBlank()) { "No se pudo identificar al usuario." }
            require(accessToken.isNotBlank()) { "Inicia sesión para guardar tu nombre." }
            request(
                method = "POST",
                path = "/rest/v1/user_profiles?on_conflict=user_id",
                body = JSONArray().put(
                    JSONObject()
                        .put("user_id", userId)
                        .put("display_name", normalizedName)
                ).toString(),
                accessToken = accessToken,
                contentType = "application/json",
                prefer = "resolution=merge-duplicates,return=minimal"
            )
            Unit
        }

    override suspend fun saveGuideProfile(
        userId: String,
        displayName: String,
        bio: String,
        avatarUrl: String?,
        accessToken: String
    ): String? = withContext(Dispatchers.IO) {
        requireConfigured()
        val normalizedName = displayName.trim()
        val normalizedBio = bio.trim()
        require(normalizedName.isNotEmpty()) { "Escribe tu nombre de guía." }
        require(normalizedName.length <= 60) { "El nombre no puede superar los 60 caracteres." }
        require(normalizedBio.length <= 500) { "La descripción no puede superar los 500 caracteres." }
        require(userId.isNotBlank()) { "No se pudo identificar al usuario." }
        require(accessToken.isNotBlank()) { "Inicia sesión para guardar tu perfil de guía." }
        val storedAvatarUrl = avatarUrl?.let { url ->
            if (url.startsWith("data:")) persistDataUrl(url, userId, "avatar", accessToken) else url
        }
        request(
            method = "POST",
            path = "/rest/v1/user_profiles?on_conflict=user_id",
            body = JSONArray().put(
                JSONObject()
                    .put("user_id", userId)
                    .put("display_name", normalizedName)
                    .put("bio", normalizedBio)
                    .put("avatar_url", storedAvatarUrl ?: JSONObject.NULL)
            ).toString(),
            accessToken = accessToken,
            contentType = "application/json",
            prefer = "resolution=merge-duplicates,return=minimal"
        )
        storedAvatarUrl
    }

    override suspend fun loadSavedRouteIds(userId: String, accessToken: String): Set<String> =
        withContext(Dispatchers.IO) {
            requireConfigured()
            require(userId.isNotBlank()) { "No se pudo identificar al usuario." }
            require(accessToken.isNotBlank()) { "Inicia sesión para cargar tus rutas guardadas." }
            val response = request(
                method = "GET",
                path = "/rest/v1/user_profiles?select=saved_route_ids&user_id=eq.${encode(userId)}",
                accessToken = accessToken
            )
            val rows = JSONArray(response)
            if (rows.length() == 0) emptySet() else {
                val ids = rows.getJSONObject(0).optJSONArray("saved_route_ids") ?: JSONArray()
                (0 until ids.length()).mapTo(mutableSetOf()) { ids.getString(it) }
            }
        }

    override suspend fun saveSavedRouteIds(
        userId: String,
        displayName: String,
        routeIds: Set<String>,
        accessToken: String
    ) =
        withContext(Dispatchers.IO) {
            requireConfigured()
            require(userId.isNotBlank()) { "No se pudo identificar al usuario." }
            require(accessToken.isNotBlank()) { "Inicia sesión para guardar tus rutas favoritas." }
            val profileResponse = request(
                method = "GET",
                path = "/rest/v1/user_profiles?select=display_name&user_id=eq.${encode(userId)}",
                accessToken = accessToken
            )
            val profiles = JSONArray(profileResponse)
            val storedName = profiles
                .optJSONObject(0)
                ?.optString("display_name")
                ?.takeIf { it.isNotBlank() }
            val nameToStore = storedName ?: displayName.trim()
            require(nameToStore.isNotBlank()) { "No se encontró un nombre para completar el perfil del usuario." }
            request(
                method = "POST",
                path = "/rest/v1/user_profiles?on_conflict=user_id",
                body = JSONArray().put(
                    JSONObject()
                        .put("user_id", userId)
                        .put("display_name", nameToStore)
                        .put("saved_route_ids", JSONArray(routeIds.sorted()))
                ).toString(),
                accessToken = accessToken,
                contentType = "application/json",
                prefer = "resolution=merge-duplicates,return=minimal"
            )
            Unit
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
            JSONObject(response).toAuthSession()

        }

    suspend fun refreshAuthSession(refreshToken: String): SupabaseAuthSession =
        withContext(Dispatchers.IO) {
            requireConfigured()
            require(refreshToken.isNotBlank()) { "No hay un token de renovación guardado." }
            val response = request(
                method = "POST",
                path = "/auth/v1/token?grant_type=refresh_token",
                body = JSONObject().put("refresh_token", refreshToken).toString(),
                contentType = "application/json"
            )
            JSONObject(response).toAuthSession()

        }

    private fun requireConfigured() {
        require(projectUrl.startsWith("https://") && publishableKey.isNotBlank()) {
            "Configura SUPABASE_URL y SUPABASE_PUBLISHABLE_KEY en .env.local para Android."
        }
    }

    private fun persistRouteMedia(route: RouteItem, accessToken: String): RouteItem {
        fun persist(url: String): String = if (url.startsWith("data:")) {
            persistDataUrl(url, route.guide.id, route.id, accessToken)
        } else {
            url
        }
        return route.copy(
            imageUrl = persist(route.imageUrl),
            stops = route.stops.map { stop ->
                stop.copy(
                    imageUrl = persist(stop.imageUrl),
                    recordedAudioUrl = stop.recordedAudioUrl?.let { persist(it) },
                    trial = stop.trial?.let { trial -> trial.copy(imageUrl = persist(trial.imageUrl)) }
                )
            }
        )
    }

    private fun persistDataUrl(dataUrl: String, creatorId: String, folderId: String, accessToken: String): String {
        val match = Regex(
            "^data:((?:image/(?:jpeg|png|webp))|(?:audio/(?:mp4|mpeg|webm|ogg)));base64,(.+)$",
            RegexOption.DOT_MATCHES_ALL
        ).matchEntire(dataUrl)
            ?: throw IllegalArgumentException("El archivo multimedia debe ser una imagen JPEG, PNG o WebP, o un audio válido.")
        val contentType = match.groupValues[1]
        val imageBytes = try {
            Base64.decode(match.groupValues[2], Base64.DEFAULT)
        } catch (error: IllegalArgumentException) {
            throw IllegalArgumentException("No se pudo decodificar un archivo multimedia de la ruta.", error)
        }
        require(imageBytes.isNotEmpty()) { "Un archivo multimedia de la ruta está vacío." }
        require(imageBytes.size <= 50 * 1024 * 1024) {
            "Cada archivo multimedia debe ocupar como máximo 50 MB."
        }

        require(creatorId.isNotBlank()) { "No se pudo determinar el propietario de la imagen." }
        val extension = when (contentType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            "audio/mp4" -> "m4a"
            "audio/mpeg" -> "mp3"
            "audio/webm" -> "webm"
            else -> "ogg"
        }
        val safeRouteId = folderId.replace(Regex("[^a-zA-Z0-9_-]"), "-")
        val objectPath = "$creatorId/$safeRouteId/${UUID.randomUUID()}.$extension"
        val connection = (URL(
            "${projectUrl.trimEnd('/')}/storage/v1/object/route-media/$objectPath"
        ).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 60_000
            doOutput = true
            setRequestProperty("apikey", publishableKey)
            setRequestProperty("Authorization", "Bearer $accessToken")
            setRequestProperty("Content-Type", contentType)
            setRequestProperty("x-upsert", "false")
        }
        try {
            connection.outputStream.use { it.write(imageBytes) }
            val status = connection.responseCode
            if (status !in 200..299) {
                val details = connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                throw IllegalStateException("No se pudo subir el archivo multimedia a Supabase: HTTP $status $details")
            }
            return "${projectUrl.trimEnd('/')}/storage/v1/object/public/route-media/$objectPath"
        } finally {
            connection.disconnect()
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

private fun JSONObject.toAuthSession(): SupabaseAuthSession {
    val user = getJSONObject("user")
    val metadata = user.optJSONObject("user_metadata") ?: JSONObject()
    return SupabaseAuthSession(
        accessToken = getString("access_token"),
        refreshToken = getString("refresh_token"),
        userId = user.getString("id"),
        email = user.optString("email"),
        displayName = metadata.optString(
            "full_name",
            metadata.optString("name", user.optString("email"))
        )
    )
}

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

private val UUID_PATTERN = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")

private fun JSONObject.nullableString(name: String): String =
    if (isNull(name)) "" else optString(name, "")

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
        mapPositionY = mapY,
        imageUrl = optString("imageUrl"),
        audioMode = optString("audioMode").toAudioMode(),
        narrationVoice = optString("narrationVoice").toNarrationVoice(),
        recordedAudioUrl = optString("recordedAudioUrl").takeIf { it.isNotBlank() }
    )
}

private fun String.toAudioMode(): StopAudioMode = when (lowercase()) {
    "tts" -> StopAudioMode.TTS
    "recorded" -> StopAudioMode.RECORDED
    else -> StopAudioMode.WRITTEN
}

private fun String.toNarrationVoice(): NarrationVoice = when (lowercase()) {
    "male" -> NarrationVoice.MALE
    else -> NarrationVoice.FEMALE
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
        imageUrl = optString("imageUrl"),
        puzzleGridSize = optInt("puzzleGridSize", 3),
        sentence = optString("sentence"),
        missingWord = optString("missingWord"),
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
            "naturaleza" -> Category.NATURALEZA
            "gastronomía", "gastronomia" -> Category.GASTRONOMIA
            "arte" -> Category.ARTE
            "tradiciones", "tradicións" -> Category.TRADICIONES
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
    "puzle_imagen" -> TrialType.PUZLE_IMAGEN
    "ordenar_palabra" -> TrialType.ORDENAR_PALABRA
    "completar_frase" -> TrialType.COMPLETAR_FRASE
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
        .put("imageUrl", imageUrl)
        .put("audioMode", audioMode.name.lowercase())
        .put("narrationVoice", narrationVoice.name.lowercase())
        .put("recordedAudioUrl", recordedAudioUrl ?: JSONObject.NULL)
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
    .put("imageUrl", imageUrl)
    .put("puzzleGridSize", puzzleGridSize)
    .put("sentence", sentence)
    .put("missingWord", missingWord)
    .put("generalHint", generalHint)
    .put("characterClues", JSONObject(characterClues))
    .put("solutionExplanation", solutionExplanation)
    .put("magicalReward", magicalReward)
