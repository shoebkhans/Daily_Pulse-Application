package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SagePrimary

@Composable
fun DayScoreCard(
    tasksTotal: Int,
    tasksCompleted: Int,
    habitsTotal: Int,
    habitsCompleted: Int,
    modifier: Modifier = Modifier
) {
    // Score calculation logic:
    // If both tasks & habits exist: 50% tasks + 50% habits.
    // If only tasks: 100% tasks.
    // If only habits: 100% habits.
    // If neither: 0%.
    val score = when {
        tasksTotal > 0 && habitsTotal > 0 -> {
            val taskRatio = tasksCompleted.toFloat() / tasksTotal
            val habitRatio = habitsCompleted.toFloat() / habitsTotal
            ((taskRatio * 0.5f + habitRatio * 0.5f) * 100).toInt()
        }
        tasksTotal > 0 -> {
            ((tasksCompleted.toFloat() / tasksTotal) * 100).toInt()
        }
        habitsTotal > 0 -> {
            ((habitsCompleted.toFloat() / habitsTotal) * 100).toInt()
        }
        else -> 0
    }

    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "score_progress"
    )

    val (encouragement, subText) = when {
        tasksTotal == 0 && habitsTotal == 0 -> Pair(
            "A peaceful clean slate",
            "Add a gentle task or habit to begin your day"
        )
        score == 100 -> Pair(
            "Harmonious Day! 🌿",
            "You completed all planned tasks and habits today."
        )
        score >= 75 -> Pair(
            "Wonderful momentum ✨",
            "Great progress today, keep moving gently."
        )
        score >= 50 -> Pair(
            "Halfway there 🌱",
            "Steady steps forward. Listen to your body and mind."
        )
        score > 0 -> Pair(
            "Small steps matter 💚",
            "Every completed step is a victory. Be kind to yourself."
        )
        else -> Pair(
            "Ready when you are",
            "No pressure today. Focus on one small thing first."
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .smooth3DTilt(maxRotationDegrees = 3f)
            .testTag("day_score_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Day Score",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = encouragement,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Breakdown chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (tasksTotal > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "Tasks: $tasksCompleted/$tasksTotal",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    if (habitsTotal > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "Habits: $habitsCompleted/$habitsTotal",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Circular progress indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(76.dp)
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

                Canvas(modifier = Modifier.size(76.dp)) {
                    // Background track
                    drawCircle(
                        color = trackColor,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Progress arc
                    if (animatedProgress > 0f) {
                        drawArc(
                            color = primaryColor,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
