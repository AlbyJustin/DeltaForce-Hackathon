package com.example.dpass.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.g00fy2.quickie.QRResult.QRError
import io.github.g00fy2.quickie.QRResult.QRMissingPermission
import io.github.g00fy2.quickie.QRResult.QRSuccess
import io.github.g00fy2.quickie.QRResult.QRUserCanceled
import io.github.g00fy2.quickie.ScanQRCode
import kotlinx.coroutines.launch

@Composable
fun QrCodeScreen(modifier: Modifier = Modifier) {
    var text: String? by remember { mutableStateOf("") }

    val scanQrCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        text = when (result) {
            is QRSuccess -> {
                result.content.rawValue
            }
            QRUserCanceled -> "User canceled"

            QRMissingPermission -> "Missing permission"
            is QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"

        }
    }

    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { scanQrCodeLauncher.launch(null) }) {
            Text(text = "Scan QR Code")
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = text.toString(),
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            fontSize = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}