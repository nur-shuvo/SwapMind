package com.developerspace.webrtcsample.compose.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.theming.lightGreen
import com.developerspace.webrtcsample.compose.ui.viewmodel.HomeScreenViewModel
import com.developerspace.webrtcsample.model.User

@Composable
fun HomeScreen() {
    val viewmodel: HomeScreenViewModel = viewModel()
    // viewmodel.countState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        DividerText(text = "Top stories")
        ListOfStory()
        DividerText(text = "Top topics that you want to discuss")
        ListOfTopCategoryCards()
    }
}

@Composable
fun ListOfStory() {
    LazyRow {
        items(10) {
            StoryCard {}
        }
    }
}

@Composable
fun StoryCard(onClickCard: () -> Unit) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .width(100.dp)
            .height(180.dp)
            .padding(2.dp)
            .border(2.dp, lightGreen, RoundedCornerShape(15.dp))
            .clickable { onClickCard.invoke() }
    ) {
        Box {
            Image(
                painter = rememberImagePainter(
                    data = "https://images.unsplash.com/photo-1485290334039-a3c69043e517?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    builder = {
                        transformations(RoundedCornersTransformation())
                    },
                ),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
            Text(
                "My name",
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp)
            )
            ProfilePicture(
                user = User(),
                size = 20.dp,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
    }
}

@Composable
fun ListOfTopCategoryCards() {
    LazyColumn {
        items(6) {
            CategoryCardTuple()
            Divider(modifier = Modifier.fillMaxWidth(), 2.dp)
        }
    }
}

@Composable
fun CategoryCardTuple() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TopicCard()
        TopicCard()
        TopicCard()
    }
}

@Composable
fun TopicCard(color: Color = Color.White) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .width(100.dp)
            .height(140.dp)
            .padding(2.dp)
            .border(2.dp, lightGreen, RoundedCornerShape(15.dp)),
        colors = CardDefaults.cardColors(
            containerColor = color,
        )
    ) {
        Column {
            Image(painterResource(id = R.drawable.childhood),
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
            Text(modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
                text = "Childhood",
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DividerText(text: String) {
    Text(text,
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth()
            .wrapContentWidth(),
        style = TextStyle(
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewHome() {
    MyTheme {
        HomeScreen()
    }
}