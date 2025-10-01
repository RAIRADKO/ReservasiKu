package com.restaurant.reservation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Color scheme based on the original design
val PrimaryBlue = Color(0xFF0000FF)
val SecondaryOrange = Color(0xFFFFA500)
val SuccessGreen = Color(0xFF00C853)
val DangerRed = Color(0xFFE53935)
val BackgroundGray = Color(0xFFF9F9F9)
val SurfaceWhite = Color(0xFFFFFFFF)
val OnSurfaceBlack = Color(0xFF212121)
val MutedGray = Color(0xFF757575)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = SecondaryOrange,
    onSecondary = Color.White,
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    error = DangerRed,
    onError = Color.White,
    background = BackgroundGray,
    onBackground = OnSurfaceBlack,
    surface = SurfaceWhite,
    onSurface = OnSurfaceBlack,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = MutedGray,
    outline = Color(0x1A000000),
    inverseOnSurface = Color.White,
    inverseSurface = OnSurfaceBlack,
    inversePrimary = Color(0xFF8BB5FF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8BB5FF),
    onPrimary = Color(0xFF1A1A1A),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF1A1A1A),
    tertiary = Color(0xFF4CAF50),
    onTertiary = Color(0xFF1A1A1A),
    error = Color(0xFFFF5722),
    onError = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0x33FFFFFF),
    inverseOnSurface = Color(0xFF1A1A1A),
    inverseSurface = Color(0xFFE0E0E0),
    inversePrimary = PrimaryBlue
)

@Composable
fun RestaurantReservationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}