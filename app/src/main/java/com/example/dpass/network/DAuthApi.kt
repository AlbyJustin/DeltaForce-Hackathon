package com.example.dpass.network;

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface DAuthApi {
    @FormUrlEncoded
    @POST("/api/oauth/token")
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): TokenResponse

    @POST("/api/resources/user")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String
    ): UserInfoResponse
}

object DAuthApiClients {
    val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    val client = OkHttpClient.Builder().addInterceptor(logger).build()

    val apiService: DAuthApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://auth.delta.nitt.edu")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DAuthApi::class.java)
    }
}

data class AuthorizationResponse(
    val state: String,
    val code: String
)

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val scope: String
)

data class UserInfoResponse(
    val id: String,
    val name: String,
    val email: String
)
