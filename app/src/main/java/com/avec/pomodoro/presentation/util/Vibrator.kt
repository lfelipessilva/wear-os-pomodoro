package com.avec.pomodoro.presentation.util

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager

fun vibrate(time: Long, context: Context) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    val vibrator = vibratorManager.defaultVibrator
    vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
}