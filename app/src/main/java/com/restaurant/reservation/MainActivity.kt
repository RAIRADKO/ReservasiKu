package com.restaurant.reservation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.restaurant.reservation.ui.screens.AppNavigation
import com.restaurant.reservation.ui.theme.RestaurantReservationTheme
import com.restaurant.reservation.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // PENTING: Panggil fungsi ini HANYA SEKALI saat pertama kali menjalankan aplikasi
        // untuk mengisi database. Setelah terisi, hapus atau beri komentar baris ini.
        appViewModel.populateDatabaseWithSampleData()

        setContent {
            RestaurantReservationTheme {
                val appState by appViewModel.appState.collectAsState()
                val user by appViewModel.user.collectAsState()

                AppNavigation(
                    appState = appState,
                    user = user,
                    viewModel = appViewModel
                )
            }
        }
    }
}