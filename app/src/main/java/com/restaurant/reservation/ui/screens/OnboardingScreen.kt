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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.restaurant.reservation.R
import com.restaurant.reservation.ui.theme.PrimaryBlue
import com.restaurant.reservation.viewmodel.AppViewModel
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val icon: ImageVector,
    val titleRes: Int,
    val descriptionRes: Int
)

private val slides = listOf(
    OnboardingSlide(
        icon = Icons.Default.CalendarMonth,
        titleRes = R.string.onboarding_title_1,
        descriptionRes = R.string.onboarding_desc_1
    ),
    OnboardingSlide(
        icon = Icons.Default.Fastfood,
        titleRes = R.string.onboarding_title_2,
        descriptionRes = R.string.onboarding_desc_2
    ),
    OnboardingSlide(
        icon = Icons.Default.Smartphone,
        titleRes = R.string.onboarding_title_3,
        descriptionRes = R.string.onboarding_desc_3
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
            Text(stringResource(id = R.string.onboarding_skip), color = PrimaryBlue)
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
                text = if (pagerState.currentPage == slides.size - 1) stringResource(id = R.string.onboarding_start) else stringResource(id = R.string.onboarding_next),
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
            contentDescription = stringResource(id = slide.titleRes),
            modifier = Modifier.size(120.dp),
            tint = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = slide.titleRes),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = slide.descriptionRes),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}