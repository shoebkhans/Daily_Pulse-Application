package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEntryEntity

@Composable
fun AddEditJournalDialog(
    initialEntry: JournalEntryEntity? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, moodTag: String?) -> Unit
) {
    var title by remember { mutableStateOf(initialEntry?.title ?: "") }
    var content by remember { mutableStateOf(initialEntry?.content ?: "") }
    var selectedMoodTag by remember { mutableStateOf(initialEntry?.moodTag) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val moodTags = listOf(
        Pair("Peaceful", "🌿"),
        Pair("Grateful", "🙏"),
        Pair("Productive", "⚡"),
        Pair("Pensive", "💭"),
        Pair("Resting", "🌙")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialEntry == null) "New Reflection" else "Edit Reflection",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (optional)") },
                    placeholder = { Text("e.g., Evening quiet, Morning thoughts") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("journal_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Column {
                    Text(
                        text = "Mood Tag",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        moodTags.forEach { (tag, emoji) ->
                            val isSelected = selectedMoodTag == tag
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedMoodTag = if (isSelected) null else tag
                                    }
                                    .testTag("journal_tag_$tag"),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = emoji, fontSize = 16.sp)
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        if (it.isNotBlank()) errorText = null
                    },
                    label = { Text("Reflection *") },
                    placeholder = { Text("Write your thoughts freely. Everything is safely stored locally on this phone...") },
                    isError = errorText != null,
                    supportingText = errorText?.let { { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("journal_content_input"),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 8
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isBlank() && title.isBlank()) {
                        errorText = "Please write a thought or reflection"
                    } else {
                        onSave(title.trim(), content.trim(), selectedMoodTag)
                    }
                },
                modifier = Modifier.testTag("journal_save_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("journal_cancel_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
