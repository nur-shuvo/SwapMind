package com.developerspace.webrtcsample.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.developerspace.webrtcsample.compose.ui.screens.AccountProfileEditScreen
import com.developerspace.webrtcsample.compose.ui.screens.AccountProfileScreen
import com.developerspace.webrtcsample.compose.ui.screens.ActiveUsersScreen
import com.developerspace.webrtcsample.compose.ui.screens.MainScreen
import com.developerspace.webrtcsample.compose.ui.screens.UserDetailScreen
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TopLevelNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(navController)
        }
        composable("active_users_screen") {
            ActiveUsersScreen(navController)
        }
        composable("user_detail_screen/{profileUserID}",
            arguments = listOf(navArgument("profileUserID") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            UserDetailScreen(navBackStackEntry.arguments!!.getString("profileUserID")!!, navController)
        }
        composable("account_profile_screen/{profileUserID}",
            arguments = listOf(navArgument("profileUserID") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            AccountProfileScreen(navBackStackEntry.arguments!!.getString("profileUserID")!!, navController)
        }
        composable("account_profile_edit_screen/{type}",
            arguments = listOf(navArgument("type") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            AccountProfileEditScreen(navBackStackEntry.arguments!!.getString("type") ?: "", navController)
        }
    }
}