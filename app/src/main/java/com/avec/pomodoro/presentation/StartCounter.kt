package com.avec.pomodoro.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import com.avec.pomodoro.R
import com.avec.pomodoro.presentation.util.deleteNotification

@Composable
fun StartCounter(navController: NavController) {
    val context = LocalContext.current
    deleteNotification(context)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {navController.navigate("counter") },
            modifier = Modifier.size(88.dp) // Extra large button
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_play_arrow_30),
                contentDescription = "Start",
            )
        }

    }
}