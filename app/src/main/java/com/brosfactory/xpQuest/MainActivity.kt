package com.brosfactory.xpQuest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.brosfactory.xpQuest.ui.navigation.AppNavGraph
import com.brosfactory.xpQuest.ui.navigation.Routes
import com.brosfactory.xpQuest.ui.theme.XpQuestTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            XpQuestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Determine where to start based on Firebase Auth state
                    val startDestination = if (auth.currentUser != null) {
                        Routes.MAIN_DASHBOARD
                    } else {
                        Routes.AUTH
                    }

                    // 👇 THIS WAS MISSING! We have to actually tell the app to draw the graph!
                    AppNavGraph(startDestination = startDestination)
                }
            }
        }
    }
}