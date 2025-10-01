package com.restaurant.reservation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.restaurant.reservation.ui.screens.AppNavigation
import com.restaurant.reservation.ui.theme.RestaurantReservationTheme
import com.restaurant.reservation.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RestaurantReservationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
}