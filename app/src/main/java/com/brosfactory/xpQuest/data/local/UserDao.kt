package com.brosfactory.xpQuest.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Inserts or updates the user profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Gets the current user as a Flow so the UI updates automatically
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    // Gets the user once (not as a stream)
    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUser(): UserEntity?

    // Clears the local DB when the user logs out
    @Query("DELETE FROM user_profile")
    suspend fun clearUser()
}