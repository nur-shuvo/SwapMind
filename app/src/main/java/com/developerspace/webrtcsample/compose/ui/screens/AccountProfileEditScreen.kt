package com.developerspace.webrtcsample.compose.ui.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.util.EditTextView
import com.developerspace.webrtcsample.compose.ui.viewmodel.AccountProfileEditViewModel

@Composable
fun AccountProfileEditScreen(type: String, navController: NavController? = null) {
    val viewModel: AccountProfileEditViewModel = viewModel()
    val activity = LocalContext.current as AppCompatActivity
    val isProgressShow by viewModel.isProgressLoading.collectAsState()
    if (type == "name") {
        Name("name", navController, isProgressShow) { newName ->
            viewModel.updateCurrentUserProfileName(activity, newName) {
                navController?.navigateUp()
            }
        }
    } else if (type == "about") {
        About("about", navController, isProgressShow) {
            // update about
            navController?.navigateUp()
        }
    }
}

@Composable
fun Name(
    type: String,
    navController: NavController? = null,
    isProgressShow: Boolean = false,
    onClicked: (newName: String) -> Unit
) {
    Scaffold(topBar = {
        AppBarWithBack(text = "Edit your $type") {
            navController?.navigateUp()
        }
    }) { innerPadding ->
        var text1 by remember { mutableStateOf("") }
        var text2 by remember { mutableStateOf("") }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    EditTextView(text1, "First Name", "Enter your first $type") {
                        text1 = it
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    EditTextView(text2, "Last Name", "Enter your last $type") {
                        text2 = it
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    Button(onClick = {
                        onClicked.invoke("$text1 $text2")
                    }, modifier = Modifier.align(Alignment.End)) {
                        Text("Done")
                    }
                }
                if (isProgressShow) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                            .size(80.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun About(
    type: String,
    navController: NavController? = null,
    isProgressShow: Boolean = false,
    onClicked: (newAbout: String) -> Unit
) {
    Scaffold(topBar = {
        AppBarWithBack(text = "Edit your $type") {
            navController?.navigateUp()
        }
    }) { innerPadding ->
        var text by remember { mutableStateOf("") }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    EditTextView(text, "About", "Update your status $type") {
                        text = it
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    Button(onClick = {
                        onClicked.invoke(text)
                    }, modifier = Modifier.align(Alignment.End)) {
                        Text("Done")
                    }
                }
                if (isProgressShow) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                            .size(80.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewAccountProfileEditScreen() {
    MyTheme {
        EditTextView()
    }
}