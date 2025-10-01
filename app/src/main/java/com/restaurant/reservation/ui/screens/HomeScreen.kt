package com.restaurant.reservation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.restaurant.reservation.model.Reservation
import com.restaurant.reservation.model.ReservationStatus
import com.restaurant.reservation.ui.theme.DangerRed
import com.restaurant.reservation.ui.theme.PrimaryBlue
import com.restaurant.reservation.ui.theme.SecondaryOrange
import com.restaurant.reservation.ui.theme.SuccessGreen
import com.restaurant.reservation.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val user by viewModel.user.collectAsState()
    val activeReservation by viewModel.activeReservation.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Selamat datang,",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = user?.name ?: "Pengguna",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Active Reservation Card
            if (activeReservation != null) {
                ActiveReservationCard(reservation = activeReservation)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Quick Stats
            Text(
                text = "Statistik Cepat",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Reservasi",
                    value = "12",
                    icon = Icons.Default.TableRestaurant,
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Bulan Ini",
                    value = "3",
                    icon = Icons.Default.Schedule,
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { viewModel.navigateToTableSelection() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp, 16.dp, 16.dp, 80.dp),
            containerColor = PrimaryBlue,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Buat Reservasi Baru"
            )
        }
    }
}

@Composable
fun ActiveReservationCard(reservation: Reservation) {
    val statusColor = when (reservation.status) {
        ReservationStatus.CONFIRMED -> SuccessGreen
        ReservationStatus.PENDING -> SecondaryOrange
        ReservationStatus.CANCELLED -> DangerRed
    }

    val statusText = when (reservation.status) {
        ReservationStatus.CONFIRMED -> "Dikonfirmasi"
        ReservationStatus.PENDING -> "Menunggu"
        ReservationStatus.CANCELLED -> "Dibatalkan"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
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
                    text = "Reservasi Aktif",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ReservationDetailItem(
                    icon = Icons.Default.Schedule,
                    label = "Tanggal & Waktu",
                    value = "${formatDate(reservation.date)} • ${reservation.time}"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ReservationDetailItem(
                    icon = Icons.Default.TableRestaurant,
                    label = "Meja",
                    value = "Meja ${reservation.table}",
                    modifier = Modifier.weight(1f)
                )
                ReservationDetailItem(
                    icon = Icons.Default.Person,
                    label = "Tamu",
                    value = "${reservation.people} orang",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ReservationDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
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