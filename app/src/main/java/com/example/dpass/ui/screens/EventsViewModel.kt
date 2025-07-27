package com.example.dpass.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dpass.network.EventRequest
import com.example.dpass.network.EventResponse
import com.example.dpass.network.RSVPRequest
import com.example.dpass.network.RetrofitEventsClient
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    var eventState by mutableStateOf<EventResponse?>(null)
    var eventStates by mutableStateOf<List<EventResponse>?>(null)
    var message by mutableStateOf<String?>(null)
    var error by mutableStateOf<String?>(null)

    fun getEvents() {
        viewModelScope.launch {
            try {
                val response = RetrofitEventsClient.apiService.getEvents()
                if (response.isSuccessful) {
                    eventStates = response.body()?.events
                } else {
                    message = "Failed to fetch events"}
            } catch (e: Exception) {
                message = "Error: ${e.message}"}
        }
    }


    fun createEvent(name: String, description: String, max_seats: Int, deadline: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitEventsClient.apiService.createEvent(
                    EventRequest(
                        name,
                        description,
                        max_seats,
                        deadline
                    )
                )
                message = response.body()?.message
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            }
        }
    }

    fun loadEvent(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitEventsClient.apiService.getEvent(id)
                if (response.isSuccessful) {
                    eventState = response.body()
                } else {
                    message = "Failed to fetch event"
                }
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            }
        }
    }

    fun rsvp(eventId: Int, email: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitEventsClient.apiService.rsvp(eventId, RSVPRequest(email))
                message = response.body()?.message
                error = response.body()?.error
                loadEvent(eventId)
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            }
        }
    }

    fun cancel(eventId: Int, email: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitEventsClient.apiService.cancelRsvp(eventId, RSVPRequest(email))
                message = response.body()?.message
                error = response.body()?.error
                loadEvent(eventId)
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            }
        }
    }
}