package com.developerspace.webrtcsample.compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.viewmodel.LiveStreamViewModel
import com.developerspace.webrtcsample.compose.ui.viewmodel.Stream

@Composable
fun LiveStreamScreen(
    navController: NavController? = null,
    viewModel: LiveStreamViewModel = hiltViewModel()
) {
    val streams by viewModel.streams.collectAsStateWithLifecycle()
    LiveStreamScreenContent(streams, {
        navController?.navigate("live_stream_running/${io.agora.rtc2.Constants.CLIENT_ROLE_BROADCASTER}/hello")
    }, {
        navController?.navigate("live_stream_running/${io.agora.rtc2.Constants.CLIENT_ROLE_BROADCASTER}/hello")
    })
}

@Composable
fun LiveStreamScreenContent(
    streams: List<Stream>,
    startStream: () -> Unit,
    joinStreamAudience: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(streams) { stream ->
                    StreamCard(
                        broadcasterName = stream.broadcasterName,
                        channelId = stream.channelId,
                        profileImageUrl = stream.profileImageUrl,
                        joinStreamAudience = joinStreamAudience
                    )
                }
            }

            Button(
                onClick = {
                    startStream.invoke()
                    // TODO Host broadcasting channel
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .padding(bottom = 10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "Start Stream",
                    style = MaterialTheme.typography.button,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun StreamCard(
    broadcasterName: String,
    channelId: String,
    profileImageUrl: String,
    joinStreamAudience: () -> Unit,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                joinStreamAudience.invoke()
            },
        elevation = 6.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray.copy(alpha = 0.2f), CircleShape), // Border effect
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.user_person)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = broadcasterName,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    joinStreamAudience.invoke()
                },
                modifier = Modifier
                    .height(36.dp)
                    .width(80.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(
                    text = "Join",
                    color = Color.White,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewStreamScreen() {
    MyTheme {
        LiveStreamScreenContent(
            listOf(
                Stream("Shuvo", "first_channel", ""),
                Stream("Wakil", "second_channel", "")
            ), {}, {}
        )
    }
}