package com.avec.pomodoro.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.avec.pomodoro.R
import com.avec.pomodoro.presentation.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerService : Service() {

    private val binder = LocalBinder()

    private val _timeLeft = MutableStateFlow(FOCUS_TIME)
    private val _totalTime = MutableStateFlow(FOCUS_TIME)
    private val _isRunning = MutableStateFlow(false)
    private val _currentStep = MutableStateFlow(0)
    private val steps = arrayOf(0, 1, 0, 1, 0, 2)

    val timeLeft: StateFlow<Long> get() = _timeLeft
    val totalTime: StateFlow<Long> get() = _totalTime
    val isRunning: StateFlow<Boolean> get() = _isRunning
    val currentStep: StateFlow<Int> get() = _currentStep

    private var timerJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundService()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Clean up coroutine scope on destroy
    }

    fun startTimer() {
        if (_isRunning.value) return

        timerJob = coroutineScope.launch {
            _isRunning.value = true

            while (_timeLeft.value > 0) {
                delay(1000) // 1-second delay
                _timeLeft.value -= 1000 // Decrement time by 1 second
            }

            _isRunning.value = false
            advanceToNextStep()
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isRunning.value = false
    }

    fun resumeTimer() {
        if (!_isRunning.value) startTimer()
    }

    fun skipTimer() {
        pauseTimer()
        advanceToNextStep()
        startTimer()
    }

    private fun advanceToNextStep() {
        _currentStep.value = (_currentStep.value + 1) % steps.size
        val stepDuration = when (steps[_currentStep.value]) {
            0 -> FOCUS_TIME
            1 -> SHORT_BREAK_TIME
            else -> LONG_BREAK_TIME
        }
        _totalTime.value = stepDuration
        _timeLeft.value = stepDuration
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "pomodoro_notification_channel"
            val channelName = "Pomodoro Timer Service"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for Pomodoro Timer Service"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val channelId = "pomodoro_notification_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Pomodoro Timer")
            .setContentText("Pomodoro timer is running")
            .setSmallIcon(R.drawable.tomato_svgrepo_com)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(createPendingIntent()) // Add pending intent
            .setAutoCancel(false) // Do not dismiss on click

        return notificationBuilder.build()
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val FOCUS_TIME = 25 * 60 * 1000L // 25 minutes
        private const val SHORT_BREAK_TIME = 5 * 60 * 1000L // 5 minutes
        private const val LONG_BREAK_TIME = 15 * 60 * 1000L // 15 minutes
    }
}
