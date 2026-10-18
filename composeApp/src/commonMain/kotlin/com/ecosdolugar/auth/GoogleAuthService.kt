package com.ecosdolugar.auth

import com.ecosdolugar.model.UserProfile

sealed class GoogleAuthResult {
    data class Success(
        val googleId: String,
        val email: String,
        val displayName: String,
        val photoUrl: String?,
        val idToken: String,
        val accessToken: String,
        val refreshToken: String,
        val characterId: String? = null,
        val displayNameSet: Boolean = false,
        val profileComplete: Boolean = false,
        val isGuideAccount: Boolean = false
    ) : GoogleAuthResult()

    data class Error(val message: String) : GoogleAuthResult()
    object Cancelled : GoogleAuthResult()
    object NoStoredSession : GoogleAuthResult()
}

interface GoogleAuthService {
    suspend fun signInWithGoogle(): GoogleAuthResult
    suspend fun restoreSession(): GoogleAuthResult
    suspend fun saveDisplayName(displayName: String)
    suspend fun saveProfile(displayName: String, characterId: String)
    suspend fun setGuideAccount(isGuideAccount: Boolean)
    suspend fun signOut(accessToken: String?)
}
