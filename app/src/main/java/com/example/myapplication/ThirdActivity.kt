package com.example.myapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ThirdActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val backButton = findViewById<Button>(R.id.backButton)
        val alarmToggle = findViewById<Switch>(R.id.alarmToggle)
        val sharedPreferences = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)

        backButton.setOnClickListener {
//            showNotification(this, "Тест", "Это тестовое уведомление")
            finish()
        }

        // Загружаем текущее состояние
        alarmToggle.isChecked = sharedPreferences.getBoolean("alarm_enabled", false)

        // Обработка переключения
        alarmToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startAlarm()
            } else {
                stopAlarm()
            }
            sharedPreferences.edit().putBoolean("alarm_enabled", isChecked).apply()
        }
    }

    private fun startAlarm() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Устанавливаем повторяющийся Alarm
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 60 * 1000L,
            60 * 1000L,
            pendingIntent
        )

        Toast.makeText(this, "Уведомления включены", Toast.LENGTH_SHORT).show()
    }

    private fun stopAlarm() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Уведомления выключены", Toast.LENGTH_SHORT).show()
    }
}