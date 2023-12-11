package com.developerspace.webrtcsample.compose.ui.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.developerspace.webrtcsample.compose.ui.theming.MyTheme
import com.developerspace.webrtcsample.compose.ui.viewmodel.AccountProfileEditViewModel

@Composable
fun AccountProfileEditScreen(type: String, navController: NavController? = null) {
    val viewModel: AccountProfileEditViewModel = viewModel()
    val activity = LocalContext.current as AppCompatActivity
    if (type == "name") {
        Name("name", navController) { newName ->
            viewModel.updateCurrentUserProfileName(activity, newName)
        }
    } else if (type == "about") {

    }
}

@Composable
fun Name(type: String, navController: NavController? = null, onClicked: (newName: String) -> Unit) {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text1,
                    onValueChange = { text1 = it },
                    label = { Text("Enter your first $type") }
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text2,
                    onValueChange = { text2 = it },
                    label = { Text("Enter your last $type") }
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Button(onClick = {
                    onClicked.invoke("$text1 $text2")
                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Done")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewAccountProfileEditScreen() {
    MyTheme {
        AccountProfileEditScreen("name")
    }
}