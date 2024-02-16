package com.example.mc_homework

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.reactivex.rxjava3.core.Notification

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                "channel_id",
                "channel_name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "channel_id")
            .setContentTitle("Title")
            .setContentText("Content")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        notificationManager.notify(1, notification)
    }
}