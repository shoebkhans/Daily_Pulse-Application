package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.JournalEntryEntity
import com.example.data.model.LearningCardStateEntity
import com.example.data.model.MoodEntryEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPulseDao {

    // --- Tasks ---
    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY isCompleted ASC, createdAt ASC")
    fun getTasksForDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY date DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    // --- Habits ---
    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Long)

    // --- Habit Completions ---
    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions")
    fun getAllCompletions(): Flow<List<HabitCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: HabitCompletionEntity): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCompletion(habitId: Long, date: String)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteCompletionsForHabit(habitId: Long)

    // --- Mood ---
    @Query("SELECT * FROM mood_entries WHERE date = :date ORDER BY timestamp DESC LIMIT 1")
    fun getMoodForDate(date: String): Flow<MoodEntryEntity?>

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoods(): Flow<List<MoodEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(entry: MoodEntryEntity): Long

    @Query("DELETE FROM mood_entries WHERE id = :id")
    suspend fun deleteMoodById(id: Long)

    // --- Journal ---
    @Query("SELECT * FROM journal_entries ORDER BY date DESC, updatedAt DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE date = :date ORDER BY updatedAt DESC")
    fun getJournalEntriesForDate(date: String): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(entry: JournalEntryEntity): Long

    @Update
    suspend fun updateJournal(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteJournal(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalById(id: Long)

    // --- Learning Cards State ---
    @Query("SELECT * FROM learning_cards_state")
    fun getAllLearningStates(): Flow<List<LearningCardStateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLearningState(state: LearningCardStateEntity)

    // --- Danger / Data Clear ---
    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()

    @Query("DELETE FROM habits")
    suspend fun clearAllHabits()

    @Query("DELETE FROM habit_completions")
    suspend fun clearAllCompletions()

    @Query("DELETE FROM mood_entries")
    suspend fun clearAllMoods()

    @Query("DELETE FROM journal_entries")
    suspend fun clearAllJournal()

    @Query("DELETE FROM learning_cards_state")
    suspend fun clearAllLearningStates()
}
