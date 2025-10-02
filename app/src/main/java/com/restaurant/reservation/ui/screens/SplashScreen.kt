package com.restaurant.reservation.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.restaurant.reservation.R
import com.restaurant.reservation.ui.theme.PrimaryBlue
import com.restaurant.reservation.ui.theme.TextPrimary
import com.restaurant.reservation.ui.theme.TextSecondary
import com.restaurant.reservation.viewmodel.AppViewModel

@Composable
fun SplashScreen(viewModel: AppViewModel) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        viewModel.handleSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
                    .background(Color.White, CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_restaurant),
                    contentDescription = "Restaurant Logo",
                    modifier = Modifier.size(64.dp),
                    // Menggunakan PrimaryBlue sesuai spesifikasi
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(PrimaryBlue)
                )
            }

            Text(
                text = "ReservasiKu",
                style = MaterialTheme.typography.displayMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                modifier = Modifier.scale(scale.value)
            )

            Text(
                text = "Reservasi Meja Restoran",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextSecondary,
                    fontSize = 16.sp
                ),
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}