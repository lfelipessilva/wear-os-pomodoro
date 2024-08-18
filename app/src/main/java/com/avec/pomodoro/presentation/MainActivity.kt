package com.avec.pomodoro.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avec.pomodoro.presentation.theme.PomodoroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    PomodoroTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "start_counter") {
            composable("start_counter") { StartCounter(navController = navController) }
            composable("counter") { Counter(navController = navController) }
        }
    }
}


@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
