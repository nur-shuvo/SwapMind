package com.developerspace.webrtcsample.compose.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.model.User

@Composable
fun ChatListScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        ChatListItem()
        ChatListItem()
        ChatListItem()
        ChatListItem()
        ChatListItem()
    }
}

@Composable
fun ChatListItem() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        ProfilePicture(user = User(), 45.dp)
        NameAndLastText(
            Modifier
                .fillMaxWidth(0.80f)
                .height(80.dp)
        )
        Spacer(Modifier.weight(1f))
        TimeAndUnreadIcon(
            modifier = Modifier
                .height(80.dp)
                .padding(end = 8.dp)
        )
    }
}

@Composable
fun NameAndLastText(modifier: Modifier) {
    Column(verticalArrangement = Arrangement.Center, modifier = modifier) {
        Text("Mr Asad")
        Text(
            "Why are you so busy? Please add me in the group that is in front of you",
            maxLines = 1,
            style = TextStyle(Color.Gray, fontSize = 12.sp)
        )
    }
}

@Composable
fun TimeAndUnreadIcon(modifier: Modifier) {
    Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = modifier) {
        Text("03:21", style = TextStyle(Color.Gray, fontSize = 10.sp))
        UnreadIcon()
    }
}

@Composable
fun UnreadIcon() {
    Card(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.Blue
        ),
        backgroundColor = Color.Green
    ) {
        Text(text = "Unread", Modifier.padding(4.dp), fontSize = 8.sp, color = Color.Blue)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewChat() {
    MyTheme {
        ChatListScreen()
    }
}
