package com.developerspace.webrtcsample.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.developerspace.webrtcsample.compose.ui.screens.ActiveUsersScreen
import com.developerspace.webrtcsample.compose.ui.screens.UserDetailScreen

@Composable
fun TopLevelNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "active_users_screen") {
        composable("active_users_screen") {
            ActiveUsersScreen(navController)
        }
        composable("user_detail_screen/{profileID}",
            arguments = listOf(navArgument("profileID") {
                type = NavType.IntType
            })
        ) { navBackStackEntry ->
            UserDetailScreen(navBackStackEntry.arguments!!.getInt("profileID"), navController)
        }
    }
}