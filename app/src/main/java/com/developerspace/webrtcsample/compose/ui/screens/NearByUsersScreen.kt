package com.developerspace.webrtcsample.compose.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.developerspace.webrtcsample.compose.data.model.User
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.viewmodel.NearByUsersViewModel
import com.developerspace.webrtcsample.legacy.ChatMainActivity

@Composable
fun NearByUsersScreen(navController: NavController? = null) {
    val viewModel: NearByUsersViewModel = hiltViewModel()
    val nearByUsers by viewModel.nearByUserListState.collectAsState()
    val isProgressShow by viewModel.isProgressLoading.collectAsState()

    if (isProgressShow) {
        CircularProgressIndicator(
            modifier = Modifier
                .wrapContentSize()
                .size(80.dp)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(nearByUsers) {
            ProfileCardWithDistance(it, navController)
        }
    }
}

@Composable
fun ProfileCardWithDistance(nearByUser: Pair<User, Long>, navController: NavController? = null) {
    Row {
        val user = nearByUser.first
        val context = LocalContext.current
        Text(
            "${nearByUser.second} Km",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.2f)
                .align(Alignment.CenterVertically)
                .padding(4.dp)
        )
        ProfileCardForNearByUser(user = user,
            Modifier.weight(0.8f),
            onClickCard = {
                navController?.navigate("user_detail_screen/${user.userID}")
            },
            onCLickMessage = {
                // Go to chat screen
                val intent = Intent(context, ChatMainActivity::class.java)
                intent.putExtra("receiverUserID", user.userID)
                intent.putExtra("receiverUserName", user.userName)
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun ProfileCardForNearByUser(
    user: User,
    modifier: Modifier,
    onClickCard: () -> Unit,
    onCLickMessage: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp, 2.dp, 2.dp, 2.dp),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
            .then(modifier)
            .border(1.dp, Color.Blue, RoundedCornerShape(15.dp))
            .clickable { onClickCard.invoke() }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            ProfilePicture(user) {
                onClickCard.invoke()
            }
            ProfileContent(user, tStyle = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.weight(1f))
            MessageSection(onCLickMessage)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewNearBy() {
    MyTheme {
        ProfileCardWithDistance(User(userName = "Tania Sultana") to 5L)
    }
}