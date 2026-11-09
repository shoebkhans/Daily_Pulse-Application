package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.learning.DailyLearningContent
import com.example.data.model.HabitEntity
import com.example.data.model.TaskEntity
import com.example.ui.DailyPulseViewModel
import com.example.ui.components.AddEditHabitDialog
import com.example.ui.components.AddEditTaskDialog
import com.example.ui.components.HabitItemCard
import com.example.ui.components.LearningCardView
import com.example.ui.components.MoodSelectorCard
import com.example.ui.components.TaskItemCard
import com.example.ui.components.getTimePeriodInfo
import com.example.ui.components.smooth3DTilt
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
    onNavigateToHabits: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val todayDate by viewModel.todayDate.collectAsStateWithLifecycle()
    val tasks by viewModel.tasksForSelectedDate.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val habitCompletions by viewModel.habitCompletionsForSelectedDate.collectAsStateWithLifecycle()
    val currentMood by viewModel.moodForSelectedDate.collectAsStateWithLifecycle()
    val learningStates by viewModel.learningStates.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()

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

    // Date formatted as: WEDNESDAY · 14 OCTOBER matching screenshot
    val formattedHeaderDate = remember(selectedDate) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(selectedDate) ?: Date()
            val dayNameFormat = SimpleDateFormat("EEEE", Locale.US).format(date).uppercase(Locale.US)
            val dayNumMonthFormat = SimpleDateFormat("d MMMM", Locale.US).format(date).uppercase(Locale.US)
            "$dayNameFormat · $dayNumMonthFormat"
        } catch (_: Exception) {
            "TODAY"
        }
    }

    // Time cycle info (e.g. morning, afternoon, evening meal suggestion)
    val timeInfo = remember {
        getTimePeriodInfo(Calendar.getInstance())
    }

    // Applicable habits for the selected date
    val applicableHabits = remember(habits, selectedDate) {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        try {
            cal.time = parser.parse(selectedDate) ?: Date()
        } catch (_: Exception) {}
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
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

    // Day Score calculation (0 to 100)
    val dayScore = remember(tasks, completedTaskCount, applicableHabits, completedHabitCount, currentMood) {
        val totalTrackers = tasks.size + applicableHabits.size + 1 // +1 for mood check-in
        if (totalTrackers == 1 && tasks.isEmpty() && applicableHabits.isEmpty()) {
            if (currentMood != null) 30 else 0
        } else {
            val moodScore = if (currentMood != null) 1 else 0
            val done = completedTaskCount + completedHabitCount + moodScore
            ((done.toFloat() / totalTrackers.toFloat()) * 100f).toInt().coerceIn(0, 100)
        }
    }

    val terracottaColor = Color(0xFFB85D43)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Calm Header directly aligned with reference image
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Terracotta uppercase date
                    Text(
                        text = formattedHeaderDate,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = terracottaColor,
                        modifier = Modifier.testTag("home_date_header")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Calm title
                    Text(
                        text = "A little at a time.",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            lineHeight = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Subtitle
                    Text(
                        text = "Your day, without the pressure.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Top right squircle badge with pulse icon and weather pill
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("home_top_pulse_badge")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = "DailyPulse",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Weather pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.clickable { viewModel.refreshWeather() }
                    ) {
                        Text(
                            text = weather.displayString,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Time-aware gentle nudge (Breakfast / Lunch / Dinner suggestion)
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = timeInfo.mealIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${timeInfo.greeting} • ${timeInfo.mealTitle}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // "TODAY AT A GLANCE" Card matching reference image
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .smooth3DTilt(maxRotationDegrees = 0.5f)
                    .testTag("home_glance_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TODAY AT A GLANCE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (dayScore > 0) "Good progress today." else "Start wherever you are.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add one intention, or simply check in.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Circular Day Score
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(76.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { (dayScore / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.size(76.dp),
                            color = terracottaColor,
                            trackColor = terracottaColor.copy(alpha = 0.18f),
                            strokeWidth = 6.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayScore.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "of 100",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Disclaimer beneath the card from reference screenshot
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Your score reflects tasks, habits, and a mood check-in—not how good your day was.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    lineHeight = 14.sp
                )
            }
        }

        // Daily Motivation Quote Line
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🌱", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "“$dailyQuote”",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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

        // "Your focus" Section Header (matching screenshot)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your focus",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier.testTag("home_focus_add_icon")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add focus task",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Quick Inline Task Field
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quickTaskTitle,
                        onValueChange = { quickTaskTitle = it },
                        placeholder = {
                            Text(
                                "Add a gentle focus task...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_quick_task_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
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
                            contentDescription = "Submit quick task",
                            tint = if (quickTaskTitle.isNotBlank()) terracottaColor else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // "Your focus" Items or Reference Screenshot Empty State Card
        if (tasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_empty_focus_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Nothing on the list yet",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Keep it light. Add just one thing that matters.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { showAddTaskDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = terracottaColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("home_add_focus_btn")
                        ) {
                            Text("+ Add", fontWeight = FontWeight.Bold)
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

        // "Your habits" Section Header (matching screenshot)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your habits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = onNavigateToHabits,
                    modifier = Modifier.testTag("home_habits_see_all_btn")
                ) {
                    Text(
                        text = "See all",
                        color = terracottaColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // "Your habits" Items or Reference Screenshot Card
        if (applicableHabits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_empty_habits_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Spa,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "A gentle routine can start with one small habit.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = { showAddHabitDialog = true },
                            modifier = Modifier.testTag("home_create_habit_btn")
                        ) {
                            Text(
                                text = "Create",
                                color = terracottaColor,
                                fontWeight = FontWeight.Bold
                            )
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

        // "A thought for today" Section (matching screenshot)
        item {
            Text(
                text = "A thought for today",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            LearningCardView(
                card = todayLearningCard,
                isLearned = cardState?.isLearned ?: false,
                isFavorite = cardState?.isFavorite ?: false,
                onToggleLearned = { viewModel.toggleLearningLearned(todayLearningCard.id) },
                onToggleFavorite = { viewModel.toggleLearningFavorite(todayLearningCard.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
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
