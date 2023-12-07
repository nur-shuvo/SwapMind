package com.developerspace.webrtcsample.compose.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.theming.lightGreen
import com.developerspace.webrtcsample.compose.ui.util.AppLevelCache
import com.developerspace.webrtcsample.model.User

@Composable
fun UserDetailScreen(userProfileID: Int, navController: NavController? = null) {
    val userProfile = AppLevelCache.userProfiles?.get(userProfileID) ?: User()
    Scaffold(topBar = {
        AppBarWithBack(userProfile.userName!!) {
            navController?.navigateUp()
        }
    }) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box {
                    ProfilePicture(userProfile, 240.dp)
                    Image(
                        painterResource(id = R.drawable.photo_camera_24px), "",
                        Modifier
                            .background(color = lightGreen, shape = RoundedCornerShape(12.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                            .padding(5.dp)
                            .align(Alignment.BottomCenter), alignment = Alignment.BottomCenter
                    )
                }
                ProfileContent(userProfile, Alignment.CenterHorizontally)
                ProfileSection("Name", true)
                ProfileSection("About", true)
                ProfileSection("Phone Number or Email", false)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarWithBack(text: String, onBackPressed: () -> Unit) {
    TopAppBar(title = { Text(text) }, navigationIcon = {
        Icon(
            Icons.Default.ArrowBack,
            contentDescription = "",
            modifier = Modifier.padding(horizontal = 12.dp).clickable {
                onBackPressed.invoke()
            },
        )
    })
}

@Composable
fun ProfileSection(type: String, isEditable: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val drawable: Int = if (type == "Name") {
            R.drawable.user_person
        } else if (type == "About") {
            R.drawable.info_24px
        } else {
            R.drawable.call_24px
        }
        Image(painter = painterResource(id = drawable), contentDescription = "")
        InnerComponent(
            type, getMiddleText(type), getBottomText(type),
            Modifier
                .fillMaxWidth(0.8f)
                .padding(20.dp)
        )
        if (!type.contains("Phone")) {
            Image(painter = painterResource(id = R.drawable.edit_24px), contentDescription = "")
        } else {
            Spacer(
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            )
        }
    }
}

@Composable
fun InnerComponent(topText: String, middleText: String, bottomText: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(topText, style = TextStyle(color = Color.Gray))
        Text(middleText)
        Text(bottomText, style = TextStyle(color = Color.Gray, fontSize = 12.sp))
    }
}

fun getBottomText(type: String): String {
    return when (type) {
        "Name" -> {
            "This is not your username or pin. It is just displayed in your profile"
        }
        "About" -> {
            "Only visible to your friends" // TODO user bio
        }
        else -> {
            ""
        }
    }
}

fun getMiddleText(type: String): String {
    return if (type == "Name") {
        "Zayeng Kenn"
    } else if (type == "About") {
        "Doing nothing is great" // TODO user bio
    } else {
        "8801537227217"
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview1() {
    MyTheme {
        UserDetailScreen(0)
    }
}