package com.ecosdolugar.auth

import com.ecosdolugar.model.UserProfile

sealed class GoogleAuthResult {
    data class Success(
        val googleId: String,
        val email: String,
        val displayName: String,
        val photoUrl: String?,
        val idToken: String,
        val accessToken: String
    ) : GoogleAuthResult()

    data class Error(val message: String) : GoogleAuthResult()
    object Cancelled : GoogleAuthResult()
}

interface GoogleAuthService {
    suspend fun signInWithGoogle(): GoogleAuthResult
    suspend fun signOut(accessToken: String?)
}
