package com.example.dpass.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EventScreen(viewModel: EventViewModel = viewModel(), onEventListClick: () -> Unit) {
    var eventId by remember { mutableStateOf(1) }
    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .statusBarsPadding()
        .padding(horizontal = 40.dp)
        .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = eventId.toString(),
            onValueChange = { eventId = it.toIntOrNull() ?: 1 },
            label = { Text("Event ID") }
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Row(Modifier.padding(vertical = 8.dp)) {
            Button(onClick = { viewModel.loadEvent(eventId) }) {
                Text("Load Event")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { viewModel.rsvp(eventId, email) }) {
                Text("RSVP")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { viewModel.cancel(eventId, email) }) {
                Text("Cancel")
            }
        }

        viewModel.message?.let {
            Text(it, color = Color.Red)
        }

        viewModel.error?.let {
            Text(it, color = Color.Red)
        }

        viewModel.eventState?.let { event ->
            Text("Event: ${event.name}", style = MaterialTheme.typography.titleMedium)
            Text("Seats: ${event.seats_filled}/${event.max_seats}")
            Text("Deadline: ${event.deadline}")
            Text("Registrations:")
            event.registrations.forEach {
                Text("- ${it.email} [${it.status}] at ${it.time}")
            }
        }
        Button(onClick = onEventListClick) {
            Text("Event List")
        }
    }

}

@Composable
fun EventListScreen(
    viewModel: EventViewModel = viewModel(),
    onEventClick: () -> Unit,
    onCreateEventClick: () -> Unit
) {
    viewModel.getEvents()
    val events = viewModel.eventStates ?: emptyList()

    Column {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(events) { event ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Text("Event name : ${event.name}")
                    Text("Event Description : ${event.description}")
                    Text("Event Deadline : ${event.deadline}")
                    Text("Event Max Seats : ${event.max_seats}")
                    Text("Event Seats Filled : ${event.seats_filled}")
                }
            }
        }
        Button(onClick = onCreateEventClick) {
            Text("Create Event")
        }
        Button(onClick = onEventClick) {
            Text("Make RSVP")
        }
    }
}

@Composable
fun CreateEventScreen(viewModel: EventViewModel = viewModel(), onEventListClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var maxSeats by remember { mutableStateOf(0) }
    var deadline by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .statusBarsPadding()
        .padding(horizontal = 40.dp)
        .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        OutlinedTextField(
            value = maxSeats.toString(),
            onValueChange = { maxSeats = it.toIntOrNull() ?: 0 },
            label = { Text("Deadline") }
        )
        OutlinedTextField(
            value = deadline,
            onValueChange = { deadline = it },
            label = { Text("Deadline") }
        )

        Button(onClick = { viewModel.createEvent(name, description, maxSeats, deadline) }) {
            Text("Create Event")
        }

        viewModel.message?.let {
            Text(it, color = Color.Red)
        }

        viewModel.error?.let {
            Text(it, color = Color.Red)
        }

        Button(onClick = onEventListClick) {
            Text("Event List")
        }

    }


}