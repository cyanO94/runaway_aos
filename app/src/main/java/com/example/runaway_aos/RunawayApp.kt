package com.example.runaway_aos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.runaway_aos.ui.theme.Runaway_aosTheme

@Composable
fun RunawayApp() {
    Runaway_aosTheme {
        var webViewUrl by rememberSaveable {
            mutableStateOf("")
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (webViewUrl.isEmpty()) SetIpScreen(changeView = { url -> webViewUrl = url})
            else {
                WebViewScreen(webViewUrl)
            }
        }
    }
}

@Composable
fun SetIpScreen(
    changeView: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var url by remember { mutableStateOf("")}
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = url,
                onValueChange = {url = it},
                label = { Text(text = "Input IP") }
            )

            Button(
                onClick = {changeView(url)}) {
                Text("입력")
            }
        }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(10.dp),

        ) {
            Button(
                onClick = {changeView("http://223.130.147.208/")}) {
                Text("배포")
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun RunawayAppPreview() {
    RunawayApp()
}