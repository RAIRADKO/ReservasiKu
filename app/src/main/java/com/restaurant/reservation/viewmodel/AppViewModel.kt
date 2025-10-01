package com.restaurant.reservation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.restaurant.reservation.model.*

class AppViewModel : ViewModel() {

    private val _appState = MutableStateFlow(AppState.SPLASH)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _activeTab = MutableStateFlow(NavigationTab.HOME)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _activeReservation = MutableStateFlow<Reservation?>(null)
    val activeReservation: StateFlow<Reservation?> = _activeReservation.asStateFlow()

    private val _pendingReservation = MutableStateFlow<TableSelectionData?>(null)
    val pendingReservation: StateFlow<TableSelectionData?> = _pendingReservation.asStateFlow()

    private val _reservationHistory = MutableStateFlow<List<Reservation>>(emptyList())
    val reservationHistory: StateFlow<List<Reservation>> = _reservationHistory.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _hasSeenOnboarding = MutableStateFlow(false)
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    init {
        // Simulate checking shared preferences for onboarding status
        checkOnboardingStatus()
        loadMockData()
    }

    private fun checkOnboardingStatus() {
        // In real app, check SharedPreferences
        _hasSeenOnboarding.value = false
    }

    private fun loadMockData() {
        // Load mock notifications
        _notifications.value = listOf(
            NotificationItem(
                id = "1",
                title = "Reservasi Dikonfirmasi",
                message = "Reservasi Anda untuk 2 orang pada 20 Des 2024 pukul 19:00 telah dikonfirmasi.",
                timestamp = "2 jam yang lalu"
            ),
            NotificationItem(
                id = "2",
                title = "Pengingat Reservasi",
                message = "Jangan lupa reservasi Anda hari ini pukul 19:00 di meja 4.",
                timestamp = "1 hari yang lalu",
                isRead = true
            )
        )

        // Load mock reservation history
        _reservationHistory.value = listOf(
            Reservation(
                id = "1",
                date = "2024-12-20",
                time = "19:00",
                table = 4,
                people = 2,
                status = ReservationStatus.CONFIRMED
            ),
            Reservation(
                id = "2",
                date = "2024-12-15",
                time = "18:30",
                table = 2,
                people = 4,
                status = ReservationStatus.CANCELLED
            ),
            Reservation(
                id = "3",
                date = "2024-12-10",
                time = "20:00",
                table = 6,
                people = 3,
                status = ReservationStatus.CONFIRMED
            )
        )
    }

    fun handleSplashComplete() {
        viewModelScope.launch {
            delay(2000) // Simulate splash duration
            if (_hasSeenOnboarding.value) {
                _appState.value = AppState.AUTH
            } else {
                _appState.value = AppState.ONBOARDING
            }
        }
    }

    fun handleOnboardingComplete() {
        _hasSeenOnboarding.value = true
        // In real app, save to SharedPreferences
        _appState.value = AppState.AUTH
    }

    fun handleOnboardingSkip() {
        _hasSeenOnboarding.value = true
        // In real app, save to SharedPreferences
        _appState.value = AppState.AUTH
    }

    fun handleLogin(userData: User) {
        _user.value = userData
        _appState.value = AppState.HOME

        // Set mock active reservation
        _activeReservation.value = Reservation(
            id = "1",
            date = "2024-12-20",
            time = "19:00",
            table = 4,
            people = 2,
            status = ReservationStatus.CONFIRMED
        )
    }

    fun handleLogout() {
        _user.value = null
        _activeReservation.value = null
        _appState.value = AppState.AUTH
        _activeTab.value = NavigationTab.HOME
    }

    fun navigateToTableSelection() {
        _appState.value = AppState.TABLE_SELECTION
    }

    fun navigateToReservationDetails(reservationData: TableSelectionData) {
        _pendingReservation.value = reservationData
        _appState.value = AppState.RESERVATION_DETAILS
    }

    fun navigateBack() {
        when (_appState.value) {
            AppState.TABLE_SELECTION -> _appState.value = AppState.HOME
            AppState.RESERVATION_DETAILS -> _appState.value = AppState.TABLE_SELECTION
            else -> _appState.value = AppState.HOME
        }
    }

    fun confirmReservation() {
        _pendingReservation.value?.let { pending ->
            val newReservation = Reservation(
                id = System.currentTimeMillis().toString(),
                date = pending.date,
                time = pending.time,
                table = pending.table ?: 1,
                people = pending.people,
                status = ReservationStatus.PENDING
            )

            _activeReservation.value = newReservation
            _reservationHistory.value = listOf(newReservation) + _reservationHistory.value
            _pendingReservation.value = null
            _appState.value = AppState.HOME
        }
    }

    fun changeTab(tab: NavigationTab) {
        _activeTab.value = tab
        if (_appState.value != AppState.HOME) {
            _appState.value = AppState.HOME
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        _notifications.value = _notifications.value.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }
    }
}