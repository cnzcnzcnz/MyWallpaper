package com.abstraksi.gambarpemainbola.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.abstraksi.gambarpemainbola.R
import com.abstraksi.gambarpemainbola.presentation.main.SplashActivity
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        p0.data.let {
            val isUpdate = it["update"].toString() == "yes"
            if (isUpdate) updateData()

            sendNotification(
                    it["title"].toString(),
                    it["body"].toString(),
                    it["moreapp"].toString()
            )
        }
    }

    private fun updateData() {
        val intent = Intent(this, UpdateDataService::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent)
        } else {
            startService(intent);
        }
    }

    private fun sendNotification(
            title: String,
            messageBody: String,
            moreApp: String = ""
    ) {

        val intent: Intent
        if (moreApp.isNotEmpty()) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(moreApp)
        } else {
            intent = Intent(this, SplashActivity::class.java)
        }

        val notifyPendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.app_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        notificationBuilder.apply {
            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logo))
            setSmallIcon(R.drawable.logo)
            setContentTitle(title)
            setContentText(messageBody)
            setAutoCancel(true)
            setSound(defaultSoundUri)
            setContentIntent(notifyPendingIntent)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    channelId,
                    getString(R.string.app_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random().nextInt(60000) /* ID of notification */, notificationBuilder.build())
    }

}