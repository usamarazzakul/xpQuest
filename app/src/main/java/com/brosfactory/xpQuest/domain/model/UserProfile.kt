package com.brosfactory.xpQuest.domain.model

data class UserProfile(
    val uid: String = "",
    val username: String = "",
    val name: String = "",
    val email: String = "",
    val referralCode: String = "",
    val isReferred: Boolean = false,
    val referredBy: String = "",
    val coin: Int = 0,
    val xp: Int = 0,
    val profileUrl: String = ""
)