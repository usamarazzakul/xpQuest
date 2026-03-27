package com.brosfactory.xpQuest.domain.repository

import com.brosfactory.xpQuest.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Remote checks
    suspend fun checkUserExists(uid: String): Boolean
    suspend fun createUserProfile(profile: UserProfile): Result<Unit>

    // Local Cache (Room DB)
    fun getLocalUserProfile(): Flow<UserProfile?>
    suspend fun syncUserFromRemoteToLocal(uid: String)

    //  Function to validate referral code
    suspend fun validateReferralCode(code: String): String?
}