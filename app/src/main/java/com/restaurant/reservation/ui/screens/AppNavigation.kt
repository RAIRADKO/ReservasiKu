package com.restaurant.reservation.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.restaurant.reservation.model.AppState
import com.restaurant.reservation.model.NavigationTab
import com.restaurant.reservation.model.User
import com.restaurant.reservation.ui.components.BottomNavigationBar
import com.restaurant.reservation.viewmodel.AppViewModel

@Composable
fun AppNavigation(
    appState: AppState,
    user: User?,
    viewModel: AppViewModel
) {
    val activeTab by viewModel.activeTab.collectAsState()
    val shouldShowBottomNav = user != null &&
            appState !in listOf(AppState.TABLE_SELECTION, AppState.RESERVATION_DETAILS)

    if (shouldShowBottomNav) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    activeTab = activeTab,
                    onTabChange = { tab -> viewModel.changeTab(tab) },
                    notificationCount = 2
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "screen_transition"
                ) { tab ->
                    when (tab) {
                        NavigationTab.HOME -> HomeScreen(viewModel = viewModel)
                        NavigationTab.RESERVATIONS -> ReservationHistoryScreen(viewModel = viewModel)
                        NavigationTab.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
                        NavigationTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                    }
                }
            }
        }
    } else {
        AnimatedContent(
            targetState = appState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "full_screen_transition"
        ) { state ->
            when (state) {
                AppState.SPLASH -> SplashScreen(viewModel = viewModel)
                AppState.ONBOARDING -> OnboardingScreen(viewModel = viewModel)
                AppState.AUTH -> AuthScreen(viewModel = viewModel)
                AppState.TABLE_SELECTION -> TableSelectionScreen(viewModel = viewModel)
                AppState.RESERVATION_DETAILS -> ReservationDetailsScreen(viewModel = viewModel)
                else -> HomeScreen(viewModel = viewModel)
            }
        }
    }
}