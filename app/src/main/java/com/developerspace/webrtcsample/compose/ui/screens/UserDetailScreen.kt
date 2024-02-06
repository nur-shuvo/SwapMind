package com.developerspace.webrtcsample.compose.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.data.model.User
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.theming.lightGreen
import com.developerspace.webrtcsample.compose.ui.viewmodel.UserDetailViewModel
import com.developerspace.webrtcsample.compose.util.misc.MyOpenDocumentContract
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.sqrt

@Composable
fun UserDetailScreen(userProfileID: String, navController: NavController? = null) {
    val activity = LocalContext.current as AppCompatActivity
    val viewmodel: UserDetailViewModel = hiltViewModel()
    val userProfile by viewmodel.userProfileState.collectAsState()
    viewmodel.setUserProfile(userProfileID)
    val openDocument = rememberLauncherForActivityResult(contract = MyOpenDocumentContract(),
        onResult = { viewmodel.onProfileImageEditSelected(activity, it!!) })

    // profile image container size calculation
    val profileImageSize = 200
    val imagePickerIconSize = profileImageSize / 7
    val imagePickerIconPadding = imagePickerIconSize / 5
    val imagePickerIconBorder = imagePickerIconPadding / 5
    val imageBoxSize = profileImageSize
    val offset =
        (imagePickerIconSize / 2) + imagePickerIconPadding + imagePickerIconBorder + (profileImageSize / 12)
    val innerBoxSize = (imageBoxSize / sqrt(2.00)) + offset

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
                    ProfilePicture(userProfile, profileImageSize.dp)
                    Box(
                        modifier = Modifier
                            .height(innerBoxSize.dp)
                            .width(innerBoxSize.dp)
                            .align(Alignment.Center)
                    ) {
                        if (userProfile.userID == Firebase.auth.uid) {
                            Image(
                                painterResource(id = R.drawable.photo_camera_24px), "",
                                Modifier
                                    .clickable {
                                        openDocument.launch(arrayOf("image/*"))
                                    }
                                    .background(
                                        color = lightGreen,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        imagePickerIconBorder.dp,
                                        Color.Black,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(imagePickerIconPadding.dp)
                                    .size(imagePickerIconSize.dp)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }
                }
                ProfileContent(userProfile, Alignment.CenterHorizontally)
                ProfileSection("Name", false, userProfile)
                ProfileSection("About", false, userProfile)
                ProfileSection("Phone Number or Email", false, userProfile)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarWithBack(text: String, onBackPressed: () -> Unit) {
    TopAppBar(backgroundColor = Color.White,
        title = { Text(text) },
        navigationIcon = {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "",
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable {
                        onBackPressed.invoke()
                    },
            )
        })
}

@Composable
fun ProfileSection(type: String, isEditable: Boolean, user: User, onClicked: () -> Unit = {}) {
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
            type, getMiddleText(type, user), getBottomText(type),
            Modifier
                .fillMaxWidth(0.8f)
                .padding(20.dp)
        )
        if (!type.contains("Phone")) {
            Image(
                painter = painterResource(id = R.drawable.edit_24px),
                contentDescription = "",
                modifier = Modifier.clickable {
                    onClicked.invoke()
                })
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
            "Note that it is public"
        }
    }
}

fun getMiddleText(type: String, user: User): String {
    return if (type == "Name") {
        user.userName!!
    } else if (type == "About") {
        "Doing nothing is great" // TODO user bio
    } else {
        "Add your email or phone number"
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview1() {
    MyTheme {
        UserDetailScreen("")
    }
}