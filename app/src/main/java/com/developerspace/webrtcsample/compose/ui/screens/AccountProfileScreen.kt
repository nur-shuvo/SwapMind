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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.theming.lightGreen
import com.developerspace.webrtcsample.compose.ui.viewmodel.AccountProfileViewModel
import com.developerspace.webrtcsample.util.misc.MyOpenDocumentContract
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.sqrt

@Composable
fun AccountProfileScreen(profileUserID: String, navController: NavController? = null) {
    val activity = LocalContext.current as AppCompatActivity
    val viewmodel: AccountProfileViewModel = hiltViewModel()
    val userProfile by viewmodel.userProfileState.collectAsState()
    val isProgressShow by viewmodel.isProgressLoading.collectAsState()
    viewmodel.setUserProfile(profileUserID)
    val openDocument = rememberLauncherForActivityResult(contract = MyOpenDocumentContract(),
        onResult = {
            if (it != null) {
                viewmodel.onProfileImageEditSelected(activity, it)
            }
        })

    // profile image container size calculation
    val profileImageSize = 200
    val imagePickerIconSize = profileImageSize / 7
    val imagePickerIconPadding = imagePickerIconSize / 5
    val imagePickerIconBorder = imagePickerIconPadding / 5
    val imageBoxSize = profileImageSize;
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
            if (isProgressShow) {
                CircularProgressIndicator(modifier = Modifier
                    .wrapContentSize()
                    .size(80.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box {
                    ProfilePicture(userProfile, profileImageSize.dp, color = Color.Blue)
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
                ProfileSection("Name", true, userProfile) {
                    navController?.navigate("account_profile_edit_screen/name")
                }
                ProfileSection("About", true, userProfile) {

                }
                ProfileSection("Phone Number or Email", false, userProfile)
                Spacer(Modifier.weight(1.0f))
                Button(onClick = {
                    viewmodel.signOut(activity)
                }, modifier = Modifier.padding(bottom = 15.dp)) {
                    Text("Sign Out")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewAccountProfile() {
    MyTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .wrapContentSize()
                    .size(80.dp)
            )
        }
    }
}

