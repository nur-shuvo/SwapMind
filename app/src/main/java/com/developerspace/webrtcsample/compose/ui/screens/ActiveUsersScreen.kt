package com.developerspace.webrtcsample.compose.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.data.model.User
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.theming.lightGreen
import com.developerspace.webrtcsample.compose.ui.theming.shadeGreen
import com.developerspace.webrtcsample.compose.ui.viewmodel.ActiveUserViewModel
import com.developerspace.webrtcsample.legacy.ChatMainActivity

@Composable
fun ActiveUsersScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val viewModel: ActiveUserViewModel = hiltViewModel()
    val userListState by viewModel.userListState.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                )
            }
            itemsIndexed(userListState) { index, it ->
                ProfileCard(it,
                    onClickCard = {
                        navController?.navigate("user_detail_screen/${it.userID}")
                    },
                    onClickMessage = {
                        // Go to chat screen
                        val intent = Intent(context, ChatMainActivity::class.java)
                        intent.putExtra("receiverUserID", it.userID)
                        intent.putExtra("receiverUserName", it.userName)
                        context.startActivity(intent)
                    })
            }
        }
    }
}

@Composable
fun ProfileCard(user: User, onClickCard: () -> Unit, onClickMessage: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .clickable { onClickCard() }
            .padding(12.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(user) {
                onClickCard()
            }
            Spacer(modifier = Modifier.width(16.dp))
            ProfileContent(user, tStyle = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            MessageSection(onClickMessage)
        }
    }
}

@Composable
fun MessageSection(onCLickMessage: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.baseline_textsms_24),
        contentDescription = "",
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .clickable {
                onCLickMessage.invoke()
            }
            .padding(end = 10.dp),
        alignment = Alignment.CenterEnd
    )
}

@Composable
fun ProfilePicture(
    user: User, size: Dp = 72.dp, color: Color = lightGreen,
    borderStroke: Dp = 2.dp,
    modifier: Modifier = Modifier, onClicked: () -> Unit = {}
) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = borderStroke,
            color = color
        ),
        modifier = Modifier
            .padding(16.dp)
            .then(modifier)
    ) {
        Image(
            painter = rememberImagePainter(
                data = if (user.photoUrl.isNullOrEmpty())
                    "https://images.unsplash.com/photo-1485290334039-a3c69043e517?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80"
                else user.photoUrl,
                builder = {
                    transformations(CircleCropTransformation())
                },
            ),
            modifier = Modifier
                .size(size)
                .clickable { onClicked.invoke() },
            contentScale = ContentScale.Crop,
            contentDescription = ""
        )
    }
}

@Composable
fun ProfileContent(
    user: User,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    tStyle: TextStyle = MaterialTheme.typography.headlineMedium
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(0.8f),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = user.userName!!,
            style = tStyle,
            maxLines = 1,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Active now",
            style = MaterialTheme.typography.bodySmall,
            color = shadeGreen,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTheme {
        ProfileCard(User(userName = "Yoooo Bro"), {}) {}
    }
}