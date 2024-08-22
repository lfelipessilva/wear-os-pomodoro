package com.avec.pomodoro.presentation

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun Counter(navController: NavController, timerService: TimerService) {
    val steps by remember { mutableStateOf(arrayOf(0, 1, 0, 1, 0, 2)) }
    val isRunning by timerService.isRunning.collectAsState()
    val currentStep by timerService.currentStep.collectAsState()
    val context = LocalContext.current

    fun playPauseTimer() {
        vibrate(50L, context)
        if (isRunning)
            timerService.pauseTimer() else timerService.resumeTimer()
    }

    fun stopSkipTimer() {
        vibrate(100L, context)
        if (isRunning) {
            timerService.skipTimer()
        } else {
            timerService.resetTimer()
            navController.navigate("start_counter")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Progress(timerService = timerService)
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
                text = if (steps[currentStep] == 0) "Focus" else if (steps[currentStep] == 1) "Short Break" else "Long Break",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.LightGray
            )
            TimerDisplay(timerService)
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