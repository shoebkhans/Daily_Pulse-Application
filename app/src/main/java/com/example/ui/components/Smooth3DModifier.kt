package com.example.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Lightweight, hardware-accelerated 3D perspective modifier.
 * Applies subtle spatial depth, smooth camera distance, and realistic elevation
 * without blocking or interfering with touch gestures or LazyColumn scroll flings.
 */
fun Modifier.smooth3DTilt(
    elevation: Dp = 1.dp,
    maxRotationDegrees: Float = 0f
): Modifier = this.graphicsLayer {
    this.shadowElevation = elevation.toPx()
    this.cameraDistance = 16f * density
    if (maxRotationDegrees != 0f) {
        this.rotationX = maxRotationDegrees
    }
}
