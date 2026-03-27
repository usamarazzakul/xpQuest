package com.brosfactory.xpQuest.ui.screens.auth

import android.content.Context
import android.util.Log // Added import
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.brosfactory.xpQuest.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

// 👇 Fixed duplicate signature
@Composable
fun AuthScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.NavigateToHome -> {
                onNavigateToHome()
                viewModel.resetState()
            }
            is AuthState.NavigateToProfileSetup -> {
                onNavigateToProfileSetup()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to xpQuest",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    launchGoogleSignIn(context, viewModel)
                }
            },
            enabled = authState !is AuthState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Sign in with Google")
            }
        }
    }
}

// Helper function to trigger Credential Manager
private suspend fun launchGoogleSignIn(
    context: Context,
    viewModel: AuthViewModel
) {
    viewModel.setLoading()

    val credentialManager = CredentialManager.create(context)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(context.getString(R.string.default_web_client_id))
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            viewModel.onGoogleSignInResult(googleIdTokenCredential.idToken)

        } else {
            viewModel.onAuthError("Unexpected credential type")
        }

    } catch (e: GetCredentialException) {
        // 👇 Log added back so you can see if it fails in Logcat
        Log.e("AuthScreen", "Google Sign-In Failed: ${e.message}", e)
        viewModel.onAuthError("Google sign-in failed")
    } catch (e: Exception) {
        Log.e("AuthScreen", "Unexpected Error: ${e.message}", e)
        viewModel.onAuthError("Unexpected error occurred")
    }
}