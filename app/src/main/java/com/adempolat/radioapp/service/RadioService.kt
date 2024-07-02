package com.adempolat.radioapp.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.adempolat.radioapp.R
import com.adempolat.radioapp.data.RadioList
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

private const val CHANNEL_ID = "RadioServiceChannel"
private const val NOTIFICATION_ID = 1

class RadioService : Service() {

    private var exoPlayer: ExoPlayer? = null
    private var currentRadioIndex = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "STOP_RADIO" -> stopRadio()
            "PLAY_RADIO" -> {
                val radioIndex = intent.getIntExtra("RADIO_INDEX", currentRadioIndex)
                playRadio(radioIndex)
            }
            "NEXT_RADIO" -> nextRadio()
            "PREV_RADIO" -> prevRadio()
            else -> playRadio(currentRadioIndex)
        }
        return START_STICKY
    }

    private fun playRadio(index: Int) {
        currentRadioIndex = index
        val radioUrl = RadioList.radioUrls[currentRadioIndex]
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(this).build().apply {
                val mediaItem = MediaItem.fromUri(radioUrl)
                setMediaItem(mediaItem)
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        Log.d("RadioService", "ExoPlayer is playing: $isPlaying")
                        showNotification(isPlaying)
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Log.e("RadioService", "ExoPlayer error: ${error.message}")
                    }
                })
                prepare()
            }
        } else {
            exoPlayer?.apply {
                val mediaItem = MediaItem.fromUri(radioUrl)
                setMediaItem(mediaItem)
                prepare()
                play()
            }
        }
        sendRadioNameBroadcast()
    }

    private fun stopRadio() {
        exoPlayer?.pause()
        Log.d("RadioService", "ExoPlayer paused")
        showNotification(false)
    }

    private fun nextRadio() {
        if (currentRadioIndex < RadioList.radioUrls.size - 1) {
            currentRadioIndex++
            playRadio(currentRadioIndex)
        }
    }

    private fun prevRadio() {
        if (currentRadioIndex > 0) {
            currentRadioIndex--
            playRadio(currentRadioIndex)
        }
    }

    private fun sendRadioNameBroadcast() {
        val intent = Intent("RADIO_UPDATE")
        intent.putExtra("RADIO_NAME", RadioList.radioNames[currentRadioIndex])
        sendBroadcast(intent)
    }

    private fun showNotification(isPlaying: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val playIntent = Intent(this, RadioService::class.java).apply {
            action = if (isPlaying) "STOP_RADIO" else "PLAY_RADIO"
        }
        val playPendingIntent = PendingIntent.getService(
            this,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(this, RadioService::class.java).apply {
            action = "NEXT_RADIO"
        }
        val nextPendingIntent = PendingIntent.getService(
            this,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = Intent(this, RadioService::class.java).apply {
            action = "PREV_RADIO"
        }
        val prevPendingIntent = PendingIntent.getService(
            this,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(RadioList.radioNames[currentRadioIndex])
            .setContentText(if (isPlaying) "Oynatılıyor" else "Durduruldu")
            .setSmallIcon(R.drawable.radio)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.skip_previous_24px,
                    "Önceki",
                    prevPendingIntent
                )
            )
            .addAction(
                NotificationCompat.Action(
                    R.drawable.pause,
                    if (isPlaying) "Durdur" else "Oynat",
                    playPendingIntent
                )
            )
            .addAction(
                NotificationCompat.Action(
                    R.drawable.skip_next_24px,
                    "Sonraki",
                    nextPendingIntent
                )
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isPlaying)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Radio Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                lightColor = Color.BLUE
                lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        exoPlayer?.release()
        exoPlayer = null
        super.onDestroy()
        Log.d("RadioService", "ExoPlayer released")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
