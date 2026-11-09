package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.HabitEntity

data class HabitIconOption(
    val id: String,
    val name: String,
    val icon: ImageVector
)

val habitIconOptions = listOf(
    HabitIconOption("leaf", "Leaf", Icons.Default.Spa),
    HabitIconOption("water", "Water", Icons.Default.WaterDrop),
    HabitIconOption("walk", "Walk", Icons.Default.DirectionsWalk),
    HabitIconOption("book", "Read", Icons.Default.Book),
    HabitIconOption("sun", "Sun", Icons.Default.WbSunny),
    HabitIconOption("heart", "Health", Icons.Default.Favorite),
    HabitIconOption("moon", "Rest", Icons.Default.NightsStay),
    HabitIconOption("star", "Focus", Icons.Default.Star)
)

@Composable
fun AddEditHabitDialog(
    initialHabit: HabitEntity? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, iconName: String, targetDays: String) -> Unit
) {
    var title by remember { mutableStateOf(initialHabit?.title ?: "") }
    var selectedIcon by remember { mutableStateOf(initialHabit?.iconName ?: "leaf") }
    var frequency by remember { mutableStateOf(initialHabit?.targetDays ?: "ALL") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val frequencies = listOf(
        Pair("ALL", "Every day"),
        Pair("MON,TUE,WED,THU,FRI", "Weekdays"),
        Pair("SAT,SUN", "Weekends")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialHabit == null) "New Habit" else "Edit Habit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) errorText = null
                    },
                    label = { Text("Habit Title *") },
                    placeholder = { Text("e.g., Drink 2L water, 10 min walk") },
                    isError = errorText != null,
                    supportingText = errorText?.let { { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("habit_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Column {
                    Text(
                        text = "Choose an Icon",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        habitIconOptions.take(4).forEach { opt ->
                            val isSelected = selectedIcon == opt.id
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { selectedIcon = opt.id }
                                    .testTag("habit_icon_${opt.id}"),
                                shape = CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Icon(
                                    imageVector = opt.icon,
                                    contentDescription = opt.name,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        habitIconOptions.drop(4).forEach { opt ->
                            val isSelected = selectedIcon == opt.id
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { selectedIcon = opt.id }
                                    .testTag("habit_icon_${opt.id}"),
                                shape = CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Icon(
                                    imageVector = opt.icon,
                                    contentDescription = opt.name,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }

                Column {
                    Text(
                        text = "Schedule",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        frequencies.forEach { (code, label) ->
                            val isSelected = frequency == code
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { frequency = code }
                                    .testTag("habit_freq_$code"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorText = "Please enter a habit title"
                    } else {
                        onSave(title.trim(), selectedIcon, frequency)
                    }
                },
                modifier = Modifier.testTag("habit_save_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("habit_cancel_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
