package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val time: String? = null,
    val priority: String = "Medium", // "Low", "Medium", "High"
    val date: String, // "YYYY-MM-DD"
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val iconName: String = "star", // "leaf", "water", "walk", "book", "sun", "heart", "moon"
    val targetDays: String = "ALL", // "ALL" or comma-separated "MON,TUE,WED,THU,FRI,SAT,SUN"
    val streak: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_completions",
    indices = [
        Index(value = ["habitId", "date"], unique = true)
    ]
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String, // "YYYY-MM-DD"
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val mood: String, // "Great", "Good", "Okay", "Low", "Stressed"
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val title: String,
    val content: String,
    val moodTag: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "learning_cards_state")
data class LearningCardStateEntity(
    @PrimaryKey
    val cardId: String,
    val isLearned: Boolean = false,
    val isFavorite: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
