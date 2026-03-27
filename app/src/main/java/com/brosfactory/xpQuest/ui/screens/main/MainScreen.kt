package com.brosfactory.xpQuest.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brosfactory.xpQuest.ui.navigation.Routes
import com.brosfactory.xpQuest.ui.screens.games.GamesScreen
import com.brosfactory.xpQuest.ui.screens.home.HomeScreen

@Composable
fun MainScreen(rootNavController: NavController) { // rootNavController is for logging out
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == Routes.HOME,
                    onClick = {
                        bottomNavController.navigate(Routes.HOME) {
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.VideogameAsset, contentDescription = "Games") },
                    label = { Text("Games") },
                    selected = currentRoute == Routes.GAMES,
                    onClick = {
                        bottomNavController.navigate(Routes.GAMES) {
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "Quiz") },
                    label = { Text("Quiz") },
                    selected = currentRoute == Routes.QUIZ,
                    onClick = {
                        bottomNavController.navigate(Routes.QUIZ) {
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentRoute == Routes.PROFILE,
                    onClick = {
                        bottomNavController.navigate(Routes.PROFILE) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        // Nested NavHost for the Bottom Tabs
// Nested NavHost for the Bottom Tabs
        NavHost(
            navController = bottomNavController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) { HomeScreen() }
            // 👇 Link the Games Tab to the new GamesScreen we just built
            composable(Routes.GAMES) { GamesScreen(rootNavController = rootNavController) }
            composable(Routes.QUIZ) { Text("Quiz Screen Coming Soon") }
            composable(Routes.PROFILE) { Text("Profile Screen Coming Soon") }
        }    }
}