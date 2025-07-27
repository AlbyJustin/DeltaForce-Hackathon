package com.example.dpassword.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventsApi {
    @GET("/events/{event_id}")
    suspend fun getEvent(@Path("event_id") eventId: Int): Response<EventResponse>

    @GET("/events")
    suspend fun getEvents(): Response<AllEventsResponse>

    @POST("/events")
    suspend fun createEvent(@Body event: EventRequest): Response<CreateEventResponse>

    @POST("/rsvp/{event_id}")
    suspend fun rsvp(@Path("event_id") eventId: Int, @Body body: RSVPRequest): Response<NormalResponse>

    @POST("/cancel/{event_id}")
    suspend fun cancelRsvp(@Path("event_id") eventId: Int, @Body body: RSVPRequest): Response<NormalResponse>
}

object RetrofitEventsClient {
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    val apiService: EventsApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(EventsApi::class.java)
    }
}

data class EventRequest(
    val name: String,
    val description: String,
    val max_seats: Int,
    val deadline: String
)

data class CreateEventResponse(
    val message: String,
    val id: Int
)

data class AllEventsResponse(
    val events: List<EventResponse>
)

data class RSVPRequest(
    val email: String
)

data class NormalResponse(
    val message: String,
    val status: String? = null,
    val error: String? = null
)

data class EventResponse(
    val name: String,
    val description: String,
    val max_seats: Int,
    val seats_filled: Int,
    val deadline: String,
    val registrations: List<RSVPEntry>
)

data class RSVPEntry(
    val email: String,
    val status: String,
    val time: String
)