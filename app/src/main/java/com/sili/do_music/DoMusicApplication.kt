package com.sili.do_music

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.sili.do_music.util.Constants.Companion.CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DoMusicApplication : Application() {

    override fun onCreate(){
        super.onCreate()
        createNotificationChannels()
    }

    //Check if the Android version is greater than 8. (Android Oreo)
    private fun createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_ID,
                "Progress Notification",
                //IMPORTANCE_HIGH = shows a notification as peek notification.
                //IMPORTANCE_LOW = shows the notification in the status bar.
                NotificationManager.IMPORTANCE_LOW
            )
            channel1.description = "Progress Notification Channel"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)
        }
    }
}
