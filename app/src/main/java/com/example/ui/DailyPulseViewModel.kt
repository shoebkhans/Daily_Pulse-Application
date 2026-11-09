package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.DailyPulseDatabase
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.JournalEntryEntity
import com.example.data.model.LearningCardStateEntity
import com.example.data.model.MoodEntryEntity
import com.example.data.model.TaskEntity
import com.example.data.repository.DailyPulseRepository
import com.example.data.weather.WeatherInfo
import com.example.data.weather.WeatherService
import com.example.receiver.DailyPulseReminderReceiver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DailyPulseRepository

    private val _todayDate = MutableStateFlow(getTodayIsoDate())
    val todayDate: StateFlow<String> = _todayDate.asStateFlow()

    private val _selectedDate = MutableStateFlow(getTodayIsoDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _weather = MutableStateFlow(WeatherService.getCachedWeather())
    val weather: StateFlow<WeatherInfo> = _weather.asStateFlow()

    init {
        val database = DailyPulseDatabase.getDatabase(application)
        repository = DailyPulseRepository(database.dailyPulseDao(), application)
        refreshWeather()
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val result = WeatherService.fetchCurrentWeather(getApplication())
            _weather.value = result
        }
    }

    val themePreference: StateFlow<String> = repository.themePreference
    val colorOption: StateFlow<String> = repository.colorOption
    val reminderEnabled: StateFlow<Boolean> = repository.reminderEnabled
    val reminderTime: StateFlow<Pair<Int, Int>> = repository.reminderTime

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedDate: StateFlow<List<TaskEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getTasksForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val habitCompletionsForSelectedDate: StateFlow<List<HabitCompletionEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getCompletionsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHabitCompletions: StateFlow<List<HabitCompletionEntity>> = repository.getAllCompletions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val moodForSelectedDate: StateFlow<MoodEntryEntity?> = _selectedDate
        .flatMapLatest { date -> repository.getMoodForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allMoods: StateFlow<List<MoodEntryEntity>> = repository.getAllMoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJournalEntries: StateFlow<List<JournalEntryEntity>> = repository.getAllJournalEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val learningStates: StateFlow<List<LearningCardStateEntity>> = repository.getAllLearningStates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun selectToday() {
        _selectedDate.value = getTodayIsoDate()
    }

    // --- Task Actions ---
    fun addTask(title: String, time: String?, priority: String, date: String = _selectedDate.value) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title.trim(),
                    time = time?.trim().takeIf { !it.isNullOrBlank() },
                    priority = priority,
                    date = date
                )
            )
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- Habit Actions ---
    fun addHabit(title: String, iconName: String, targetDays: String = "ALL") {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(
                    title = title.trim(),
                    iconName = iconName,
                    targetDays = targetDays
                )
            )
        }
    }

    fun updateHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun toggleHabitCompletion(habitId: Long, date: String = _selectedDate.value) {
        viewModelScope.launch {
            val currentCompletions = habitCompletionsForSelectedDate.value
            val isCompleted = currentCompletions.any { it.habitId == habitId }
            repository.toggleHabitCompletion(habitId, date, isCompleted)
        }
    }

    // --- Mood Actions ---
    fun saveMood(mood: String, note: String, date: String = _selectedDate.value) {
        viewModelScope.launch {
            repository.saveMood(date, mood, note.trim())
        }
    }

    fun deleteMood(id: Long) {
        viewModelScope.launch {
            repository.deleteMoodById(id)
        }
    }

    // --- Journal Actions ---
    fun saveJournal(id: Long = 0, title: String, content: String, moodTag: String?, date: String = _selectedDate.value) {
        if (content.isBlank() && title.isBlank()) return
        viewModelScope.launch {
            val finalTitle = if (title.isBlank()) "Reflection on " + formatFriendlyDate(date) else title.trim()
            if (id == 0L) {
                repository.insertJournal(
                    JournalEntryEntity(
                        date = date,
                        title = finalTitle,
                        content = content.trim(),
                        moodTag = moodTag
                    )
                )
            } else {
                repository.updateJournal(
                    JournalEntryEntity(
                        id = id,
                        date = date,
                        title = finalTitle,
                        content = content.trim(),
                        moodTag = moodTag,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun deleteJournal(id: Long) {
        viewModelScope.launch {
            repository.deleteJournalById(id)
        }
    }

    // --- Learning Actions ---
    fun toggleLearningLearned(cardId: String) {
        viewModelScope.launch {
            val state = learningStates.value.find { it.cardId == cardId }
            val isLearned = state?.isLearned ?: false
            val isFav = state?.isFavorite ?: false
            repository.toggleLearningLearned(cardId, isLearned, isFav)
        }
    }

    fun toggleLearningFavorite(cardId: String) {
        viewModelScope.launch {
            val state = learningStates.value.find { it.cardId == cardId }
            val isLearned = state?.isLearned ?: false
            val isFav = state?.isFavorite ?: false
            repository.toggleLearningFavorite(cardId, isLearned, isFav)
        }
    }

    // --- Settings & Reminders ---
    fun setThemePreference(theme: String) {
        repository.setThemePreference(theme)
    }

    fun setColorOption(color: String) {
        repository.setColorOption(color)
    }

    fun toggleReminder(enabled: Boolean, context: Context) {
        repository.setReminderEnabled(enabled)
        val (h, m) = reminderTime.value
        if (enabled) {
            DailyPulseReminderReceiver.scheduleDailyAlarm(context, h, m)
        } else {
            DailyPulseReminderReceiver.cancelDailyAlarm(context)
        }
    }

    fun updateReminderTime(hour: Int, minute: Int, context: Context) {
        repository.setReminderTime(hour, minute)
        if (reminderEnabled.value) {
            DailyPulseReminderReceiver.scheduleDailyAlarm(context, hour, minute)
        }
    }

    fun testReminder(context: Context) {
        DailyPulseReminderReceiver.showNotification(context)
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllUserData()
        }
    }

    fun generateExportSummary(): String {
        val totalTasks = allTasks.value.size
        val completedTasks = allTasks.value.count { it.isCompleted }
        val habitCount = habits.value.size
        val completionCount = allHabitCompletions.value.size
        val journalCount = allJournalEntries.value.size
        val moodCount = allMoods.value.size

        return buildString {
            appendLine("=== DailyPulse Personal Backup ===")
            appendLine("Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())}")
            appendLine("Storage: 100% On-Device Local SQLite")
            appendLine()
            appendLine("Tasks: $completedTasks completed out of $totalTasks recorded")
            appendLine("Habits: $habitCount active habits ($completionCount total check-ins)")
            appendLine("Journal Reflections: $journalCount entries")
            appendLine("Mood Logs: $moodCount recorded days")
            appendLine()
            appendLine("--- Active Habits ---")
            habits.value.forEach { h ->
                appendLine("- ${h.title} (Days: ${h.targetDays})")
            }
            appendLine()
            appendLine("--- Recent Journal Reflections ---")
            allJournalEntries.value.take(10).forEach { j ->
                appendLine("[${j.date}] ${j.title}")
                appendLine(j.content)
                appendLine()
            }
        }
    }

    companion object {
        fun getTodayIsoDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }

        fun formatFriendlyDate(isoDate: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val date = parser.parse(isoDate) ?: return isoDate
                val formatter = SimpleDateFormat("EEE, MMM d", Locale.US)
                formatter.format(date)
            } catch (_: Exception) {
                isoDate
            }
        }

        fun getDayOfYear(): Int {
            val cal = Calendar.getInstance()
            return cal.get(Calendar.DAY_OF_YEAR)
        }
    }
}
