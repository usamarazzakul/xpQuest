package com.brosfactory.xpQuest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brosfactory.xpQuest.features.games.memorygame.MemoryGameViewModel
import com.brosfactory.xpQuest.ui.screens.auth.AuthScreen
import com.brosfactory.xpQuest.ui.screens.auth.CompleteProfileScreen
import com.brosfactory.xpQuest.ui.screens.games.endlessrunner.RunnerGameScreen
import com.brosfactory.xpQuest.ui.screens.main.MainScreen

@Composable
fun AppNavGraph(
    startDestination: String,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // 1. Auth Screen
        composable(Routes.AUTH) {
            AuthScreen(
                onNavigateToHome = {
                    // 👇 CHANGED FROM HOME TO MAIN_DASHBOARD 👇
                    navController.navigate(Routes.MAIN_DASHBOARD) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onNavigateToProfileSetup = {
                    navController.navigate(Routes.COMPLETE_PROFILE) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        // 2. Complete Profile Screen
        composable(Routes.COMPLETE_PROFILE) {
            CompleteProfileScreen(
                onNavigateToHome = {
                    // 👇 CHANGED FROM HOME TO MAIN_DASHBOARD 👇
                    navController.navigate(Routes.MAIN_DASHBOARD) {
                        popUpTo(Routes.COMPLETE_PROFILE) { inclusive = true }
                    }
                }
            )
        }

        // 3. Main Dashboard (Replaces the old HOME composable)
        composable(Routes.MAIN_DASHBOARD) {
            MainScreen(rootNavController = navController)
        }
        // 4. Runner Game
        composable(Routes.RUNNER_GAME) {
            RunnerGameScreen(
                onNavigateHome = { navController.popBackStack() }, // 👇 THIS FIXES THE ERROR!
            )
        }

        // 5. Memory Game
        composable(Routes.MEMORY_GAME) {
            val viewModel: MemoryGameViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            com.brosfactory.xpQuest.features.games.memorygame.MemoryGameScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}