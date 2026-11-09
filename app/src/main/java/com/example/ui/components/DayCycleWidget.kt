package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class TimePeriodInfo(
    val greeting: String,
    val mealTitle: String,
    val mealSubtitle: String,
    val mealIcon: ImageVector,
    val cycleStage: String,
    val isDay: Boolean,
    val progress: Float // 0f (sunrise) to 1f (sunset) or night progress
)

fun getTimePeriodInfo(calendar: Calendar = Calendar.getInstance()): TimePeriodInfo {
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val totalMinutes = hour * 60 + minute

    // Approximate solar cycle: Sunrise ~ 06:00 (360m), Midday ~ 12:00 (720m), Sunset ~ 18:00 (1080m)
    return when (hour) {
        in 5..11 -> {
            val progress = ((totalMinutes - 300).coerceIn(0, 420)).toFloat() / 420f
            TimePeriodInfo(
                greeting = "Good morning",
                mealTitle = "Breakfast time 🥣",
                mealSubtitle = "Nourish your morning with calm energy",
                mealIcon = Icons.Default.FreeBreakfast,
                cycleStage = if (hour < 8) "Sunrise 🌅" else "Morning Sun ☀️",
                isDay = true,
                progress = progress * 0.5f // left to top of arc
            )
        }
        in 12..15 -> {
            val progress = 0.5f + ((totalMinutes - 720).coerceIn(0, 240)).toFloat() / 480f
            TimePeriodInfo(
                greeting = "Good afternoon",
                mealTitle = "Lunch time 🥗",
                mealSubtitle = "Pause for a nourishing midday break",
                mealIcon = Icons.Default.LunchDining,
                cycleStage = "Midday Sun ☀️",
                isDay = true,
                progress = progress
            )
        }
        in 16..17 -> {
            val progress = 0.75f + ((totalMinutes - 960).coerceIn(0, 120)).toFloat() / 480f
            TimePeriodInfo(
                greeting = "Good afternoon",
                mealTitle = "Teatime & refresh 🍵",
                mealSubtitle = "Hydrate and stretch your body",
                mealIcon = Icons.Default.WbTwilight,
                cycleStage = "Golden Hour 🌇",
                isDay = true,
                progress = progress
            )
        }
        in 18..21 -> {
            val progress = ((totalMinutes - 1080).coerceIn(0, 240)).toFloat() / 240f
            TimePeriodInfo(
                greeting = "Good evening",
                mealTitle = "Dinner time 🍲",
                mealSubtitle = "Enjoy a gentle, soothing meal",
                mealIcon = Icons.Default.DinnerDining,
                cycleStage = if (hour < 20) "Sunset 🌇" else "Dusk 🌌",
                isDay = false,
                progress = progress
            )
        }
        else -> {
            // 22..4
            val nightMinutes = if (hour >= 22) totalMinutes - 1320 else totalMinutes + 120
            val progress = (nightMinutes.coerceIn(0, 420)).toFloat() / 420f
            TimePeriodInfo(
                greeting = "Good night",
                mealTitle = "Restful sleep 🌙",
                mealSubtitle = "Quiet hours to heal and recharge",
                mealIcon = Icons.Default.Bedtime,
                cycleStage = "Moonlit Night 🌙",
                isDay = false,
                progress = progress
            )
        }
    }
}

@Composable
fun DayCycleWidget(
    timeInfo: TimePeriodInfo,
    currentTimeString: String,
    dateString: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .smooth3DTilt(maxRotationDegrees = 2f)
            .testTag("day_cycle_widget"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Silent Time & Date + Greeting + Meal Prompt
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Silent Time & Date in subtle tone
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = currentTimeString,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Time-aware Greeting
                Text(
                    text = "${timeInfo.greeting} 🌿",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Meal / Routine Suggestion Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = timeInfo.mealIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = timeInfo.mealTitle,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right Side: Small Animated Sunrise to Sunset Arc Action
            SmallSunArcAnimation(
                isDay = timeInfo.isDay,
                progress = timeInfo.progress,
                stageLabel = timeInfo.cycleStage
            )
        }
    }
}

/**
 * Compact, charming visual arc representing the sun/moon moving
 * from horizon (rise) to peak (zenith) to set.
 */
@Composable
fun SmallSunArcAnimation(
    isDay: Boolean,
    progress: Float,
    stageLabel: String,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.coerceIn(0.05f, 0.95f),
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    // Subtle gentle pulse for the sun/moon glow
    val infiniteTransition = rememberInfiniteTransition(label = "sun_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val celestialColor = if (isDay) Color(0xFFE6A700) else Color(0xFF90CAF9)

    Column(
        modifier = modifier.testTag("sun_arc_indicator"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(width = 82.dp, height = 44.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(width = 76.dp, height = 40.dp)) {
                val w = size.width
                val h = size.height

                // Draw horizon baseline
                drawLine(
                    color = trackColor,
                    start = Offset(0f, h - 2f),
                    end = Offset(w, h - 2f),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Draw dashed semi-elliptical sky arc
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(4f, h - 2f)
                    cubicTo(
                        w * 0.15f, h * 0.05f,
                        w * 0.85f, h * 0.05f,
                        w - 4f, h - 2f
                    )
                }

                drawPath(
                    path = path,
                    color = trackColor,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                )

                // Calculate Celestial Body (Sun/Moon) Position along semi-circle
                val t = animatedProgress.value
                val angle = PI * (1f - t) // PI (left horizon) -> PI/2 (zenith) -> 0 (right horizon)

                val radiusX = (w - 16f) / 2f
                val radiusY = h - 10f
                val cx = w / 2f
                val cy = h - 2f

                val sunX = cx + (radiusX * cos(angle)).toFloat()
                val sunY = cy - (radiusY * sin(angle)).toFloat()

                // Sun glow
                drawCircle(
                    color = celestialColor.copy(alpha = 0.25f),
                    radius = (7.dp.toPx()) * pulseGlow,
                    center = Offset(sunX, sunY)
                )

                // Sun/Moon body
                drawCircle(
                    color = celestialColor,
                    radius = 4.5.dp.toPx(),
                    center = Offset(sunX, sunY)
                )
            }
        }

        // Small stage label (e.g. "Morning Sun", "Sunset")
        Text(
            text = stageLabel,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
