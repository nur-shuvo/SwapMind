package com.developerspace.webrtcsample.compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.developerspace.webrtcsample.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.data.model.User
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun MainScreen(navController: NavController? = null) {
    val pagerState = rememberPagerState(pageCount = 4)
    Column(
        modifier = Modifier
            .background(Color.White)
    ) {
        TopAppBar(backgroundColor = Color.White) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Swap Mind",
                    style = TextStyle(
                        color = Color.Blue,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Cursive
                    ),
                    fontWeight = FontWeight.W900,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(all = 5.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.weight(1.0f))
                ProfilePicture(
                    user = User(photoUrl = getPhotoUrl()),
                    25.dp,
                    Color.Blue,
                    1.dp,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    navController?.navigate("account_profile_screen/${getCurrentUserID()}")
                }
            }
        }
        Tabs(pagerState = pagerState)
        TabsContent(pagerState = pagerState, navController)
    }
}

// on below line we are creating a function for tabs @ExperimentalPagerApi
@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(pagerState: PagerState) {
    val list = listOf(
        "Home" to Icons.Default.Home,
        "Active" to Icons.Default.Face,
        "Chat" to Icons.Default.Email,
        "Live" to Icons.Default.Home // change when creation
    )
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.White,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color.Blue
            )
        }
    ) {
        list.forEachIndexed { index, _ ->
            Tab(
                icon = {
                    if (index == 3) Icon(
                        painterResource(id = R.drawable.live_cast_24px),
                        contentDescription = null
                    )
                    else Icon(imageVector = list[index].second, contentDescription = null)
                },
                text = {
                    Text(
                        list[index].first,
                        color = if (pagerState.currentPage == index) Color.Blue else Color.Black
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabsContent(pagerState: PagerState, navController: NavController?) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> HomeScreen(navController)
            1 -> ActiveUsersScreen(navController)
            2 -> ChatListScreen()
            3 -> LiveStreamScreen()
        }
    }
}

private fun getPhotoUrl(): String? {
    val user = Firebase.auth.currentUser
    return user?.photoUrl?.toString()
}

private fun getCurrentUserID() = Firebase.auth.currentUser!!.uid


@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    MyTheme {
        LiveStreamScreen()
    }
}