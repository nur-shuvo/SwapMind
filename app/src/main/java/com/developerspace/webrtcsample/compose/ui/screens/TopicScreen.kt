package com.developerspace.webrtcsample.compose.ui.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.theming.shapes
import com.developerspace.webrtcsample.compose.ui.util.Topic
import com.developerspace.webrtcsample.compose.ui.util.staticTopicList
import com.developerspace.webrtcsample.compose.ui.viewmodel.TopicScreenViewModel
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun TopicScreen(topic: Topic, navController: NavController? = null) {
    val viewModel: TopicScreenViewModel = hiltViewModel()
    val userListState by viewModel.userListState.collectAsState()
    val context = LocalContext.current as AppCompatActivity
    viewModel.startGeneratingActiveUserIfNeeded(topic)
    Scaffold(
        topBar = {
            AppBarWithBack(topic.topicTitle) {
                navController?.navigateUp()
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(12.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize()
            ) {
                item {
                    HeaderText()
                }
                item {
                    ImageContent(topic = topic)
                }
                item {
                    QuoteText(topic.quoteText,  topic.extraQuoteText)
                }
                item {
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp))
                }
                item {
                    ActiveUserText()
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
}

@Composable
fun ImageContent(modifier: Modifier = Modifier, topic: Topic = Topic()) {
    Card(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .height(150.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Image(
            painterResource(id = topic.drawableID),
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally),
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun HeaderText(modifier: Modifier = Modifier) {
    Text(
        "Stay on the page to broadcast other users that you're currently interested to this topic",
        style = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp
        )
    )
}

@Composable
fun QuoteText(contentText1: String, contentText2: String,  modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }
    val alpha by animateFloatAsState(
        targetValue = if(visible) 1f else 0f,
        animationSpec = tween(2000, easing = LinearEasing),
        label = ""
    )
    LaunchedEffect(Unit) {
        while (true) {
            delay(2.seconds)
            visible = !visible
        }
    }

    Crossfade(targetState = visible, label = "") {
        if (it) {
            TextWithFade(contentText1, alpha)
        } else {
            TextWithFade(contentText2, alpha)
        }
    }
}

@Composable
fun TextWithFade(contentText: String, alpha: Float) {
    Text(
        text = contentText,
        modifier = Modifier.padding(horizontal = 15.dp),
        style = TextStyle(
            textAlign = TextAlign.Justify,
            color = Color.Black.copy(alpha = alpha),
            fontStyle = FontStyle.Italic
        )
    )
}

@Composable
fun ActiveUserText(modifier: Modifier = Modifier) {
    Text(
        "Start conversation who are interested to this topic now!",
        style = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color.Black,
            fontFamily = FontFamily.Serif
        ),
        modifier = Modifier.padding(10.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun TopicScreen() {
    MyTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {

        }
    }
}