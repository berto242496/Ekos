package com.ecosdolugar.auth

import com.ecosdolugar.model.UserProfile

sealed class GoogleAuthResult {
    data class Success(
        val email: String,
        val displayName: String,
        val photoUrl: String?,
        val idToken: String
    ) : GoogleAuthResult()

    data class Error(val message: String) : GoogleAuthResult()
    object Cancelled : GoogleAuthResult()
}

interface GoogleAuthService {
    suspend fun signInWithGoogle(): GoogleAuthResult
    suspend fun signOut()
    fun getCurrentUser(): UserProfile?
}

class MockGoogleAuthService : GoogleAuthService {
    private var currentUser: UserProfile? = null

    override suspend fun signInWithGoogle(): GoogleAuthResult {
        val email = "bert2424.b2@gmail.com"
        val name = "Explorador da Raia"
        currentUser = UserProfile(
            name = name,
            email = email,
            isVisitor = false,
            isGoogleAuthenticated = true,
            googleId = "google_user_983172"
        )
        return GoogleAuthResult.Success(
            email = email,
            displayName = name,
            photoUrl = null,
            idToken = "mock_google_id_token"
        )
    }

    override suspend fun signOut() {
        currentUser = null
    }

    override fun getCurrentUser(): UserProfile? = currentUser
}
