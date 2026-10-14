package com.ecosdolugar.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.ecosdolugar.R
import com.ecosdolugar.data.SupabaseRoutesRepository
import kotlinx.coroutines.CancellationException
import android.util.Base64
import org.json.JSONObject

class AndroidGoogleAuthService(
    private val context: Context,
    private val routesRepository: SupabaseRoutesRepository
) : GoogleAuthService {
    private val credentialManager = CredentialManager.create(context)

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

        return try {
            val response = credentialManager.getCredential(context, request)
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
            GoogleAuthResult.Success(
                googleId = session.userId,
                email = session.email,
                displayName = session.displayName.ifBlank { googleCredential.displayName.orEmpty() },
                photoUrl = googleCredential.profilePictureUri?.toString(),
                idToken = googleCredential.idToken,
                accessToken = session.accessToken
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

    override suspend fun signOut(accessToken: String?) {
        try {
            routesRepository.signOut(accessToken.orEmpty())
        } finally {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }
    }
}
