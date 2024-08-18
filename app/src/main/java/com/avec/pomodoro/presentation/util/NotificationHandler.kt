package com.avec.pomodoro.presentation.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.avec.pomodoro.R
import com.avec.pomodoro.presentation.MainActivity


fun createNotification(context: Context) {
    val notificationManager = context.getSystemService(NotificationManager::class.java)

    val notificationIntent = Intent(
        context,
        MainActivity::class.java
    )

    val contentIntent = PendingIntent.getActivity(
        context,
        1,
        notificationIntent,
        PendingIntent.FLAG_MUTABLE
    )

    val notificationBuilder = NotificationCompat.Builder(context, "pomodoro_notification_channel")
        .setContentTitle("Pomodoro")
        .setContentText("Pomodoro timer in running")
        .setSmallIcon(R.drawable.tomato_svgrepo_com)
        .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        .setContentIntent(contentIntent)
        .setOngoing(true)

    val ongoingActivityStatus = Status.Builder()
        .addTemplate("Pomodoro")
        .build()

    val ongoingActivity =
        OngoingActivity.Builder(
            context, 1, notificationBuilder
        )
            .setAnimatedIcon(R.drawable.tomato_svgrepo_com)
            .setStaticIcon(R.drawable.tomato_svgrepo_com)
            .setTouchIntent(contentIntent)
            .setStatus(ongoingActivityStatus)
            .build()


    ongoingActivity.apply(context)

    val notification = notificationBuilder.build()
    notificationManager.notify(1, notification)
}

fun deleteNotification(context: Context) {
    val notificationManager = context.getSystemService(NotificationManager::class.java)

    notificationManager.cancelAll()
}