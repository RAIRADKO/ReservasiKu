package com.restaurant.reservation.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val email: String
)

@Serializable
data class Reservation(
    val id: String,
    val date: String,
    val time: String,
    val table: Int,
    val people: Int,
    val status: ReservationStatus
)

enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

enum class AppState {
    SPLASH,
    ONBOARDING,
    AUTH,
    HOME,
    TABLE_SELECTION,
    RESERVATION_DETAILS
}

enum class NavigationTab {
    HOME,
    RESERVATIONS,
    NOTIFICATIONS,
    PROFILE
}

data class TableSelectionData(
    val date: String,
    val time: String,
    val people: Int,
    val table: Int? = null
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)