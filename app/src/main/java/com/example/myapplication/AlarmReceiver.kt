package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        showNotification(context, "Проверьте курс валют", "Нажмите для обновления данных")
    }
}

fun showNotification(context: Context, title: String, message: String) {

//    Log.d("AlarmReceiver", Build.VERSION.RELEASE.toString())

    var notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    // Создаем канал для Android 8.0+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Log.d("TAG", "Alarm received!")
        var channel = NotificationChannel(
            "currency_channel",
            "Курс валют",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, "currency_channel")
        .setSmallIcon(R.drawable.notification)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setDefaults(Notification.DEFAULT_SOUND and Notification.DEFAULT_VIBRATE)
        .build()
    Log.d("TAG", "Alarm received!")
    notificationManager.notify(42, notification)
}