package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.dao.DailyPulseDao
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.JournalEntryEntity
import com.example.data.model.LearningCardStateEntity
import com.example.data.model.MoodEntryEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DailyPulseRepository(
    private val dao: DailyPulseDao,
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dailypulse_prefs", Context.MODE_PRIVATE)

    private val _themePreference = MutableStateFlow(
        prefs.getString(PREF_THEME, "system") ?: "system"
    )
    val themePreference: StateFlow<String> = _themePreference.asStateFlow()

    private val _colorOption = MutableStateFlow(
        prefs.getString(PREF_COLOR_OPTION, "sage") ?: "sage"
    )
    val colorOption: StateFlow<String> = _colorOption.asStateFlow()

    private val _reminderEnabled = MutableStateFlow(
        prefs.getBoolean(PREF_REMINDER_ENABLED, false)
    )
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled.asStateFlow()

    private val _reminderTime = MutableStateFlow(
        Pair(
            prefs.getInt(PREF_REMINDER_HOUR, 20),
            prefs.getInt(PREF_REMINDER_MINUTE, 0)
        )
    )
    val reminderTime: StateFlow<Pair<Int, Int>> = _reminderTime.asStateFlow()

    fun setThemePreference(theme: String) {
        prefs.edit().putString(PREF_THEME, theme).apply()
        _themePreference.value = theme
    }

    fun setColorOption(color: String) {
        prefs.edit().putString(PREF_COLOR_OPTION, color).apply()
        _colorOption.value = color
    }

    fun setReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(PREF_REMINDER_ENABLED, enabled).apply()
        _reminderEnabled.value = enabled
    }

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(PREF_REMINDER_HOUR, hour)
            .putInt(PREF_REMINDER_MINUTE, minute)
            .apply()
        _reminderTime.value = Pair(hour, minute)
    }

    // --- Tasks ---
    fun getTasksForDate(date: String): Flow<List<TaskEntity>> = dao.getTasksForDate(date)
    fun getAllTasks(): Flow<List<TaskEntity>> = dao.getAllTasks()
    suspend fun insertTask(task: TaskEntity): Long = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)
    suspend fun deleteTaskById(id: Long) = dao.deleteTaskById(id)

    // --- Habits ---
    fun getAllHabits(): Flow<List<HabitEntity>> = dao.getAllHabits()
    suspend fun insertHabit(habit: HabitEntity): Long = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) {
        dao.deleteCompletionsForHabit(habit.id)
        dao.deleteHabit(habit)
    }

    // --- Habit Completions ---
    fun getCompletionsForDate(date: String): Flow<List<HabitCompletionEntity>> =
        dao.getCompletionsForDate(date)

    fun getAllCompletions(): Flow<List<HabitCompletionEntity>> = dao.getAllCompletions()

    suspend fun toggleHabitCompletion(habitId: Long, date: String, isCurrentlyCompleted: Boolean) {
        if (isCurrentlyCompleted) {
            dao.deleteCompletion(habitId, date)
        } else {
            dao.insertCompletion(HabitCompletionEntity(habitId = habitId, date = date))
        }
    }

    // --- Mood ---
    fun getMoodForDate(date: String): Flow<MoodEntryEntity?> = dao.getMoodForDate(date)
    fun getAllMoods(): Flow<List<MoodEntryEntity>> = dao.getAllMoods()
    suspend fun saveMood(date: String, mood: String, note: String) {
        dao.insertMood(MoodEntryEntity(date = date, mood = mood, note = note))
    }
    suspend fun deleteMoodById(id: Long) = dao.deleteMoodById(id)

    // --- Journal ---
    fun getAllJournalEntries(): Flow<List<JournalEntryEntity>> = dao.getAllJournalEntries()
    fun getJournalEntriesForDate(date: String): Flow<List<JournalEntryEntity>> =
        dao.getJournalEntriesForDate(date)

    suspend fun insertJournal(entry: JournalEntryEntity): Long = dao.insertJournal(entry)
    suspend fun updateJournal(entry: JournalEntryEntity) = dao.updateJournal(entry)
    suspend fun deleteJournal(entry: JournalEntryEntity) = dao.deleteJournal(entry)
    suspend fun deleteJournalById(id: Long) = dao.deleteJournalById(id)

    // --- Learning ---
    fun getAllLearningStates(): Flow<List<LearningCardStateEntity>> = dao.getAllLearningStates()
    suspend fun toggleLearningLearned(cardId: String, currentLearned: Boolean, currentFav: Boolean) {
        dao.insertOrUpdateLearningState(
            LearningCardStateEntity(
                cardId = cardId,
                isLearned = !currentLearned,
                isFavorite = currentFav
            )
        )
    }

    suspend fun toggleLearningFavorite(cardId: String, currentLearned: Boolean, currentFav: Boolean) {
        dao.insertOrUpdateLearningState(
            LearningCardStateEntity(
                cardId = cardId,
                isLearned = currentLearned,
                isFavorite = !currentFav
            )
        )
    }

    // --- Data Deletion ---
    suspend fun deleteAllUserData() {
        dao.clearAllTasks()
        dao.clearAllHabits()
        dao.clearAllCompletions()
        dao.clearAllMoods()
        dao.clearAllJournal()
        dao.clearAllLearningStates()
    }

    companion object {
        private const val PREF_THEME = "pref_theme"
        private const val PREF_COLOR_OPTION = "pref_color_option"
        private const val PREF_REMINDER_ENABLED = "pref_reminder_enabled"
        private const val PREF_REMINDER_HOUR = "pref_reminder_hour"
        private const val PREF_REMINDER_MINUTE = "pref_reminder_minute"
    }
}
