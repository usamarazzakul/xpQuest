package com.brosfactory.xpQuest.ui.screens.auth

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object NavigateToHome : AuthState()
    data object NavigateToProfileSetup : AuthState()
    data class Error(val message: String) : AuthState()
}