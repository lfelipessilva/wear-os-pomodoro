/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.pomodoro.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.pomodoro.R
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.pomodoro.presentation.theme.PomodoroTheme
import kotlinx.coroutines.delay
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.ProgressIndicatorDefaults
import java.util.Locale

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

    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1
        }
    }

    val minutes = (timeLeft / 60)
    val seconds = (timeLeft % 60)
    val timeFormatted = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)

    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = timeFormatted
    )

    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 1.dp),
        startAngle = 270f,
        progress = timeLeft.toFloat() / startTime.toFloat(),
        strokeWidth = ProgressIndicatorDefaults.FullScreenStrokeWidth
    )

    Row(verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp,alignment = Alignment.CenterHorizontally ),
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.size(ButtonDefaults.SmallButtonSize)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_play_arrow_30),
                contentDescription = "airplane",
                modifier = Modifier
                    .size(ButtonDefaults.LargeIconSize).wrapContentSize(align = Alignment.Center),
            )
        }

    Button(onClick = { /*TODO*/ }, modifier = Modifier.size(ButtonDefaults.SmallButtonSize)) {

        Icon(
            painter = painterResource(id = R.drawable.baseline_pause_30),
            contentDescription = "airplane",
            modifier = Modifier
                .size(ButtonDefaults.LargeIconSize).wrapContentSize(align = Alignment.Center),
        )
    }


    }
}


@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}


