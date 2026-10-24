package com.ecosdolugar.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.ecosdolugar.R
import com.ecosdolugar.data.SupabaseAuthSession
import com.ecosdolugar.data.SupabaseRoutesRepository
import kotlinx.coroutines.CancellationException
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AndroidGoogleAuthService(
    private val context: Context,
    private val routesRepository: SupabaseRoutesRepository
) : GoogleAuthService {
    private val credentialManager = CredentialManager.create(context)
    private val preferences = context.getSharedPreferences("google_auth_session", Context.MODE_PRIVATE)
    private val sessionStorageKey = "encrypted_session"
    private val keystoreAlias = "ecos_google_auth_session"

    override suspend fun signInWithGoogle(): GoogleAuthResult {
        val webClientId = context.getString(R.string.google_web_client_id)
        if (webClientId.isBlank()) {
            return GoogleAuthResult.Error(
                "Añade GOOGLE_WEB_CLIENT_ID al archivo .env.local para habilitar Google Sign-In."
            )
        }

        val googleOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        val activity = CurrentActivity.get()
            ?: return GoogleAuthResult.Error("No hay una pantalla activa para iniciar sesión con Google.")

        return try {
            val response = credentialManager.getCredential(activity, request)
            val credential = response.credential
            if (credential !is CustomCredential ||
                credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return GoogleAuthResult.Error("Google no devolvió una credencial válida.")
            }

            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val tokenAudience = googleCredential.idToken
                .split('.')
                .getOrNull(1)
                ?.let { payload ->
                    runCatching {
                        JSONObject(
                            String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP))
                        ).optString("aud")
                    }.getOrNull()
                }
            if (tokenAudience != webClientId) {
                return GoogleAuthResult.Error(
                    "El Client ID de Google incluido en esta app no coincide con el del token. " +
                        "Revisa GOOGLE_WEB_CLIENT_ID en .env.local y vuelve a compilar."
                )
            }
            val session = routesRepository.exchangeGoogleIdToken(googleCredential.idToken)
            // A session saved by a previous install or key may be unreadable; sign-in must still work.
            val storedProfile = runCatching { readStoredSession() }.getOrNull()
                ?.takeIf { it.optString("user_id") == session.userId }
            val displayName = storedProfile?.optString("display_name")
                ?.takeIf(String::isNotBlank)
                ?: session.displayName.ifBlank { googleCredential.displayName.orEmpty() }
            val characterId = storedProfile?.optString("character_id")?.takeIf(String::isNotBlank)
            val displayNameSet = storedProfile?.optBoolean("display_name_set") == true
            val profileComplete = storedProfile?.optBoolean("profile_complete") == true
            val isGuideAccount = storedProfile?.optBoolean("is_guide_account") == true
            persistSession(session, displayName, characterId, displayNameSet, profileComplete, isGuideAccount)
            GoogleAuthResult.Success(
                googleId = session.userId,
                email = session.email,
                displayName = displayName,
                photoUrl = googleCredential.profilePictureUri?.toString(),
                idToken = googleCredential.idToken,
                accessToken = session.accessToken,
                refreshToken = session.refreshToken,
                characterId = characterId,
                displayNameSet = displayNameSet,
                profileComplete = profileComplete,
                isGuideAccount = isGuideAccount
            )
        } catch (_: GetCredentialCancellationException) {
            GoogleAuthResult.Cancelled
        } catch (error: GetCredentialException) {
            GoogleAuthResult.Error(error.message ?: "No se pudo iniciar sesión con Google.")
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            val message = error.message ?: "No se pudo verificar la sesión con Supabase."
            GoogleAuthResult.Error(
                if (message.contains("unacceptable audience", ignoreCase = true)) {
                    "Supabase rechazó el token de Google porque su audiencia no está autorizada. " +
                        "En Supabase > Authentication > Providers > Google, añade como Client ID " +
                        "el mismo GOOGLE_WEB_CLIENT_ID configurado para Android. Detalle: $message"
                } else {
                    message
                }
            )
        }
    }

    override suspend fun restoreSession(): GoogleAuthResult {
        val stored = try {
            readStoredSession()
        } catch (error: Exception) {
            clearStoredSession()
            return GoogleAuthResult.Error(
                "No se pudo recuperar la sesión guardada de forma segura: " +
                    (error.message ?: "datos de sesión no válidos.")
            )
        } ?: return GoogleAuthResult.NoStoredSession

        return try {
            val session = routesRepository.refreshAuthSession(stored.getString("refresh_token"))
            val displayName = stored.optString("display_name").ifBlank { session.displayName }
            val characterId = stored.optString("character_id").takeIf(String::isNotBlank)
            val isGuideAccount = stored.optBoolean("is_guide_account")
            val displayNameSet = stored.optBoolean("display_name_set")
            val profileComplete = stored.optBoolean("profile_complete") &&
                (characterId != null || isGuideAccount)
            persistSession(session, displayName, characterId, displayNameSet, profileComplete, isGuideAccount)
            GoogleAuthResult.Success(
                googleId = session.userId,
                email = session.email,
                displayName = displayName,
                photoUrl = null,
                idToken = "",
                accessToken = session.accessToken,
                refreshToken = session.refreshToken,
                characterId = characterId,
                displayNameSet = displayNameSet,
                profileComplete = profileComplete,
                isGuideAccount = isGuideAccount
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            GoogleAuthResult.Error(
                error.message ?: "No se pudo renovar la sesión guardada."
            )
        }
    }

    override suspend fun saveProfile(displayName: String, characterId: String) = updateStoredSession {
        put("display_name", displayName.trim())
        put("display_name_set", true)
        put("character_id", characterId)
        put("profile_complete", true)
    }

    override suspend fun setGuideAccount(isGuideAccount: Boolean) = updateStoredSession {
        put("is_guide_account", isGuideAccount)
    }

    override suspend fun saveDisplayName(displayName: String) = updateStoredSession {
        put("display_name", displayName.trim())
        put("display_name_set", true)
        put("profile_complete", false)
    }

    override suspend fun completeGuideProfile(displayName: String) = updateStoredSession {
        put("display_name", displayName.trim())
        put("display_name_set", true)
        put("profile_complete", true)
    }

    private fun updateStoredSession(update: JSONObject.() -> Unit) {
        val stored = readStoredSession()
            ?: throw IllegalStateException("No hay una sesión guardada para actualizar el perfil.")
        stored.update()
        writeStoredSession(stored)
    }

    override suspend fun signOut(accessToken: String?) {
        try {
            // The in-memory access token expires after about an hour, so renew it before the remote logout.
            val storedRefreshToken = runCatching { readStoredSession()?.optString("refresh_token") }
                .getOrNull()
                ?.takeIf(String::isNotBlank)
            val freshAccessToken = storedRefreshToken?.let { refreshToken ->
                runCatching { routesRepository.refreshAuthSession(refreshToken).accessToken }.getOrNull()
            }
            routesRepository.signOut(freshAccessToken ?: accessToken.orEmpty())
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            // The remote logout is best-effort; the local session is cleared below regardless.
        } finally {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } finally {
                clearStoredSession()
            }
        }
    }

    private fun persistSession(
        session: SupabaseAuthSession,
        displayName: String,
        characterId: String?,
        displayNameSet: Boolean,
        profileComplete: Boolean,
        isGuideAccount: Boolean = false
    ) {
        writeStoredSession(
            JSONObject()
                .put("user_id", session.userId)
                .put("email", session.email)
                .put("display_name", displayName)
                .put("display_name_set", displayNameSet)
                .put("access_token", session.accessToken)
                .put("refresh_token", session.refreshToken)
                .put("character_id", characterId ?: JSONObject.NULL)
                .put("profile_complete", profileComplete)
                .put("is_guide_account", isGuideAccount)
        )
    }

    private fun readStoredSession(): JSONObject? {
        val encrypted = preferences.getString(sessionStorageKey, null) ?: return null
        val combined = Base64.decode(encrypted, Base64.NO_WRAP)
        require(combined.size > GCM_IV_LENGTH_BYTES) { "El archivo de sesión está incompleto." }
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH_BYTES)
        val ciphertext = combined.copyOfRange(GCM_IV_LENGTH_BYTES, combined.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        return JSONObject(String(cipher.doFinal(ciphertext), Charsets.UTF_8))
    }

    private fun writeStoredSession(session: JSONObject) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val encrypted = cipher.iv + cipher.doFinal(session.toString().toByteArray(Charsets.UTF_8))
        val saved = preferences.edit()
            .putString(sessionStorageKey, Base64.encodeToString(encrypted, Base64.NO_WRAP))
            .commit()
        check(saved) { "No se pudo guardar la sesión cifrada en el dispositivo." }
    }

    private fun clearStoredSession() {
        check(preferences.edit().remove(sessionStorageKey).commit()) {
            "No se pudieron borrar los datos de sesión guardados."
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(keystoreAlias, null) as? SecretKey)?.let { return it }
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                keystoreAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private companion object {
        const val GCM_IV_LENGTH_BYTES = 12
        const val GCM_TAG_LENGTH_BITS = 128
    }
}
