package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.DailyPulseDao
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.JournalEntryEntity
import com.example.data.model.LearningCardStateEntity
import com.example.data.model.MoodEntryEntity
import com.example.data.model.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        MoodEntryEntity::class,
        JournalEntryEntity::class,
        LearningCardStateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DailyPulseDatabase : RoomDatabase() {

    abstract fun dailyPulseDao(): DailyPulseDao

    companion object {
        @Volatile
        private var INSTANCE: DailyPulseDatabase? = null

        fun getDatabase(context: Context): DailyPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DailyPulseDatabase::class.java,
                    "dailypulse_offline.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
