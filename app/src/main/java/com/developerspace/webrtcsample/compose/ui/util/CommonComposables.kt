package com.developerspace.webrtcsample.compose.ui.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.developerspace.webrtcsample.R

@Composable
fun EditTextView(
    defaultText: String = "",
    label: String = "",
    placeholder: String = "",
    onChangeText: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf(defaultText) }

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            onChangeText.invoke(newText)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_account_circle_black_36dp),
                contentDescription = null,
                tint = Color.Gray
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.Black
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        )
    )
}