package com.example.dpass

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dpass.ui.screens.dPassViewModel
import com.example.dpass.ui.theme.DPassTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dpass.network.AuthorizationResponse
import com.example.dpass.ui.screens.QrCodeScreen
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DPassTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: dPassViewModel = viewModel()) {

    val context = LocalContext.current


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(text = "Login")
        Button(onClick = {
            val authUrl = "https://auth.delta.nitt.edu/authorize".toUri()
                .buildUpon()
                .appendQueryParameter("client_id", "gmwjj~XN59YFU63t")
                .appendQueryParameter("redirect_uri", "dpass://callback")
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("scope", "email")
                .appendQueryParameter("state", "state1234")
                .appendQueryParameter("nonce", "nonce5678")
                .build()

            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(context,authUrl)

        }) {
            Text(text = "Authorize")
        }
    }
}

