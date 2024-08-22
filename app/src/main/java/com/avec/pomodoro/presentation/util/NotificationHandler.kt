package com.avec.pomodoro.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.avec.pomodoro.R
import com.avec.pomodoro.presentation.MainActivity
import com.avec.pomodoro.presentation.service.TimerService


fun createForegroundNotification(context: TimerService) {

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
        .setContentTitle("Pomodoro Timer")
        .setContentText("Timer is running")
        .setSmallIcon(android.R.drawable.star_on)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
            .setStatus(ongoingActivityStatus)
            .setTouchIntent(contentIntent)
            .build()

    ongoingActivity.apply(context)

    ServiceCompat.startForeground(
        context, 1, notificationBuilder.build(),
        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
    )
}

