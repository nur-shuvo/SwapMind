package com.developerspace.webrtcsample.compose.ui.screens

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.developerspace.webrtcsample.compose.data.model.User
import com.developerspace.webrtcsample.compose.ui.viewmodel.StoryDetailViewModel
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun StoryDetailView(navController: NavController?) {
    val viewModel: StoryDetailViewModel = hiltViewModel()
    val activity = LocalContext.current as AppCompatActivity
    LaunchedEffect(Unit) {
        delay(10.seconds)
        navController?.navigateUp()
    }
    Card(modifier = Modifier.fillMaxSize()) {
        Box {
            Image(
                painter = rememberImagePainter(
                    data = viewModel.selectedStoryDetail?.remoteStory?.storyUrl,
                    builder = {
                        transformations(RoundedCornersTransformation())
                    },
                ),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
            Text(
                text = viewModel.selectedStoryDetail?.user?.userName!!,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .clickable {
                        goForChatActivity(activity, viewModel.selectedStoryDetail.user)
                    }
            )
            ProfilePicture(
                user = viewModel.selectedStoryDetail.user,
                size = 56.dp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(bottom = 15.dp),
                borderStroke = 0.5.dp
            ) {
                goForChatActivity(activity, viewModel.selectedStoryDetail.user)
            }
        }
    }
}

private fun goForChatActivity(context: Context, user: User) {
    val intent = Intent(context, ChatMainActivity::class.java)
    intent.putExtra("receiverUserID", user.userID)
    intent.putExtra("receiverUserName", user.userName)
    context.startActivity(intent)
}