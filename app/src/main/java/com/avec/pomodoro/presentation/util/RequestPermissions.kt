package com.avec.pomodoro.presentation.util


import android.Manifest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions() {
    val postNotificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val foregroundServicePermission = rememberPermissionState(permission = Manifest.permission.FOREGROUND_SERVICE)
    val wakeLockPermission = rememberPermissionState(permission = Manifest.permission.WAKE_LOCK)
    val vibratePermission = rememberPermissionState(permission = Manifest.permission.VIBRATE)

    LaunchedEffect(Unit) {
        listOf(
            postNotificationPermission,
            foregroundServicePermission,
            wakeLockPermission,
            vibratePermission
        ).forEach { permissionState ->
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}
