package com.example.pomodoro.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
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
    val startTime = 5 * 60
    var timeLeft by remember { mutableIntStateOf(startTime) }
    var isRunning by remember { mutableStateOf(false) }

    // Function to pause the timer
    fun playPauseTimer() {
        isRunning = !isRunning
    }

    // Function to reset the timer
    fun resetTimer() {
        timeLeft = startTime
        isRunning = false
    }

    // Timer countdown logic
    LaunchedEffect(isRunning) {
        if (isRunning && timeLeft > 0) {
            while (isRunning && timeLeft > 0) {
                delay(1000L)
                timeLeft -= 1
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
                .fillMaxSize()
                .padding(4.dp),
            startAngle = 270f,
            progress = timeLeft / startTime.toFloat(),
            strokeWidth = ProgressIndicatorDefaults.FullScreenStrokeWidth
        )

        Text(
            text = timeFormatted,
            style = MaterialTheme.typography.display1,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.align(Alignment.Center)
        )

        Row(

                    verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(space = 4.dp,alignment = Alignment.CenterHorizontally ),
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedButton(
                onClick = { resetTimer() },
                modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_stop_30),
                    contentDescription = "Start",
                    modifier = Modifier.size(ButtonDefaults.LargeIconSize)
                )
            }

            Button(
                onClick = { playPauseTimer() },
                modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
            ) {
                val iconId = if (isRunning) R.drawable.baseline_pause_30 else R.drawable.baseline_play_arrow_30
                val contentDescription = if (isRunning) "Pause" else "Play"

                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription,
                    modifier = Modifier.size(ButtonDefaults.LargeIconSize)
                )
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
