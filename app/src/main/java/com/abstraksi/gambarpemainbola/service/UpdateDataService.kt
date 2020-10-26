package com.abstraksi.gambarpemainbola.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.d
import com.abstraksi.gambarpemainbola.R
import com.abstraksi.gambarpemainbola.config.ALBUM
import com.abstraksi.gambarpemainbola.config.JUMLAH_PHOTO_PER_ALBUM
import com.abstraksi.gambarpemainbola.config.USERNAME
import com.abstraksi.gambarpemainbola.domain.repository.PhotoRepository
import com.abstraksi.gambarpemainbola.presentation.main.SplashActivity
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.math.ceil


class UpdateDataService() : IntentService("UpdateDataService") {

    private val disposables = CompositeDisposable()
    private val photoRepository: PhotoRepository by inject()

    override fun onCreate() {
        super.onCreate()
        sendNotification()
    }

    override fun onHandleIntent(p0: Intent?) {
        ALBUM.forEach {
            val album = it.toLowerCase()
            disposables.add(photoRepository.getRowCount(album)
                .subscribe({ total ->
                    val albumCount = ceil((total / JUMLAH_PHOTO_PER_ALBUM).toDouble()).toInt() + 1
                    updatePhoto("$album$albumCount")
                },{
                    Timber.e(it)
                }))
        }

    }

    private fun updatePhoto(album: String){
        disposables.add(photoRepository.getPhoto(USERNAME, album)
            .subscribe({
                d { "Photo updated" }
            },{
                Timber.e(it)
            }))
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    private fun sendNotification() {

        val notifIntent = Intent(this, SplashActivity::class.java)
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.update_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        notificationBuilder.apply {
            setSmallIcon(R.drawable.logo)
            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logo))
            setContentTitle(getString(R.string.app_name))
            setContentText(getString(R.string.update_photo))
            setAutoCancel(false)
            setSound(defaultSoundUri)
            setContentIntent(notifyPendingIntent)
        }


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.update_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(Random().nextInt(60000), notificationBuilder.build())

    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

}