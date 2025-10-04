package com.restaurant.reservation.model

import androidx.compose.runtime.Immutable

@Immutable
enum class AppState {
    SPLASH,
    ONBOARDING,
    AUTH,
    HOME,
    TABLE_SELECTION,
    RESERVATION_DETAILS
}

@Immutable
enum class NavigationTab {
    HOME,
    RESERVATIONS,
    NOTIFICATIONS,
    PROFILE
}

@Immutable
data class User(
    val id: String?,
    val name: String,
    val email: String,
    val phoneNumber: String
)

@Immutable
data class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val capacity: Int,
    val openingHours: Map<String, String>
)

@Immutable
data class Table(
    val id: String,
    val tableNumber: String,
    val capacity: Int,
    val restaurantId: String,
    val isAvailable: Boolean
)

@Immutable
data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean
)

@Immutable
data class Reservation(
    val id: String,
    val date: String,
    val time: String,
    val tableId: String, // PERBAIKI: Ganti 'table' menjadi 'tableId'
    val people: Int,
    val status: ReservationStatus,
    val specialRequests: String
)

enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

@Immutable
data class TableSelectionData(
    val date: String,
    val time: String,
    val people: Int,
    val restaurantId: String,
    val tableId: String,
    val specialRequests: String
)