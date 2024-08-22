package com.avec.pomodoro.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avec.pomodoro.presentation.service.TimerService
import com.avec.pomodoro.presentation.theme.PomodoroTheme
import com.avec.pomodoro.presentation.util.RequestPermissions

class MainActivity : ComponentActivity() {
    private lateinit var timerService: TimerService
    private var isTimerServiceBound: Boolean = false

    private fun createNotificationChannel() {
        val channelId = "pomodoro_notification_channel"
        val channelName = "Pomodoro Notifications"
        val channelDescription = "Pomodoro notification channel"
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = channelDescription
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            timerService = binder.getService()
            isTimerServiceBound = true

            setContent {
                WearApp(timerService)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isTimerServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        installSplashScreen()


        val intent = Intent(this, TimerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        this.startForegroundService(intent)

        createNotificationChannel()

        setContent {
            RequestPermissions()
            LoadingScreen()
        }

    }

    override fun onStop() {
        super.onStop()
        if (isTimerServiceBound) {
            unbindService(connection)
            isTimerServiceBound = false
        }
    }
}

@Composable
fun LoadingScreen() {
    PomodoroTheme {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(indicatorColor = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun WearApp(timerService: TimerService) {
    PomodoroTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "start_counter") {
            composable("start_counter") {
                StartCounter(
                    navController = navController,
                    timerService = timerService
                )
            }
            composable("counter") {
                Counter(
                    navController = navController,
                    timerService = timerService
                )
            }
        }
    }
}
