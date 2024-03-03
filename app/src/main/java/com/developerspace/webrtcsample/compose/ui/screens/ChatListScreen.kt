package com.developerspace.webrtcsample.compose.ui.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.theming.lightBlue
import com.developerspace.webrtcsample.compose.ui.viewmodel.ChatListViewModel
import com.developerspace.webrtcsample.compose.data.model.RecentMessage
import com.developerspace.webrtcsample.compose.data.model.User

@Composable
fun ChatListScreen() {
    val viewModel: ChatListViewModel = hiltViewModel()
    val recentMessageList by viewModel.recentMessageListState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        items(recentMessageList) { recentMessage ->
            ChatListItem(recentMessage, viewModel)
        }
    }
}

@Composable
fun ChatListItem(recentMessage: RecentMessage, viewModel: ChatListViewModel) {
    val context = LocalContext.current
    val activity = LocalContext.current as AppCompatActivity
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Go to chat screen
                val intent = Intent(context, ChatMainActivity::class.java)
                intent.putExtra("receiverUserID", recentMessage.toUserId)
                intent.putExtra("receiverUserName", recentMessage.toUserName)
                context.startActivity(intent)
                viewModel.resetUnreadCountUsingWorker(activity, recentMessage.toUserId)
            },
        horizontalArrangement = Arrangement.Start
    ) {
        ProfilePicture(user = User(photoUrl = recentMessage.toPhotoUrl), 45.dp)
        NameAndLastText(
            recentMessage,
            Modifier
                .fillMaxWidth(0.80f)
                .height(80.dp),
        )
        Spacer(Modifier.weight(1f))
        TimeAndUnreadIcon(
            recentMessage,
            Modifier
                .height(80.dp)
                .padding(end = 8.dp)
        )
    }
}

@Composable
fun NameAndLastText(recentMessage: RecentMessage, modifier: Modifier) {
    Column(verticalArrangement = Arrangement.Center, modifier = modifier) {
        Text(recentMessage.toUserName)
        Text(
            recentMessage.friendlyMessage.text?:"",
            maxLines = 1,
            style = TextStyle(Color.Gray, fontSize = 12.sp)
        )
    }
}

@Composable
fun TimeAndUnreadIcon(
    recentMessage: RecentMessage,
    modifier: Modifier
) {
    Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = modifier) {
        Text(
            recentMessage.friendlyMessage.time ?: "",
            style = TextStyle(Color.Gray, fontSize = 10.sp)
        )

        if (recentMessage.unreadCount != 0 &&
            recentMessage.toUserName == recentMessage.friendlyMessage.name
        ) Box(
            modifier = Modifier
                .height(22.dp)
                .width(22.dp)
                .clip(shape = RoundedCornerShape(50))
                .background(Color(0xFF25D366))
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text =  recentMessage.unreadCount.toString(),
                fontSize = 10.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

    }
}

@Composable
fun UnreadIcon() {
    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White
        ),
        backgroundColor = lightBlue
    ) {
        Text(text = "Unread", Modifier.padding(4.dp), fontSize = 8.sp, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewChat() {
    MyTheme {
        ChatListScreen()
    }
}
