package com.example.pomodoro.presentation

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
    var mode by remember { mutableIntStateOf(0) }
    var startTime by remember { mutableIntStateOf(25 * 60) }
    var timeLeft by remember { mutableIntStateOf(startTime) }
    var isRunning by remember { mutableStateOf(true) }

    // Function to pause the timer
    fun playPauseTimer() {
        isRunning = !isRunning
    }

    fun stopSkipTimer() {
        // should skip
        if (isRunning) {
            startTime = if (mode == 0) 5 * 60 else 25 * 60
            timeLeft = if (mode == 0) 5 * 60 else 25 * 60

            mode = if (mode == 0) 1 else 0
            return
        } else {
            navController.navigate("start_counter")
            return
        }

    }

    // Timer countdown logic
    LaunchedEffect(isRunning) {
        if (isRunning && timeLeft > 0) {
            while (isRunning && timeLeft > 0) {
                delay(1000L)
                timeLeft -= 1
            }
            if (timeLeft == 0) {
                stopSkipTimer()
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
            progress = timeLeft.toFloat() / startTime.toFloat(),
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
                text = if (mode == 0) "Focus" else "Break",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.LightGray
            )

            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.displayLarge,
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
