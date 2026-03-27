package com.brosfactory.xpQuest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.brosfactory.xpQuest.domain.model.UserProfile

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val uid: String,
    val username: String,
    val name: String,
    val email: String,
    val referralCode: String,
    val isReferred: Boolean,
    val referredBy: String,
    val coin: Int,
    val xp: Int,
    val profileUrl: String
)

// Extension function to easily map between Data and Domain layers
fun UserEntity.toDomain() = UserProfile(
    uid, username, name, email, referralCode, isReferred, referredBy, coin, xp, profileUrl
)

fun UserProfile.toEntity() = UserEntity(
    uid, username, name, email, referralCode, isReferred, referredBy, coin, xp, profileUrl
)