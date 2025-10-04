package com.restaurant.reservation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.restaurant.reservation.model.* // Perbaiki impor
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

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

    private val _tables = MutableStateFlow<List<Table>>(emptyList())
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    val unreadNotificationCount: StateFlow<Int> = _notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val totalReservations: StateFlow<Int> = _reservationHistory.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val thisMonthReservations: StateFlow<Int> = _reservationHistory.map { reservations ->
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        reservations.count {
            it.date.startsWith(currentMonth)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        checkOnboardingStatus()
        loadInitialData()
    }

    private fun checkOnboardingStatus() {
        // In real app, check SharedPreferences
        _hasSeenOnboarding.value = false
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                loadRestaurants()
                // Memuat tables setelah restaurants dimuat
                loadTables()
                // Memuat notifikasi setelah user terautentikasi (contoh sederhana)
                _user.value?.id?.let { loadNotificationsForCurrentUser(it) }
                _user.value?.id?.let { loadReservationsForCurrentUser(it) }
            } catch (e: Exception) {
                Log.e("Firebase", "Error loading initial data: ${e.message}")
            }
        }
    }

    // Fungsi untuk mengisi database dengan data contoh
    fun populateDatabaseWithSampleData() {
        Log.d("Firebase", "Memulai pengisian data contoh...")
        addSampleUser()
    }

    private fun addSampleUser() {
        val user = hashMapOf(
            "name" to "Budi Santoso",
            "email" to "budi.santoso@contoh.com",
            "phoneNumber" to "08123456789" // PERBAIKAN: Gunakan camelCase
        )
        val userDocumentId = "uid_budi_santoso"
        db.collection("users").document(userDocumentId).set(user)
            .addOnSuccessListener {
                Log.d("Firebase", "Dokumen pengguna berhasil ditambahkan dengan ID: $userDocumentId")
                addSampleRestaurant(userDocumentId)
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error saat menambahkan dokumen pengguna", e)
            }
    }

    private fun addSampleRestaurant(userId: String) {
        val restaurant = hashMapOf(
            "name" to "Lezat Rasa Restaurant",
            "address" to "Jl. Jendral Sudirman No. 1, Jakarta",
            "capacity" to 50,
            "openingHours" to mapOf( // PERBAIKAN: Gunakan camelCase
                "monday" to "09:00 - 21:00",
                "tuesday" to "09:00 - 21:00",
                "wednesday" to "09:00 - 21:00",
                "thursday" to "09:00 - 21:00",
                "friday" to "09:00 - 22:00",
                "saturday" to "10:00 - 23:00",
                "sunday" to "10:00 - 22:00"
            )
        )
        db.collection("restaurants").add(restaurant)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Dokumen restoran berhasil ditambahkan dengan ID: ${documentReference.id}")
                addSampleTable(documentReference.id, userId)
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error saat menambahkan dokumen restoran", e)
            }
    }

    private fun addSampleTable(restaurantId: String, userId: String) {
        val table = hashMapOf(
            "tableNumber" to "Tabel 5", // PERBAIKAN: Gunakan camelCase
            "capacity" to 4,
            "isAvailable" to true // PERBAIKAN: Gunakan camelCase
        )

        // PERBAIKAN: Menggunakan subkoleksi
        db.collection("restaurants").document(restaurantId)
            .collection("tables").add(table)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Dokumen meja berhasil ditambahkan dengan ID: ${documentReference.id}")
                addSampleReservation(userId, restaurantId, documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error saat menambahkan dokumen meja", e)
            }
    }

    private fun addSampleReservation(userId: String, restaurantId: String, tableId: String) {
        val reservation = hashMapOf(
            "userId" to userId, // PERBAIKAN: Gunakan camelCase
            "restaurantId" to restaurantId, // PERBAIKAN: Gunakan camelCase
            "tableId" to tableId, // PERBAIKAN: Gunakan camelCase
            "date" to "2025-10-05",
            "time" to "19:00",
            "people" to 4, // PERBAIKAN: Gunakan camelCase
            "status" to "confirmed",
            "specialRequests" to "Meja non-merokok di dekat jendela", // PERBAIKAN: Gunakan camelCase
            "createdAt" to System.currentTimeMillis() // PERBAIKAN: Gunakan camelCase
        )

        db.collection("reservations").add(reservation)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Dokumen reservasi berhasil ditambahkan dengan ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error saat menambahkan dokumen reservasi", e)
            }
    }

    // Fungsi untuk memuat data restoran dari Firebase
    private suspend fun loadRestaurants() {
        try {
            val result = db.collection("restaurants").get().await()
            val restaurantList = result.documents.map { doc ->
                Restaurant(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    address = doc.getString("address") ?: "",
                    capacity = doc.getLong("capacity")?.toInt() ?: 0,
                    openingHours = (doc.get("openingHours") as? Map<String, String>) ?: emptyMap()
                )
            }
            _restaurants.value = restaurantList
            Log.d("Firebase", "Restaurants loaded: ${restaurantList.size}")
        } catch (e: Exception) {
            Log.e("Firebase", "Error loading restaurants: ${e.message}")
        }
    }

    // Fungsi untuk memuat data meja dari Firebase (menggunakan subkoleksi)
    private suspend fun loadTables() {
        val allTables = mutableListOf<Table>()
        try {
            val restaurants = _restaurants.value // Asumsikan restoran sudah dimuat
            for (restaurant in restaurants) {
                val result = db.collection("restaurants").document(restaurant.id)
                    .collection("tables").get().await()

                val tableList = result.documents.map { doc ->
                    Table(
                        id = doc.id,
                        tableNumber = doc.getString("tableNumber") ?: "",
                        capacity = doc.getLong("capacity")?.toInt() ?: 0,
                        restaurantId = restaurant.id,
                        isAvailable = doc.getBoolean("isAvailable") ?: true
                    )
                }
                allTables.addAll(tableList)
            }
            _tables.value = allTables
            Log.d("Firebase", "Tables loaded: ${allTables.size}")
        } catch (e: Exception) {
            Log.e("Firebase", "Error loading tables: ${e.message}")
        }
    }

    // Fungsi untuk memuat notifikasi dari Firebase
    private suspend fun loadNotificationsForCurrentUser(userId: String) { // PERBAIKAN: Menambahkan parameter userId
        try {
            val result = db.collection("users").document(userId)
                .collection("notifications")
                .orderBy("timestamp")
                .get()
                .await()

            val notificationList = result.documents.map { doc ->
                NotificationItem(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    message = doc.getString("message") ?: "",
                    timestamp = doc.getString("timestamp") ?: "",
                    isRead = doc.getBoolean("isRead") ?: false
                )
            }
            _notifications.value = notificationList
            Log.d("Firebase", "Notifications loaded: ${notificationList.size}")
        } catch (e: Exception) {
            Log.e("Firebase", "Error loading notifications: ${e.message}")
        }
    }

    // Fungsi untuk memuat riwayat reservasi dari Firebase
    private suspend fun loadReservationsForCurrentUser(userId: String) {
        try {
            val result = db.collection("reservations")
                .whereEqualTo("userId", userId) // PERBAIKAN: Gunakan camelCase
                .orderBy("createdAt") // PERBAIKAN: Gunakan camelCase
                .get()
                .await()

            val reservationList = result.documents.map { doc ->
                Reservation(
                    id = doc.id,
                    date = doc.getString("date") ?: "",
                    time = doc.getString("time") ?: "",
                    tableId = doc.getString("tableId") ?: "", // PERBAIKAN: Ganti table_id
                    people = doc.getLong("people")?.toInt() ?: 0, // PERBAIKAN: Ganti number_of_guests
                    status = when (doc.getString("status")) {
                        "confirmed" -> ReservationStatus.CONFIRMED
                        "cancelled" -> ReservationStatus.CANCELLED
                        else -> ReservationStatus.PENDING
                    },
                    specialRequests = doc.getString("specialRequests") ?: "" // PERBAIKAN: Ganti special_requests
                )
            }
            _reservationHistory.value = reservationList
            Log.d("Firebase", "Reservations loaded: ${reservationList.size}")
        } catch (e: Exception) {
            Log.e("Firebase", "Error loading reservations: ${e.message}")
        }
    }

    // Fungsi untuk membuat reservasi baru di Firebase
    fun createReservation(reservationData: TableSelectionData, userId: String) {
        viewModelScope.launch {
            try {
                val reservation = hashMapOf(
                    "userId" to userId, // PERBAIKAN: Gunakan camelCase
                    "restaurantId" to reservationData.restaurantId,
                    "tableId" to reservationData.tableId,
                    "date" to reservationData.date,
                    "time" to reservationData.time,
                    "people" to reservationData.people, // PERBAIKAN: Gunakan camelCase
                    "status" to "pending",
                    "specialRequests" to reservationData.specialRequests,
                    "createdAt" to System.currentTimeMillis()
                )

                val documentReference = db.collection("reservations").add(reservation).await()
                Log.d("Firebase", "Reservation created with ID: ${documentReference.id}")

                // Update local state
                val newReservation = Reservation(
                    id = documentReference.id,
                    date = reservationData.date,
                    time = reservationData.time,
                    tableId = reservationData.tableId, // Perbaikan
                    people = reservationData.people,
                    status = ReservationStatus.PENDING,
                    specialRequests = reservationData.specialRequests
                )

                _activeReservation.value = newReservation
                _reservationHistory.value = listOf(newReservation) + _reservationHistory.value
                _pendingReservation.value = null
                _appState.value = AppState.HOME

            } catch (e: Exception) {
                Log.e("Firebase", "Error creating reservation: ${e.message}")
            }
        }
    }

    // Fungsi untuk membatalkan reservasi
    fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            try {
                db.collection("reservations")
                    .document(reservationId)
                    .update("status", "cancelled")
                    .await()

                // Update local state
                _reservationHistory.value = _reservationHistory.value.map { reservation ->
                    if (reservation.id == reservationId) {
                        reservation.copy(status = ReservationStatus.CANCELLED)
                    } else {
                        reservation
                    }
                }

                // If this was the active reservation, clear it
                if (_activeReservation.value?.id == reservationId) {
                    _activeReservation.value = null
                }

                Log.d("Firebase", "Reservation cancelled: $reservationId")
            } catch (e: Exception) {
                Log.e("Firebase", "Error cancelling reservation: ${e.message}")
            }
        }
    }

    // Fungsi untuk memperbarui status notifikasi di Firebase
    fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                _user.value?.id?.let { userId ->
                    db.collection("users").document(userId)
                        .collection("notifications")
                        .document(notificationId)
                        .update("isRead", true) // PERBAIKAN: Gunakan camelCase
                        .await()
                }

                // Update local state
                _notifications.value = _notifications.value.map { notification ->
                    if (notification.id == notificationId) {
                        notification.copy(isRead = true)
                    } else {
                        notification
                    }
                }
                Log.d("Firebase", "Notification marked as read: $notificationId")
            } catch (e: Exception) {
                Log.e("Firebase", "Error marking notification as read: ${e.message}")
            }
        }
    }

    // Fungsi-fungsi navigasi dan state management yang sudah ada
    fun handleSplashComplete() {
        viewModelScope.launch {
            delay(2000)
            if (_hasSeenOnboarding.value) {
                _appState.value = AppState.AUTH
            } else {
                _appState.value = AppState.ONBOARDING
            }
        }
    }

    fun handleOnboardingComplete() {
        _hasSeenOnboarding.value = true
        _appState.value = AppState.AUTH
    }

    fun handleOnboardingSkip() {
        _hasSeenOnboarding.value = true
        _appState.value = AppState.AUTH
    }

    fun handleLogin(userData: User) {
        _user.value = userData
        _appState.value = AppState.HOME

        viewModelScope.launch {
            userData.id?.let { userId ->
                loadReservationsForCurrentUser(userId) // Perbaiki pemanggilan fungsi
                loadNotificationsForCurrentUser(userId) // Memuat notifikasi setelah login
            }
        }
    }

    fun handleLogout() {
        _user.value = null
        _activeReservation.value = null
        _reservationHistory.value = emptyList()
        _notifications.value = emptyList() // Hapus notifikasi saat logout
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
            _user.value?.id?.let { userId ->
                createReservation(pending, userId)
            } ?: run {
                // Fallback ke local state jika tidak ada user ID
                val newReservation = Reservation(
                    id = System.currentTimeMillis().toString(),
                    date = pending.date,
                    time = pending.time,
                    tableId = pending.tableId, // Perbaikan
                    people = pending.people,
                    status = ReservationStatus.PENDING,
                    specialRequests = pending.specialRequests
                )

                _activeReservation.value = newReservation
                _reservationHistory.value = listOf(newReservation) + _reservationHistory.value
                _pendingReservation.value = null
                _appState.value = AppState.HOME
            }
        }
    }

    fun changeTab(tab: NavigationTab) {
        _activeTab.value = tab
        if (_appState.value != AppState.HOME) {
            _appState.value = AppState.HOME
        }
    }
}