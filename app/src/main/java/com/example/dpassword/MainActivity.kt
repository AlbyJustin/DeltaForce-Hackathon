package com.example.dpassword

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dpassword.ui.screens.dPassViewModel
import com.example.dpassword.ui.theme.DPassTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dpassword.network.DAuthApiClients
import com.example.dpassword.ui.screens.CreateEventScreen
import com.example.dpassword.ui.screens.EventListScreen
import com.example.dpassword.ui.screens.EventScreen
import com.example.dpassword.ui.screens.QrCodeScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val uri: Uri? = intent?.data
        if (uri != null && uri.toString().startsWith("dpass://callback")) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                Log.d("DAuth", "Authorization code: $code")
                getAccessToken(code)
            }
        }
        else {
            Log.d("DAuth", "No authorization code found in the URI")
        }
        setContent {
            DPassTheme {
                MainApp()
            }
        }
    }
    private fun getAccessToken(code: String) {
        lifecycleScope.launch {
            try {
                val response = DAuthApiClients.apiService.getToken(
                    clientId = "gmwjj~XN59YFU63t",
                    clientSecret = "nPr3ZQnbsD0szjSfpxivAU2I51YUyFbj",
                    code = code,
                    redirectUri = "dpass://callback"
                )

                val token = response.access_token
                Log.d("DAuth", "Access Token: $token")

                /*getSharedPreferences("auth", MODE_PRIVATE).edit()
                    .putString("access_token", token)
                    .apply()*/

            } catch (e: Exception) {
                Log.e("DAuth", "Error getting token", e)
            }
        }
    }
}

enum class Screens {
    Login,
    EventList,
    Event,
    CreateEvent,
    QrCode
}



@Composable
fun MainApp(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screens.EventList.name,
        modifier = Modifier.padding(8.dp)
    ) {
        composable(Screens.Login.name) {
            LoginScreen()
        }
        composable(Screens.EventList.name) {
            EventListScreen(
                onEventClick = {
                    navController.navigate(Screens.Event.name)
                },
                onCreateEventClick = {
                    navController.navigate(Screens.CreateEvent.name)
                },
                onQRScanClick = {
                    navController.navigate(Screens.QrCode.name)
                }
            )
        }
        composable(Screens.Event.name) {
            EventScreen(
                onEventListClick = {
                    navController.navigate(Screens.EventList.name)
                }

            )
        }
        composable(Screens.CreateEvent.name) {
            CreateEventScreen(
                onEventListClick = {
                    navController.navigate(Screens.EventList.name)
                }
            )
        }
        composable(Screens.QrCode.name) {
            QrCodeScreen(
                onEventListClick = {
                    navController.navigate(Screens.EventList.name)
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: dPassViewModel = viewModel(),
) {

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

