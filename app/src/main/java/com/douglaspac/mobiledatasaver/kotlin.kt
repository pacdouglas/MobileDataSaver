package com.douglaspac.mobiledatasaver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v4.app.NotificationCompat
import java.util.logging.Logger

fun Any.logger(): Logger = Logger.getLogger(this.javaClass.name)

fun Context.showNotification(title: String, body: String, drawable: Drawable? = null) {
    val intent = Intent(this, this::class.java)
    val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notificationId = 1
    val channelId = "channel-01"
    val channelName = "Channel Name"

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val mChannel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(mChannel)
    }

    val mBuilder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.notification_icon_background)
        .setContentTitle(title)
        .setContentText(body)

    val stackBuilder = TaskStackBuilder.create(this)
    stackBuilder.addNextIntent(intent)
    val resultPendingIntent = stackBuilder.getPendingIntent(
        0,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    mBuilder.setContentIntent(resultPendingIntent)

    notificationManager.notify(notificationId, mBuilder.build())
}
