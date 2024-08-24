package com.avec.pomodoro.presentation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.avec.pomodoro.R
import com.avec.pomodoro.presentation.service.TimerService

@Composable
fun StartCounter(navController: NavController) {
    var isTimerServiceRunning = false
    val context = LocalContext.current

    val intent = Intent(context, TimerService::class.java)

    fun handleClick() {
        if (isTimerServiceRunning) {
            context.stopService(intent)
            isTimerServiceRunning = false
        } else {
            context.startForegroundService(intent)
            isTimerServiceRunning = true
            navController.navigate("counter")
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = { handleClick() },
            modifier = Modifier.size(88.dp) // Extra large button
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_play_arrow_30),
                contentDescription = "Start",
            )
        }
    }
}