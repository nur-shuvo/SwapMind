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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.theming.lightGreen
import com.developerspace.webrtcsample.compose.ui.util.AppLevelCache
import com.developerspace.webrtcsample.compose.ui.viewmodel.AccountProfileViewModel
import com.developerspace.webrtcsample.model.User
import com.developerspace.webrtcsample.util.MyOpenDocumentContract
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun AccountProfileScreen(userProfileID: Int, navController: NavController? = null) {
    val activity = LocalContext.current as AppCompatActivity
    val viewmodel: AccountProfileViewModel = viewModel()
    val userProfile by viewmodel.userProfileState.collectAsState()
    viewmodel.setUserProfile(AppLevelCache.userProfiles?.get(userProfileID) ?: User())
    val openDocument = rememberLauncherForActivityResult(contract = MyOpenDocumentContract(),
        onResult = {
            if (it != null) {
                viewmodel.onProfileImageEditSelected(activity, it)
            }
        })

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
                    if (userProfile.userID == Firebase.auth.uid) {
                        Image(
                            painterResource(id = R.drawable.photo_camera_24px), "",
                            Modifier
                                .clickable {
                                    openDocument.launch(arrayOf("image/*"))
                                }
                                .background(color = lightGreen, shape = RoundedCornerShape(12.dp))
                                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                                .padding(5.dp)
                                .align(Alignment.BottomCenter), alignment = Alignment.BottomCenter
                        )
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
        AccountProfileScreen(0)
    }
}

