package com.developerspace.webrtcsample.compose.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.developerspace.webrtcsample.ChatMainActivity
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.theming.lightGreen
import com.developerspace.webrtcsample.compose.ui.viewmodel.ActiveUserViewModel
import com.developerspace.webrtcsample.model.User

@Composable
fun ActiveUsersScreen(viewModel: ActiveUserViewModel = viewModel()) {
    val context  = LocalContext.current
    val userListState by viewModel.userListState.collectAsState()
    val navController = rememberNavController()
    Scaffold(topBar = { AppBar {
        navController.navigateUp()
    } }) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn {
                items(userListState) {
                    ProfileCard(it,
                        onCLickMessage =  {
                            // Go to chat screen
                            val intent = Intent(context, ChatMainActivity::class.java)
                            intent.putExtra("receiverUserID", it.userID)
                            context.startActivity(intent)
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(onBackKeyClicked: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "",
                modifier = Modifier.padding(horizontal = 12.dp).clickable {
                    onBackKeyClicked.invoke()
                },
            )
        },
        title = { Text("Active users") }
    )
}

@Composable
fun ProfileCard(user: User, onCLickMessage: () -> Unit) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .border(2.dp, lightGreen, RoundedCornerShape(15.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(user)
            ProfileContent(user)
            Spacer(Modifier.weight(1f))
            MessageSection(onCLickMessage)
        }
    }
}

@Composable
fun MessageSection(onCLickMessage: () -> Unit) {
    Image(painter = painterResource(id = R.drawable.baseline_textsms_24),
        contentDescription = "",
        modifier = Modifier.width(50.dp).height(50.dp).clickable {
            onCLickMessage.invoke()
        }.padding(end = 10.dp),
        alignment = Alignment.CenterEnd)
}

@Composable
fun ProfilePicture(user: User) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = lightGreen
        ),
        modifier = Modifier.padding(16.dp),
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
            modifier = Modifier.size(72.dp),
            contentScale = ContentScale.Crop,
            contentDescription = ""
        )
    }
}

@Composable
fun ProfileContent(user: User) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentWidth()
    ) {
        Text(
            text = user.userName!!,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Active now",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTheme {
        ProfileCard(User(userName = "Yoooo Bro")) {}
    }
}