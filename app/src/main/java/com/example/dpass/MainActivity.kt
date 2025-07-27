package com.example.dpass

import android.os.Bundle
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
import com.example.dpass.ui.screens.dPassViewModel
import com.example.dpass.ui.theme.DPassTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dpass.ui.screens.CreateEventScreen
import com.example.dpass.ui.screens.EventListScreen
import com.example.dpass.ui.screens.EventScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DPassTheme {
                MainApp()
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

