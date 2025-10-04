package com.restaurant.reservation.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.restaurant.reservation.R
import com.restaurant.reservation.model.Table
import com.restaurant.reservation.model.TableSelectionData
import com.restaurant.reservation.ui.theme.PrimaryBlue
import com.restaurant.reservation.viewmodel.AppViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TableSelectionScreen(viewModel: AppViewModel = viewModel()) {
    var step by remember { mutableStateOf(1) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var peopleCount by remember { mutableStateOf(2) }
    var selectedTable by remember { mutableStateOf<String?>(null) }
    var specialRequests by remember { mutableStateOf("") }

    val tables by viewModel.tables.collectAsState()
    val availableTables = tables.filter { it.isAvailable && it.capacity >= peopleCount }

    val canContinue = when (step) {
        1 -> selectedDate.isNotBlank()
        2 -> selectedTime.isNotBlank()
        3 -> peopleCount > 0
        4 -> selectedTable != null
        else -> false
    }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val date = GregorianCalendar(year, month, dayOfMonth).time
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.new_reservation)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step > 1) step-- else viewModel.navigateBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back_button))
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
                                text = stringResource(id = R.string.select_date),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = { datePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(selectedDate.ifBlank { stringResource(id = R.string.select_date) })
                                }
                            }
                        }
                        2 -> {
                            Text(
                                text = stringResource(id = R.string.select_time),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = { timePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(selectedTime.ifBlank { stringResource(id = R.string.select_time) })
                                }
                            }
                        }
                        3 -> {
                            Text(
                                text = stringResource(id = R.string.select_people),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                IconButton(onClick = { if (peopleCount > 1) peopleCount-- }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.decrement_people))
                                }
                                Text(
                                    text = "$peopleCount ${stringResource(id = R.string.people_count_suffix)}",
                                    style = MaterialTheme.typography.displaySmall,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                IconButton(onClick = { peopleCount++ }) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = stringResource(id = R.string.increment_people))
                                }
                            }
                        }
                        4 -> {
                            Text(
                                text = stringResource(id = R.string.select_table),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                tables.forEach { table ->
                                    val isAvailable = table.isAvailable
                                    val meetsCapacity = table.capacity >= peopleCount
                                    val isClickable = isAvailable && meetsCapacity
                                    TableCard(
                                        id = table.tableNumber,
                                        seats = table.capacity,
                                        isSelected = selectedTable == table.id,
                                        isClickable = isClickable,
                                        isOccupied = !table.isAvailable,
                                        onClick = {
                                            selectedTable = if (selectedTable == table.id) null else table.id
                                        }
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
                        val selectedTableData = tables.firstOrNull { it.id == selectedTable }
                        if (selectedTableData != null) {
                            viewModel.navigateToReservationDetails(
                                TableSelectionData(
                                    date = selectedDate,
                                    time = selectedTime,
                                    people = peopleCount,
                                    restaurantId = selectedTableData.restaurantId,
                                    tableId = selectedTableData.id,
                                    specialRequests = specialRequests
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canContinue,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(text = if (step < 4) stringResource(id = R.string.continue_button) else stringResource(id = R.string.continue_reservation_button))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableCard(id: String, seats: Int, isSelected: Boolean, isClickable: Boolean, isOccupied: Boolean, onClick: () -> Unit) {
    val containerColor = when {
        isOccupied -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        isSelected -> PrimaryBlue
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when {
        isOccupied -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        isSelected -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outline

    Card(
        onClick = onClick,
        enabled = isClickable,
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${stringResource(id = R.string.table_number)} #$id",
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
            Text(
                text = "$seats ${stringResource(id = R.string.people_count_suffix)}",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = if (isSelected) 0.8f else 0.6f)
            )
            if (isOccupied) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.table_occupied),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor
                )
            }
        }
    }
}