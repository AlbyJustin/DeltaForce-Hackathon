package com.example.dpassword.ui.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dpassword.network.DAuthApiClients
import com.example.dpassword.network.UserInfoResponse
import kotlinx.coroutines.launch

class dPassViewModel: ViewModel() {

    suspend fun getToken(code: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = DAuthApiClients.apiService.getToken(
                    clientId = "gmwjj~XN59YFU63t",
                    clientSecret = "nPr3ZQnbsD0szjSfpxivAU2I51YUyFbj",
                    code = code,
                    redirectUri = "dpass://callback"
                )

                val token = response.access_token

                Log.d("DAuth", "Token: $token")



            } catch (e: Exception) {
                Log.e("DAuth", "Token exchange failed", e)
            }
        }
    }

    suspend fun getUserInfo(accessToken: String): UserInfoResponse {
        try {
            return DAuthApiClients.apiService.getUserInfo(accessToken)
        } catch (e: Exception) {
            throw e
        }
    }

}