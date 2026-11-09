package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Lightweight, hardware-accelerated 3D tilt modifier.
 * Creates a subtle, smooth tactile 3D perspective when pressed,
 * elevating the card with realistic spatial feedback without heavy computation.
 */
fun Modifier.smooth3DTilt(
    maxRotationDegrees: Float = 4f,
    pressedScale: Float = 0.985f
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "smooth3d_scale"
    )

    val rotX by animateFloatAsState(
        targetValue = if (isPressed) maxRotationDegrees else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "smooth3d_rotX"
    )

    val shadowElevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "smooth3d_elevation"
    )

    this
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                isPressed = true
                waitForUpOrCancellation()
                isPressed = false
            }
        }
        .graphicsLayer {
            this.scaleX = scale
            this.scaleY = scale
            this.rotationX = rotX
            this.cameraDistance = 16f * density
            this.shadowElevation = shadowElevation
        }
}
