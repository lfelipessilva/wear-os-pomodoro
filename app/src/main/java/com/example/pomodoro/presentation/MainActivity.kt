package com.example.pomodoro.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.pomodoro.R
import com.example.pomodoro.presentation.theme.PomodoroTheme
import kotlinx.coroutines.delay
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Set the theme for the activity
        setTheme(android.R.style.Theme_DeviceDefault)

        // Set the content view with Jetpack Compose
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    PomodoroTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Counter()
        }
    }
}

@Composable
fun Counter() {
    var mode by remember { mutableIntStateOf(0) }
    var startTime by remember { mutableIntStateOf(25 * 60) }
    var timeLeft by remember { mutableIntStateOf(startTime) }
    var isRunning by remember { mutableStateOf(false) }

    // Function to pause the timer
    fun playPauseTimer() {
        isRunning = !isRunning
    }

    fun finish() {
        isRunning = false

        startTime = if (mode == 0) 25 * 60 else 5 * 60
        timeLeft = if (mode == 0) 25 * 60 else 5 * 60

        mode = if (mode == 0) 1 else 0

    }

    // Timer countdown logic
    LaunchedEffect(isRunning) {
        if (isRunning && timeLeft > 0) {
            while (isRunning && timeLeft > 0) {
                delay(1000L)
                timeLeft -= 1
            }
            if (timeLeft == 0) {
                finish()
            }
        }
    }

    // Format time as MM:SS
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeFormatted = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)

    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize(),
            startAngle = 270f,
            progress = timeLeft.toFloat() / startTime.toFloat(),
            strokeWidth = 10.dp
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
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = Color.LightGray
            )

            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.display1,
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
                OutlinedButton(
                    onClick = { finish() },
                    modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_check_30),
                        contentDescription = "Finish",
                    )
                }

                Button(
                    onClick = { playPauseTimer() },
                    modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
                ) {
                    val iconId =
                        if (isRunning) R.drawable.baseline_pause_30 else R.drawable.baseline_play_arrow_30
                    val contentDescription = if (isRunning) "Pause" else "Play"

                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription,
                    )
                }
            }

        }

    }
}

@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
