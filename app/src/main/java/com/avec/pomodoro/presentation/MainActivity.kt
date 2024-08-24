package com.avec.pomodoro.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avec.pomodoro.presentation.theme.PomodoroTheme
import com.avec.pomodoro.presentation.util.RequestPermissions

class MainActivity : ComponentActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        installSplashScreen()

        createNotificationChannel()

        setContent {
            RequestPermissions()
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    PomodoroTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "start_counter") {
            composable("start_counter") {
                StartCounter(
                    navController = navController,
                )
            }
            composable("counter") {
                Counter(
                    navController = navController,
                )
            }
        }
    }
}
