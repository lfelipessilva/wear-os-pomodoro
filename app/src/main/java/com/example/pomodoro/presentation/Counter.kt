package com.example.pomodoro.presentation

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.pomodoro.R
import kotlinx.coroutines.delay
import java.util.Locale


@Composable
fun Counter(navController: NavController) {
    val steps by remember { mutableStateOf(arrayOf(0, 1, 0, 1, 0, 2)) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var startTime by remember { mutableIntStateOf(25 * 60) }
    var timeLeft by remember { mutableIntStateOf(startTime) }
    var isRunning by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val progressAnimate by animateFloatAsState(
        targetValue = timeLeft.toFloat() / startTime.toFloat(),
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = 0,
            easing = LinearEasing
        ), label = ""
    )

    fun playPauseTimer() {
        isRunning = !isRunning
        vibrate(
            context,
            50L
        )
    }

    fun stopSkipTimer() {
        if (isRunning) {

            currentStepIndex = if (currentStepIndex + 1 < steps.size) currentStepIndex + 1 else 0

            val newTime =
                if (steps[currentStepIndex] == 0) 25 * 60 else if (steps[currentStepIndex] == 1) 5 * 60 else 15 * 60

            startTime = newTime
            timeLeft = newTime
        } else {
            navController.navigate("start_counter")
        }

    }

    LaunchedEffect(isRunning, startTime) {
        if (isRunning && timeLeft > 0) {
            while (isRunning && timeLeft > 0) {
                delay(1000L)
                timeLeft -= 1
            }
            if (timeLeft == 0) {
                stopSkipTimer()
                vibrate(
                    context,
                    500L
                )
            }
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeFormatted = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)

    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize(),
            startAngle = 270f,
            progress = progressAnimate,
            strokeWidth = 10.dp,
            indicatorColor = MaterialTheme.colorScheme.primary,
        )

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
                text = if (steps[currentStepIndex] == 0) "Focus" else if (steps[currentStepIndex] == 1) "Short Break" else "Long Break",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.LightGray
            )

            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                color = Color.White
            )

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

@Suppress("DEPRECATION")
fun vibrate(context: Context, duration: Long) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
}