package com.restaurant.reservation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.restaurant.reservation.model.TableSelectionData
import com.restaurant.reservation.ui.theme.PrimaryBlue
import com.restaurant.reservation.ui.theme.RestaurantReservationTheme
import com.restaurant.reservation.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableSelectionScreen(viewModel: AppViewModel) {
    var step by remember { mutableStateOf(1) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var peopleCount by remember { mutableStateOf(2) }
    var selectedTable by remember { mutableStateOf<Int?>(null) }
    val tables = listOf(
        1 to 2, 2 to 4, 3 to 2, 4 to 6, 5 to 4, 6 to 8, 7 to 2, 8 to 4
    )
    val availableTables = tables.filter { it.second >= peopleCount }

    val canContinue = when (step) {
        1 -> selectedDate.isNotBlank()
        2 -> selectedTime.isNotBlank()
        3 -> peopleCount > 0
        4 -> selectedTable != null
        else -> false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Reservasi Baru") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step > 1) step-- else viewModel.navigateBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    when (step) {
                        1 -> {
                            Text(
                                text = "Pilih Tanggal",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Implement Date Picker here
                        }
                        2 -> {
                            Text(
                                text = "Pilih Jam",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Implement Time Picker here
                        }
                        3 -> {
                            Text(
                                text = "Jumlah Orang",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Implement People Count selection here
                        }
                        4 -> {
                            Text(
                                text = "Pilih Meja",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                availableTables.forEach { (id, seats) ->
                                    TableCard(
                                        id = id,
                                        seats = seats,
                                        isSelected = selectedTable == id,
                                        onClick = { selectedTable = id }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (step < 4) {
                        step++
                    } else {
                        viewModel.navigateToReservationDetails(
                            TableSelectionData(
                                date = selectedDate,
                                time = selectedTime,
                                people = peopleCount,
                                table = selectedTable
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canContinue,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(text = if (step < 4) "Selanjutnya" else "Lanjutkan")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableCard(id: Int, seats: Int, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Meja #$id",
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$seats kursi",
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}