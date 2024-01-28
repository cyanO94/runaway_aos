package com.example.runaway_aos.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
            if (webViewUrl.isEmpty()) UrlSetScreen(changeView = { url -> webViewUrl = url})
            else WebViewScreen(webViewUrl)
        }
    }
}

@Composable
fun UrlSetScreen(
    changeView: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )

    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        permissionsMap.values.reduce { acc, next -> acc && next }
    }

    var url by remember { mutableStateOf("")}
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                modifier = Modifier.width(200.dp),
                value = url,
                onValueChange = {url = it},
                label = { Text(text = "input IP") }
            )

            Button(
                onClick = {
                    changeView(
                        if (url.contains(":")) url
                        else "$url:9002"
                    )
                }
            ) {
                Text("입력")
            }
        }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = {changeView("http://223.130.147.208/")}) {
                Text("배포 서버")
            }
            Button(
                onClick = {changeView("http://192.168.0.7:9002/")}) {
                Text("집 로컬")
            }

            Button(
                onClick = {
                    checkAndRequestPermissions(
                        context,
                        permissions,
                        launcherMultiplePermissions
                    )
                }
            ) {
                Text("권한")
            }
        }
    }
}

fun checkAndRequestPermissions(
    context: Context,
    permissions: Array<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
) {
    if (!permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    ) {
        launcher.launch(permissions)
    }
}

@Preview(showBackground = true)
@Composable
fun RunawayAppPreview() {
    RunawayApp()
}