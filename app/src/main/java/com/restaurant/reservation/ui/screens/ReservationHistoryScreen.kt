package com.restaurant.reservation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.restaurant.reservation.R
import com.restaurant.reservation.model.Reservation
import com.restaurant.reservation.model.ReservationStatus
import com.restaurant.reservation.ui.theme.DangerRed
import com.restaurant.reservation.ui.theme.PrimaryBlue
import com.restaurant.reservation.ui.theme.SecondaryOrange
import com.restaurant.reservation.ui.theme.SuccessGreen
import com.restaurant.reservation.viewmodel.AppViewModel
import com.restaurant.reservation.util.formatDate
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationHistoryScreen(viewModel: AppViewModel = viewModel()) {
    val reservations by viewModel.reservationHistory.collectAsState()
    var selectedFilter by remember { mutableStateOf(R.string.filter_all) }

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
                text = stringResource(id = R.string.reservation_history),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = stringResource(id = R.string.reservation_history_subtitle),
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
                selected = selectedFilter == R.string.filter_all,
                onClick = { selectedFilter = R.string.filter_all },
                label = { Text(stringResource(id = R.string.filter_all)) }
            )
            FilterChip(
                selected = selectedFilter == R.string.filter_confirmed,
                onClick = { selectedFilter = R.string.filter_confirmed },
                label = { Text(stringResource(id = R.string.filter_confirmed)) }
            )
            FilterChip(
                selected = selectedFilter == R.string.filter_pending,
                onClick = { selectedFilter = R.string.filter_pending },
                label = { Text(stringResource(id = R.string.filter_pending)) }
            )
            FilterChip(
                selected = selectedFilter == R.string.filter_cancelled,
                onClick = { selectedFilter = R.string.filter_cancelled },
                label = { Text(stringResource(id = R.string.filter_cancelled)) }
            )
        }

        if (reservations.isEmpty()) {
            EmptyState(
                text = stringResource(id = R.string.no_reservation_history),
                subText = stringResource(id = R.string.no_reservation_history_subtitle),
                icon = Icons.Default.History
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredReservations = when (selectedFilter) {
                    R.string.filter_confirmed -> reservations.filter { it.status == ReservationStatus.CONFIRMED }
                    R.string.filter_pending -> reservations.filter { it.status == ReservationStatus.PENDING }
                    R.string.filter_cancelled -> reservations.filter { it.status == ReservationStatus.CANCELLED }
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
    val statusColor = when (reservation.status) {
        ReservationStatus.CONFIRMED -> SuccessGreen
        ReservationStatus.PENDING -> SecondaryOrange
        ReservationStatus.CANCELLED -> DangerRed
    }

    val statusText = when (reservation.status) {
        ReservationStatus.CONFIRMED -> stringResource(id = R.string.status_confirmed)
        ReservationStatus.PENDING -> stringResource(id = R.string.status_pending)
        ReservationStatus.CANCELLED -> stringResource(id = R.string.status_cancelled)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(id = R.string.table)} #${reservation.table}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${formatDate(reservation.date)} • ${reservation.time}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "${reservation.people} ${stringResource(id = R.string.people_count_suffix)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO: Navigate to detail screen */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(text = stringResource(id = R.string.detail_button))
            }
        }
    }
}