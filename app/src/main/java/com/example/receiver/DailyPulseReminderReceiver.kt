package com.example.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import java.util.Calendar

class DailyPulseReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            showNotification(context)
            // Re-schedule for tomorrow to ensure daily delivery
            val hour = intent.getIntExtra("hour", 20)
            val minute = intent.getIntExtra("minute", 0)
            scheduleDailyAlarm(context, hour, minute)
        } catch (e: Throwable) {
            Log.e("DailyPulseReceiver", "Error on receive reminder", e)
        }
    }

    companion object {
        const val CHANNEL_ID = "dailypulse_daily_reminder"
        const val NOTIFICATION_ID = 1001
        private const val REQUEST_CODE = 2001

        fun showNotification(context: Context) {
            try {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                        ?: return

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        "Daily Gentle Reminder",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description = "Peaceful daily check-in and habits reminder"
                    }
                    notificationManager.createNotificationChannel(channel)
                }

                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("DailyPulse 🌿")
                    .setContentText("A quiet moment for you. Check in on your habits and reflect on your day.")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("A quiet moment for you. Check in on your habits, celebrate small steps, and reflect on today in peace.")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                notificationManager.notify(NOTIFICATION_ID, builder.build())
            } catch (e: Throwable) {
                Log.w("DailyPulseReceiver", "Cannot show notification: ${e.message}")
            }
        }

        fun scheduleDailyAlarm(context: Context, hour: Int, minute: Int) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                    ?: return

                val intent = Intent(context, DailyPulseReminderReceiver::class.java).apply {
                    putExtra("hour", hour)
                    putExtra("minute", minute)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } catch (e: Throwable) {
                Log.w("DailyPulseReceiver", "Cannot schedule exact alarm, falling back: ${e.message}")
            }
        }

        fun cancelDailyAlarm(context: Context) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                    ?: return
                val intent = Intent(context, DailyPulseReminderReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
            } catch (e: Throwable) {
                Log.w("DailyPulseReceiver", "Cannot cancel alarm: ${e.message}")
            }
        }
    }
}
