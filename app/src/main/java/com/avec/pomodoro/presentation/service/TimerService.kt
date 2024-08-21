package com.avec.pomodoro.presentation.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.avec.pomodoro.presentation.util.createNotification
import kotlin.random.Random

class TimerService : Service() {
    // Binder given to clients.
    private val binder = LocalBinder()

    // Random number generator.
    private val mGenerator = Random(123)

    /** Method for clients.  */
    val randomNumber: Int
        get() = mGenerator.nextInt(100)

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotification(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}
