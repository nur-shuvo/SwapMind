package com.developerspace.webrtcsample.compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.developerspace.webrtcsample.compose.ui.screens.AccountProfileEditScreen
import com.developerspace.webrtcsample.compose.ui.screens.AccountProfileScreen
import com.developerspace.webrtcsample.compose.ui.screens.LiveStreamRunningScreen
import com.developerspace.webrtcsample.compose.ui.screens.MainScreen
import com.developerspace.webrtcsample.compose.ui.screens.StoryDetailView
import com.developerspace.webrtcsample.compose.ui.screens.TopicScreen
import com.developerspace.webrtcsample.compose.ui.screens.UserDetailScreen
import com.developerspace.webrtcsample.compose.ui.util.Topic
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.gson.Gson

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(navController)
        }
        composable(
            "user_detail_screen/{profileUserID}",
            arguments = listOf(navArgument("profileUserID") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            UserDetailScreen(
                navBackStackEntry.arguments!!.getString("profileUserID")!!,
                navController
            )
        }
        composable(
            "account_profile_screen/{profileUserID}",
            arguments = listOf(navArgument("profileUserID") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            AccountProfileScreen(
                navBackStackEntry.arguments!!.getString("profileUserID")!!,
                navController
            )
        }
        composable(
            "account_profile_edit_screen/{type}",
            arguments = listOf(navArgument("type") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            AccountProfileEditScreen(
                navBackStackEntry.arguments!!.getString("type") ?: "",
                navController
            )
        }
        composable(
            "topic_screen/{topicJsonString}",
            arguments = listOf(navArgument("topicJsonString") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val jsonArg = navBackStackEntry.arguments!!.getString("topicJsonString")
            val topic = Gson().fromJson(jsonArg, Topic::class.java)
            TopicScreen(topic, navController)
        }
        composable(
            "story_detail_view"
        ) { navBackStackEntry ->
            StoryDetailView(navController)
        }
        composable(
            "live_stream_running/{clientRole}/{cnlName}",
            arguments = listOf(navArgument("clientRole") {
                type = NavType.IntType
            }, navArgument("cnlName") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val role = navBackStackEntry.arguments!!.getInt("clientRole")
            val cnlName = navBackStackEntry.arguments!!.getString("cnlName") ?: ""
            LiveStreamRunningScreen(navController, role, cnlName)
        }
    }
}