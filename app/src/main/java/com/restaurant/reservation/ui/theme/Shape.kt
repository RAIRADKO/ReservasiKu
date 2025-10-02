package com.restaurant.reservation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp), // .25rem
    small = RoundedCornerShape(8.dp), // .5rem
    medium = RoundedCornerShape(12.dp), // .75rem
    large = RoundedCornerShape(16.dp), // 1rem
    extraLarge = RoundedCornerShape(24.dp) // 1.5rem
)

// The default radius is .75rem. This corresponds to the 'medium' shape
val RestaurantReservationShapes = Shapes(
    medium = RoundedCornerShape(12.dp)
)