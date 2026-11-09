package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.DailyPulseDatabase
import com.example.data.learning.DailyLearningContent
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: DailyPulseDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DailyPulseDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DailyPulse", appName)
    }

    @Test
    fun `tasks insertion and retrieval works correctly`() = runBlocking {
        val dao = db.dailyPulseDao()
        val task = TaskEntity(
            title = "Morning Meditation",
            time = "08:00 AM",
            priority = "High",
            date = "2026-11-08"
        )
        val id = dao.insertTask(task)
        assertTrue(id > 0)

        val tasks = dao.getTasksForDate("2026-11-08").first()
        assertEquals(1, tasks.size)
        assertEquals("Morning Meditation", tasks[0].title)
        assertEquals("High", tasks[0].priority)
    }

    @Test
    fun `habits duplicate completion is prevented on same date`() = runBlocking {
        val dao = db.dailyPulseDao()
        val habitId = dao.insertHabit(
            HabitEntity(title = "Drink 2L Water", iconName = "water", targetDays = "ALL")
        )

        // Insert first completion
        val c1 = dao.insertCompletion(HabitCompletionEntity(habitId = habitId, date = "2026-11-08"))
        assertTrue(c1 > 0)

        // Attempt duplicate insertion
        val c2 = dao.insertCompletion(HabitCompletionEntity(habitId = habitId, date = "2026-11-08"))
        // OnConflictStrategy.IGNORE returns -1 or ignores duplicate
        val completions = dao.getCompletionsForDate("2026-11-08").first()
        assertEquals(1, completions.size)
    }

    @Test
    fun `learning content rotates and has valid practical tips`() {
        val card = DailyLearningContent.getTodayCard(10)
        assertNotNull(card)
        assertTrue(card.title.isNotBlank())
        assertTrue(card.practicalTip.isNotBlank())
    }

    @Test
    fun `main navigation has exactly 5 tabs`() {
        val tabs = com.example.ui.DailyPulseTab.values()
        assertEquals(5, tabs.size)
        assertEquals("Today", tabs[0].title)
        assertEquals("Habits", tabs[1].title)
        assertEquals("Reflect", tabs[2].title)
        assertEquals("Insights", tabs[3].title)
        assertEquals("Settings", tabs[4].title)
    }

    @Test
    fun `weather fallback provides valid temperature and status`() {
        val weather = com.example.data.weather.WeatherService.getCachedWeather()
        assertNotNull(weather)
        assertTrue(weather.temperatureC in -50..60)
        assertTrue(weather.emoji.isNotBlank())
    }
}
