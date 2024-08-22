package com.avec.pomodoro.presentation

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
import androidx.compose.material3.CircularProgressIndicator
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

        setContent {
            RequestPermissions()
            LoadingScreen()
        }

        val intent = Intent(this, TimerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
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
