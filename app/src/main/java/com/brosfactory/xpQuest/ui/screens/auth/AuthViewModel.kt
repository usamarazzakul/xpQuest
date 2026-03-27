package com.brosfactory.xpQuest.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brosfactory.xpQuest.domain.repository.AuthRepository
import com.brosfactory.xpQuest.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // 👇 Added missing function
    fun setLoading() {
        _authState.value = AuthState.Loading
    }

    // 👇 Added missing function
    fun onAuthError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun onGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val authResult = authRepository.signInWithGoogle(idToken)

            if (authResult.isSuccess) {
                val uid = authResult.getOrNull()!!
                val userExists = userRepository.checkUserExists(uid)

                if (userExists) {
                    userRepository.syncUserFromRemoteToLocal(uid)
                    _authState.value = AuthState.NavigateToHome
                } else {
                    _authState.value = AuthState.NavigateToProfileSetup
                }
            } else {
                _authState.value = AuthState.Error(authResult.exceptionOrNull()?.message ?: "Google Sign-In failed")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}