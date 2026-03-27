package com.brosfactory.xpQuest.domain.repository

import com.brosfactory.xpQuest.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    // Only Google Sign-In required
    suspend fun signInWithGoogle(idToken: String): Result<String>

    fun signOut()
}