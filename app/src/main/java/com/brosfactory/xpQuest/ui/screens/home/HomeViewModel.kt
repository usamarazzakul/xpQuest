package com.brosfactory.xpQuest.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brosfactory.xpQuest.domain.model.UserProfile
import com.brosfactory.xpQuest.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth // 👇 Added FirebaseAuth
) : ViewModel() {

    // Listens to the local Room DB
    val userProfile: StateFlow<UserProfile?> = userRepository.getLocalUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // 👇 ADDED THIS: Automatically syncs data when the screen opens
    init {
        syncProfileWithFirebase()
    }

    private fun syncProfileWithFirebase() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                // Grabs the latest coins/XP from Firestore and saves it to Room
                userRepository.syncUserFromRemoteToLocal(uid)
            }
        }
    }
}