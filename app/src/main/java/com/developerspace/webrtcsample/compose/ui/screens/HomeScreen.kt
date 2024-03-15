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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.data.model.RemoteStory
import com.developerspace.webrtcsample.compose.data.model.StoryDetailViewData
import com.developerspace.webrtcsample.compose.data.model.User
import com.developerspace.webrtcsample.compose.data.repository.MonitorSelectedStoryRepository
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.util.Topic
import com.developerspace.webrtcsample.compose.ui.util.staticTopicList
import com.developerspace.webrtcsample.compose.ui.viewmodel.HomeScreenViewModel
import com.developerspace.webrtcsample.compose.util.misc.MyOpenDocumentContract
import com.google.gson.Gson

@Composable
fun HomeScreen(navController: NavController? = null) {
    val activity = LocalContext.current as AppCompatActivity
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val remoteStories = viewModel.remoteStoryList.collectAsState()
    val userMap = viewModel.userMap.collectAsState()
    val isProgressShow by viewModel.isProgressShow.collectAsState()

    val openDocument = rememberLauncherForActivityResult(contract = MyOpenDocumentContract(),
        onResult = {
            if (it != null) {
                viewModel.onAddStoryImageSelected(activity, it)
            }
        })

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (isProgressShow) {
            CircularProgressIndicator(
                modifier = Modifier
                    .wrapContentSize()
                    .size(80.dp)
            )
        }
        if (isProgressShow.not()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                item {
                    DividerText(text = "Top stories")
                    ListOfStory(viewModel, userMap.value, remoteStories.value, navController) {
                        // Add story clicked
                        openDocument.launch(arrayOf("image/*"))
                    }
                    DividerText(text = "Swap your mind with online users based on topics")
                }
                var index = 0
                items(staticTopicList.size / 3) {
                    CategoryCardTuple(index++, index++, index++, navController)
                    Divider(modifier = Modifier.fillMaxWidth(), 2.dp)
                }
            }
        }
    }
}

@Composable
fun ListOfStory(
    viewModel: HomeScreenViewModel,
    userMap: Map<String, User>,
    remoteStoryList: List<RemoteStory>,
    navController: NavController?,
    onClickCard: () -> Unit = {}
) {
    LazyRow {
        item {
            AddStoryCard(onClickCard)
        }
        items(remoteStoryList) {
            StoryCard(userMap, it) { (story, user) ->
                viewModel.updateSelectedStory(user, story)
                navController?.navigate("story_detail_view")
            }
        }
    }
}

@Composable
fun AddStoryCard(onClickCard: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .width(130.dp)
            .height(180.dp)
            .padding(2.dp)
            .border(2.dp, Color.Blue, RoundedCornerShape(15.dp))
            .clickable { onClickCard.invoke() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularButton()
            Text(
                text = "Add story",
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StoryCard(userMap: Map<String, User>, remoteStory: RemoteStory, onClickCard: (Pair<RemoteStory, User>) -> Unit) {
    val user = userMap[remoteStory.userID!!] ?: User()
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .width(130.dp)
            .height(180.dp)
            .padding(2.dp)
            .border(2.dp, Color.Blue, RoundedCornerShape(15.dp))
            .clickable { onClickCard.invoke(remoteStory to user) }
    ) {
        Box {
            Image(
                painter = rememberImagePainter(
                    data = remoteStory.storyUrl,
                    builder = {
                        transformations(RoundedCornersTransformation())
                    },
                ),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
            Text(
                text = user.userName!!,
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp)
            )
            ProfilePicture(
                user = user,
                size = 20.dp,
                modifier = Modifier.align(Alignment.TopStart),
                borderStroke = 0.5.dp
            )
        }
    }
}

@Composable
fun CategoryCardTuple(first: Int, second: Int, third: Int, navController: NavController?) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val gson = Gson()
        TopicCard(topic = staticTopicList[first]) {
            val json = gson.toJson(staticTopicList[first])
            navController?.navigate("topic_screen/${json}")
        }
        TopicCard(topic = staticTopicList[second]) {
            val json = gson.toJson(staticTopicList[second])
            navController?.navigate("topic_screen/${json}")
        }
        TopicCard(topic = staticTopicList[third]) {
            val json = gson.toJson(staticTopicList[third])
            navController?.navigate("topic_screen/${json}")
        }
    }
}

@Composable
fun TopicCard(color: Color = Color.White, topic: Topic = Topic(), onClickCard: () -> Unit) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .width(120.dp)
            .height(140.dp)
            .padding(2.dp)
            .border(2.dp, Color.Blue, RoundedCornerShape(15.dp))
            .clickable {
                onClickCard.invoke()
            },
        colors = CardDefaults.cardColors(
            containerColor = color,
        )
    ) {
        Column {
            Image(
                painterResource(id = topic.drawableID),
                modifier = Modifier
                    .width(120.dp)
                    .height(100.dp),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
                    .padding(bottom = 1.5.dp),
                text = topic.topicTitle,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DividerText(text: String) {
    Text(
        text,
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(),
        style = TextStyle(
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun CircularButton() {
    Image(
        painterResource(id = R.drawable.ic_action_add_circle_outline),
        contentScale = ContentScale.FillBounds,
        contentDescription = "",
        modifier = Modifier.size(30.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewHome() {
    MyTheme {
        DividerText("Swap your mind with online users based on topics")
    }
}