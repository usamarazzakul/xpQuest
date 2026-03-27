package com.brosfactory.xpQuest.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brosfactory.xpQuest.domain.model.UserProfile
import com.brosfactory.xpQuest.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileSetupState {
    data object Idle : ProfileSetupState()
    data object Loading : ProfileSetupState()
    data object Success : ProfileSetupState()
    data class Error(val message: String) : ProfileSetupState()
}

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _setupState = MutableStateFlow<ProfileSetupState>(ProfileSetupState.Idle)
    val setupState: StateFlow<ProfileSetupState> = _setupState.asStateFlow()

    fun getAutoUsername(): String {
        return auth.currentUser?.email?.substringBefore("@") ?: "Player"
    }

    fun submitProfile(name: String, referCode: String, isSkipped: Boolean) {
        viewModelScope.launch {
            _setupState.value = ProfileSetupState.Loading

            val currentUser = auth.currentUser
            if (currentUser == null) {
                _setupState.value = ProfileSetupState.Error("User not authenticated")
                return@launch
            }

            // 👇 THE REAL VERIFICATION LOGIC 👇
            var hasValidReferral = false
            var referredByUid = ""

            if (!isSkipped && referCode.isNotBlank()) {
                // Search Firestore for the code
                val foundReferrerUid = userRepository.validateReferralCode(referCode)

                if (foundReferrerUid == null) {
                    // It's a random word! STOP EVERYTHING and show error.
                    _setupState.value = ProfileSetupState.Error("Invalid referral code. Please check and try again.")
                    return@launch
                } else {
                    // It's a real code!
                    hasValidReferral = true
                    referredByUid = foundReferrerUid
                }
            }
            // 👆 END OF VERIFICATION LOGIC 👆

            val username = currentUser.email?.substringBefore("@") ?: "Player"
            val startingCoins = if (hasValidReferral) 50 else 0
            val myReferralCode = "${username.take(4).uppercase()}${(100..999).random()}"

            val newProfile = UserProfile(
                uid = currentUser.uid,
                username = username,
                name = name.ifBlank { username },
                email = currentUser.email ?: "",
                referralCode = myReferralCode,
                isReferred = hasValidReferral,
                referredBy = referredByUid, // Store the friend's UID who invited them
                coin = startingCoins,
                xp = 0,
                profileUrl = currentUser.photoUrl?.toString() ?: ""
            )

            val result = userRepository.createUserProfile(newProfile)

            if (result.isSuccess) {
                _setupState.value = ProfileSetupState.Success
            } else {
                _setupState.value = ProfileSetupState.Error(result.exceptionOrNull()?.message ?: "Failed to save profile")
            }
        }
    }
}