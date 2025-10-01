package com.restaurant.reservation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.restaurant.reservation.model.Reservation
import com.restaurant.reservation.model.ReservationStatus
import com.restaurant.reservation.ui.theme.RestaurantReservationTheme
import com.restaurant.reservation.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationHistoryScreen(viewModel: AppViewModel) {
    val reservations by viewModel.reservationHistory.collectAsState()
    var selectedFilter by remember { mutableStateOf("Semua") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = "Riwayat Reservasi",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Lihat semua reservasi Anda",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Filter tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "Semua",
                onClick = { selectedFilter = "Semua" },
                label = { Text("Semua") }
            )
            FilterChip(
                selected = selectedFilter == "Dikonfirmasi",
                onClick = { selectedFilter = "Dikonfirmasi" },
                label = { Text("Dikonfirmasi") }
            )
            FilterChip(
                selected = selectedFilter == "Menunggu",
                onClick = { selectedFilter = "Menunggu" },
                label = { Text("Menunggu") }
            )
            FilterChip(
                selected = selectedFilter == "Dibatalkan",
                onClick = { selectedFilter = "Dibatalkan" },
                label = { Text("Dibatalkan") }
            )
        }

        if (reservations.isEmpty()) {
            EmptyState(
                text = "Belum ada riwayat",
                subText = "Anda belum memiliki reservasi apapun. Mulai pesan meja sekarang!"
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredReservations = when (selectedFilter) {
                    "Dikonfirmasi" -> reservations.filter { it.status == ReservationStatus.CONFIRMED }
                    "Menunggu" -> reservations.filter { it.status == ReservationStatus.PENDING }
                    "Dibatalkan" -> reservations.filter { it.status == ReservationStatus.CANCELLED }
                    else -> reservations
                }
                items(filteredReservations) { reservation ->
                    ReservationItemCard(reservation = reservation)
                }
            }
        }
    }
}

@Composable
fun ReservationItemCard(reservation: Reservation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Meja #${reservation.table}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = "Status: ${reservation.status}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${formatDate(reservation.date)} • ${reservation.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${reservation.people} orang",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: return dateString)
    } catch (e: Exception) {
        dateString
    }
}

@Preview(showBackground = true)
@Composable
fun ReservationHistoryScreenPreview() {
    RestaurantReservationTheme {
        ReservationHistoryScreen(viewModel = AppViewModel())
    }
}