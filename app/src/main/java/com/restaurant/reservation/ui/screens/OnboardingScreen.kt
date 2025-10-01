package com.restaurant.reservation.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.restaurant.reservation.ui.theme.PrimaryBlue
import com.restaurant.reservation.ui.theme.RestaurantReservationTheme
import com.restaurant.reservation.viewmodel.AppViewModel
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val slides = listOf(
    OnboardingSlide(
        icon = Icons.Default.CalendarMonth,
        title = "Pesan meja kapan saja",
        description = "Reservasi meja restoran favorit Anda dengan mudah, 24/7"
    ),
    OnboardingSlide(
        icon = Icons.Default.Fastfood,
        title = "Nikmati pengalaman makan tanpa antre",
        description = "Datang langsung ke meja yang sudah disiapkan untuk Anda"
    ),
    OnboardingSlide(
        icon = Icons.Default.Smartphone,
        title = "Mudah, cepat, nyaman",
        description = "Semua dalam genggaman Anda dengan aplikasi yang user-friendly"
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(viewModel: AppViewModel) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Skip Button
        TextButton(
            onClick = { viewModel.handleOnboardingSkip() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lewati", color = PrimaryBlue)
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            OnboardingSlideItem(slide = slides[page])
        }

        // Dot Indicator
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(slides.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(color, CircleShape)
                        .size(10.dp)
                )
            }
        }

        // Action Button
        Button(
            onClick = {
                if (pagerState.currentPage < slides.size - 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    viewModel.handleOnboardingComplete()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(
                text = if (pagerState.currentPage == slides.size - 1) "Mulai" else "Selanjutnya",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun OnboardingSlideItem(slide: OnboardingSlide) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = slide.icon,
            contentDescription = slide.title,
            modifier = Modifier.size(120.dp),
            tint = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = slide.title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = slide.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}