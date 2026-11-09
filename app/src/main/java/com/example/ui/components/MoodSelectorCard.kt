package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MoodEntryEntity
import com.example.ui.theme.MoodGoodColor
import com.example.ui.theme.MoodGreatColor
import com.example.ui.theme.MoodLowColor
import com.example.ui.theme.MoodOkayColor
import com.example.ui.theme.MoodStressedColor

data class MoodOption(
    val name: String,
    val emoji: String,
    val color: Color
)

val moodOptions = listOf(
    MoodOption("Great", "😊", MoodGreatColor),
    MoodOption("Good", "🙂", MoodGoodColor),
    MoodOption("Okay", "😐", MoodOkayColor),
    MoodOption("Low", "🌧️", MoodLowColor),
    MoodOption("Stressed", "⚡", MoodStressedColor)
)

@Composable
fun MoodSelectorCard(
    currentMood: MoodEntryEntity?,
    onSelectMood: (mood: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMoodName by remember(currentMood) {
        mutableStateOf(currentMood?.mood ?: "Good")
    }
    var noteInput by remember(currentMood) {
        mutableStateOf(currentMood?.note ?: "")
    }
    var isExpandedForEdit by remember(currentMood) {
        mutableStateOf(currentMood == null)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .smooth3DTilt(maxRotationDegrees = 2.5f)
            .testTag("mood_selector_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mood,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Daily Mood Check-In",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (currentMood != null) "Logged for today" else "How is your heart feeling today?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (currentMood != null && !isExpandedForEdit) {
                    IconButton(
                        onClick = { isExpandedForEdit = true },
                        modifier = Modifier.size(32.dp).testTag("mood_edit_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit mood log",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5 Mood choices
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                moodOptions.forEach { option ->
                    val isSelected = selectedMoodName == option.name
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 3.dp)
                            .clickable {
                                selectedMoodName = option.name
                                if (!isExpandedForEdit) {
                                    onSelectMood(option.name, noteInput)
                                }
                            }
                            .testTag("mood_btn_${option.name}"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) option.color.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) BorderStroke(2.dp, option.color) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = option.emoji,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // If a mood is already saved and not currently expanding editor, show the note and summary
            if (currentMood != null && !isExpandedForEdit) {
                if (currentMood.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "“${currentMood.note}”",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            } else {
                // Quick-entry input field for mood note
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    placeholder = {
                        Text(
                            "Add a quick note on why you're feeling this way (optional)...",
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mood_quick_entry_input"),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentMood != null) {
                        ButtonDefaults.buttonColors()
                        androidx.compose.material3.TextButton(
                            onClick = {
                                isExpandedForEdit = false
                                selectedMoodName = currentMood.mood
                                noteInput = currentMood.note
                            }
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Button(
                        onClick = {
                            onSelectMood(selectedMoodName, noteInput)
                            isExpandedForEdit = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("mood_quick_entry_save")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (currentMood == null) "Log Mood" else "Update Mood")
                    }
                }
            }
        }
    }
}
