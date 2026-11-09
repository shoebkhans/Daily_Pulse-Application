package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.learning.DailyLearningContent
import com.example.data.model.HabitEntity
import com.example.data.model.TaskEntity
import com.example.ui.DailyPulseViewModel
import com.example.ui.components.AddEditHabitDialog
import com.example.ui.components.AddEditTaskDialog
import com.example.ui.components.DayCycleWidget
import com.example.ui.components.DayScoreCard
import com.example.ui.components.HabitItemCard
import com.example.ui.components.LearningCardView
import com.example.ui.components.MoodSelectorCard
import com.example.ui.components.TaskItemCard
import com.example.ui.components.getTimePeriodInfo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val motivationalMessages = listOf(
    "Peace begins with a single mindful breath.",
    "Small steps taken consistently shape your future.",
    "Be gentle with your expectations today.",
    "One thoughtful action at a time.",
    "Celebrate what you finish, release what you cannot.",
    "You do not need to hurry; what is meant for you will not pass.",
    "Rest is just as vital as progress.",
    "Today is a quiet opportunity to nurture yourself."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DailyPulseViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val todayDate by viewModel.todayDate.collectAsStateWithLifecycle()
    val tasks by viewModel.tasksForSelectedDate.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val habitCompletions by viewModel.habitCompletionsForSelectedDate.collectAsStateWithLifecycle()
    val currentMood by viewModel.moodForSelectedDate.collectAsStateWithLifecycle()
    val learningStates by viewModel.learningStates.collectAsStateWithLifecycle()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var habitToEdit by remember { mutableStateOf<HabitEntity?>(null) }

    var quickTaskTitle by remember { mutableStateOf("") }

    val isToday = selectedDate == todayDate

    // Day of year for motivational message and learning card
    val dayOfYear = remember(selectedDate) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val cal = Calendar.getInstance()
            cal.time = parser.parse(selectedDate) ?: Date()
            cal.get(Calendar.DAY_OF_YEAR)
        } catch (_: Exception) {
            1
        }
    }

    val dailyQuote = motivationalMessages[(dayOfYear % motivationalMessages.size + motivationalMessages.size) % motivationalMessages.size]
    val todayLearningCard = DailyLearningContent.getTodayCard(dayOfYear)
    val cardState = learningStates.find { it.cardId == todayLearningCard.id }

    var currentTimeString by remember {
        mutableStateOf(SimpleDateFormat("h:mm a", Locale.US).format(Date()))
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeString = SimpleDateFormat("h:mm a", Locale.US).format(Date())
            kotlinx.coroutines.delay(30_000)
        }
    }

    val timeInfo = remember(currentTimeString) {
        getTimePeriodInfo(Calendar.getInstance())
    }

    // Active habits for selected date
    val applicableHabits = remember(habits, selectedDate) {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        try {
            cal.time = parser.parse(selectedDate) ?: Date()
        } catch (_: Exception) {}
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val dayCode = when (dayOfWeek) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            Calendar.SUNDAY -> "SUN"
            else -> "ALL"
        }
        habits.filter { h ->
            h.targetDays == "ALL" || h.targetDays.contains(dayCode)
        }
    }

    val completedHabitCount = applicableHabits.count { h ->
        habitCompletions.any { it.habitId == h.id }
    }
    val completedTaskCount = tasks.count { it.isCompleted }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DailyPulse",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isToday) "$currentTimeString • ${DailyPulseViewModel.formatFriendlyDate(selectedDate)}" else DailyPulseViewModel.formatFriendlyDate(selectedDate),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (!isToday) {
                        IconButton(
                            onClick = { viewModel.selectToday() },
                            modifier = Modifier.testTag("home_jump_today")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Jump to Today",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Time-Aware Day Cycle & Meal Suggestion Card
            item {
                DayCycleWidget(
                    timeInfo = timeInfo,
                    currentTimeString = currentTimeString,
                    dateString = DailyPulseViewModel.formatFriendlyDate(selectedDate)
                )
            }

            // Motivational Quote
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "“$dailyQuote”",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // Day Score Card
            item {
                DayScoreCard(
                    tasksTotal = tasks.size,
                    tasksCompleted = completedTaskCount,
                    habitsTotal = applicableHabits.size,
                    habitsCompleted = completedHabitCount
                )
            }

            // Mood Check-in Card
            item {
                MoodSelectorCard(
                    currentMood = currentMood,
                    onSelectMood = { mood, note ->
                        viewModel.saveMood(mood, note)
                    }
                )
            }

            // Habits Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Habits",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "$completedHabitCount of ${applicableHabits.size} completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = { showAddHabitDialog = true },
                        modifier = Modifier.testTag("home_add_habit_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Add Habit")
                    }
                }
            }

            // Habits Items or Empty State
            if (applicableHabits.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🌱 No habits set for today",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add simple routines like drinking water or mindful breathing.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showAddHabitDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("home_first_habit_btn")
                            ) {
                                Text("Create Habit")
                            }
                        }
                    }
                }
            } else {
                items(applicableHabits, key = { it.id }) { habit ->
                    val isDone = habitCompletions.any { it.habitId == habit.id }
                    HabitItemCard(
                        habit = habit,
                        isCompletedToday = isDone,
                        onToggleComplete = {
                            viewModel.toggleHabitCompletion(habit.id)
                        },
                        onEdit = {
                            habitToEdit = habit
                        },
                        onDelete = {
                            viewModel.deleteHabit(habit)
                        }
                    )
                }
            }

            // Tasks Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Today's Tasks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "$completedTaskCount of ${tasks.size} done",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = { showAddTaskDialog = true },
                        modifier = Modifier.testTag("home_add_task_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Add Task")
                    }
                }
            }

            // Quick Inline Task Entry
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.OutlinedTextField(
                            value = quickTaskTitle,
                            onValueChange = { quickTaskTitle = it },
                            placeholder = {
                                Text(
                                    "Add a quick task for today...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_quick_task_input"),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                            )
                        )

                        IconButton(
                            onClick = {
                                if (quickTaskTitle.isNotBlank()) {
                                    viewModel.addTask(quickTaskTitle.trim(), null, "Medium")
                                    quickTaskTitle = ""
                                }
                            },
                            enabled = quickTaskTitle.isNotBlank(),
                            modifier = Modifier.testTag("home_quick_task_submit")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add quick task",
                                tint = if (quickTaskTitle.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            // Tasks Items or Empty State
            if (tasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📝 No tasks scheduled",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Take it easy or jot down 2-3 gentle priorities for today.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showAddTaskDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("home_first_task_btn")
                            ) {
                                Text("Add a Task")
                            }
                        }
                    }
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    TaskItemCard(
                        task = task,
                        onToggleComplete = { viewModel.toggleTask(task) },
                        onEdit = { taskToEdit = task },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }

            // Daily Learning Card
            item {
                Text(
                    text = "Daily Wisdom",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                LearningCardView(
                    card = todayLearningCard,
                    isLearned = cardState?.isLearned ?: false,
                    isFavorite = cardState?.isFavorite ?: false,
                    onToggleLearned = { viewModel.toggleLearningLearned(todayLearningCard.id) },
                    onToggleFavorite = { viewModel.toggleLearningFavorite(todayLearningCard.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Add / Edit Task Dialog
    if (showAddTaskDialog || taskToEdit != null) {
        AddEditTaskDialog(
            initialTask = taskToEdit,
            onDismiss = {
                showAddTaskDialog = false
                taskToEdit = null
            },
            onSave = { title, time, priority ->
                if (taskToEdit == null) {
                    viewModel.addTask(title, time, priority)
                } else {
                    viewModel.updateTask(
                        taskToEdit!!.copy(
                            title = title,
                            time = time,
                            priority = priority
                        )
                    )
                }
                showAddTaskDialog = false
                taskToEdit = null
            }
        )
    }

    // Add / Edit Habit Dialog
    if (showAddHabitDialog || habitToEdit != null) {
        AddEditHabitDialog(
            initialHabit = habitToEdit,
            onDismiss = {
                showAddHabitDialog = false
                habitToEdit = null
            },
            onSave = { title, icon, targetDays ->
                if (habitToEdit == null) {
                    viewModel.addHabit(title, icon, targetDays)
                } else {
                    viewModel.updateHabit(
                        habitToEdit!!.copy(
                            title = title,
                            iconName = icon,
                            targetDays = targetDays
                        )
                    )
                }
                showAddHabitDialog = false
                habitToEdit = null
            }
        )
    }
}
