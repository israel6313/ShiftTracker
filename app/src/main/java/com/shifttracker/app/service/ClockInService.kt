package com.shifttracker.app.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shifttracker.app.MainActivity
import com.shifttracker.app.R
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class ClockInService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var startTimeMs = 0L
    private var timerJob: Job? = null

    companion object {
        const val CHANNEL_ID = "clock_in_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP = "com.shifttracker.STOP_CLOCK"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) { stopSelf(); return START_NOT_STICKY }
        startTimeMs = System.currentTimeMillis()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("00:00:00"))
        timerJob = serviceScope.launch {
            while (true) {
                delay(1000)
                val elapsed = System.currentTimeMillis() - startTimeMs
                val formatted = formatElapsed(elapsed)
                updateNotification(formatted)
            }
        }
        return START_STICKY
    }

    private fun formatElapsed(ms: Long): String {
        val h = TimeUnit.MILLISECONDS.toHours(ms)
        val m = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val s = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    private fun buildNotification(time: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val stopIntent = Intent(this, ClockInService::class.java).apply { action = ACTION_STOP }
        val stopPending = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("במשמרת")
            .setContentText(time)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_delete, "סיים משמרת", stopPending)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(time: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(time))
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "מעקב משמרת", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { timerJob?.cancel(); serviceScope.cancel(); super.onDestroy() }
}
