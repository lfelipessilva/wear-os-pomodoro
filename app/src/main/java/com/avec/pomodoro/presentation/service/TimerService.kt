package com.avec.pomodoro.presentation.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.avec.pomodoro.R
import com.avec.pomodoro.presentation.MainActivity
import com.avec.pomodoro.presentation.util.createForegroundNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimerService : Service() {
    companion object {
        private const val FOCUS_TIME = 25 * 60 * 1000L // 25 minutes
        private const val SHORT_BREAK_TIME = 5 * 60 * 1000L // 5 minutes
        private const val LONG_BREAK_TIME = 15 * 60 * 1000L // 15 minutes
    }

    private val binder = LocalBinder()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

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

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createForegroundNotification(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
