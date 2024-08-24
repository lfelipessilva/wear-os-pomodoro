package com.avec.pomodoro.presentation.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import com.avec.pomodoro.presentation.util.createForegroundNotification
import com.avec.pomodoro.presentation.util.vibrate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
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
    private var wakeLock: PowerManager.WakeLock? = null

    private fun startTimer() {
        if (_isRunning.value) return

        timerJob = coroutineScope.launch {
            _isRunning.value = true

            while (_timeLeft.value > 0) {
                delay(1000) // 1-second delay
                _timeLeft.value -= 1000 // Decrement time by 1 second
            }

            _isRunning.value = false
            advanceToNextStep()
            vibrate(1000L, this@TimerService)
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isRunning.value = false
    }

    fun resumeTimer() {
        if (!_isRunning.value) startTimer()
    }

    fun resetTimer() {
        timerJob?.cancel()

        _currentStep.value = 0
        _totalTime.value = FOCUS_TIME
        _timeLeft.value = FOCUS_TIME
    }

    fun skipTimer() {
        pauseTimer()
        advanceToNextStep()
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

        startTimer()
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

    fun stopService() {
        stopSelf()
    }

    @SuppressLint("WakelockTimeout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

        wakeLock =
            powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                acquire()
            }

        startTimer()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock?.run {
            if (isHeld) release()
        }
    }
}
