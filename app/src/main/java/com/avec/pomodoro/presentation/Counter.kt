package com.avec.pomodoro.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.avec.pomodoro.R
import com.avec.pomodoro.presentation.service.TimerService
import com.avec.pomodoro.presentation.util.vibrate
import java.util.Locale


@Composable
fun Counter(navController: NavController) {
    val context = LocalContext.current
    var timerService by remember { mutableStateOf<TimerService?>(null) }
    var isTimerServiceBound by remember { mutableStateOf(false) }

    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as TimerService.LocalBinder
                timerService = binder.getService()
                isTimerServiceBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                timerService = null
                isTimerServiceBound = false
            }
        }
    }

    LaunchedEffect(Unit) {
        val intent = Intent(context, TimerService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    DisposableEffect(Unit) {
        onDispose {
            context.unbindService(connection)
            timerService?.stopService()
        }
    }

    if (!isTimerServiceBound) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(indicatorColor = MaterialTheme.colorScheme.primary)
        }
        return
    }

    timerService?.let { service ->
        val steps by remember { mutableStateOf(arrayOf(0, 1, 0, 1, 0, 2)) }
        val isRunning by service.isRunning.collectAsState()
        val currentStep by service.currentStep.collectAsState()

        fun playPauseTimer() {
            vibrate(50L, context)
            if (isRunning) service.pauseTimer() else service.resumeTimer()
        }

        fun stopSkipTimer() {
            vibrate(100L, context)
            if (isRunning) {
                service.skipTimer()
            } else {
                service.resetTimer()
                navController.navigate("start_counter")
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Progress(timerService = service)
            TimeText(modifier = Modifier.padding(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = when (steps[currentStep]) {
                        0 -> "Focus"
                        1 -> "Short Break"
                        else -> "Long Break"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.LightGray
                )
                TimerDisplay(service)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 16.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                ) {
                    Button(
                        onClick = { playPauseTimer() },
                        modifier = Modifier.size(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(borderColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        val iconId =
                            if (isRunning) R.drawable.baseline_pause_30 else R.drawable.baseline_play_arrow_30
                        val contentDescription = if (isRunning) "Pause" else "Play"

                        Icon(
                            painter = painterResource(id = iconId),
                            contentDescription,
                        )
                    }

                    OutlinedButton(
                        onClick = { stopSkipTimer() },
                        modifier = Modifier.size(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(borderColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        val iconId =
                            if (isRunning) R.drawable.baseline_skip_next_30 else R.drawable.baseline_stop_30
                        val contentDescription = if (isRunning) "Skip" else "Stop"

                        Icon(
                            painter = painterResource(id = iconId),
                            contentDescription,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimerDisplay(timerService: TimerService) {
    val timeLeft by timerService.timeLeft.collectAsState()
    val minutes = timeLeft / 1000 / 60
    val seconds = timeLeft / 1000 % 60
    val timeFormatted = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)

    Text(
        text = timeFormatted,
        style = MaterialTheme.typography.displayMedium,
        textAlign = TextAlign.Center,
        color = Color.White
    )
}

@Composable
fun Progress(timerService: TimerService) {
    val timeLeft by timerService.timeLeft.collectAsState()
    val totalTime by timerService.totalTime.collectAsState()

    val progressAnimate by animateFloatAsState(
        targetValue = timeLeft.toFloat() / totalTime.toFloat(),
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = 0,
            easing = LinearEasing
        ), label = ""
    )

    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize(),
        startAngle = 270f,
        progress = progressAnimate,
        strokeWidth = 10.dp,
        indicatorColor = MaterialTheme.colorScheme.primary,
    )
}