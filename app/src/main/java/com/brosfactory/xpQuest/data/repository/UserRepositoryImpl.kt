package com.brosfactory.xpQuest.data.repository

import com.brosfactory.xpQuest.data.local.UserDao
import com.brosfactory.xpQuest.data.local.toDomain
import com.brosfactory.xpQuest.data.local.toEntity
import com.brosfactory.xpQuest.domain.model.UserProfile
import com.brosfactory.xpQuest.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : UserRepository {

    private val usersCollection = firestore.collection("users")

    // 👇 ADD THIS NEW IMPLEMENTATION 👇
    override suspend fun validateReferralCode(code: String): String? {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("referralCode", code)
                .limit(1)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                snapshot.documents[0].id // Returns the real user's UID
            } else {
                null // Code is fake/not found
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun checkUserExists(uid: String): Boolean {
        return try {
            val document = usersCollection.document(uid).get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun createUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            usersCollection.document(profile.uid).set(profile).await()
            userDao.insertUser(profile.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncUserFromRemoteToLocal(uid: String) {
        try {
            val document = usersCollection.document(uid).get().await()
            if (document.exists()) {
                val profile = document.toObject(UserProfile::class.java)
                profile?.let {
                    userDao.insertUser(it.toEntity())
                }
            }
        } catch (e: Exception) {
            // Ignore offline errors
        }
    }

    override fun getLocalUserProfile(): Flow<UserProfile?> {
        return userDao.getUserFlow().map { it?.toDomain() }
    }
}