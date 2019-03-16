package com.douglaspac.wifireminder

import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.text.format.Formatter
import com.douglaspac.wifireminder.broadcast.MuteReceiver
import com.douglaspac.wifireminder.broadcast.TurnOnWifiReceiver
import com.douglaspac.wifireminder.persister.MySharedPref
import com.douglaspac.wifireminder.utils.EXTRA_NOTIFICATION_ID

class WifiReminder(private val ctx: Context) : Runnable {
    private val trafficMobileTotal by lazy { TrafficStats.getMobileTxBytes() + TrafficStats.getMobileRxBytes() }

    override fun run() {
        if (!canRun()) return

        val lastTotalMobileUsage = MySharedPref.getTotalMobileUsage(ctx)
        val diff = trafficMobileTotal - lastTotalMobileUsage

        if (diff > 5000000L) {
            showWiFiReminderNotification(diff)
        }

        resetMobileDataValues()
    }

    private fun canRun(): Boolean {
        val myKM = ctx.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val isPhoneLocked = myKM.isKeyguardLocked
        if (isPhoneLocked) {
            return false
        }

        if (!isOnlyMobileNetworkConnected()) {
            return false
        }

        if (trafficMobileTotal == 0L) {
            return false
        }

        val now = System.currentTimeMillis()
        val lastVerified = MySharedPref.getLastVerifiedTime(ctx)
        val tenAgo = now - (10 * 60 * 1000)
        if (lastVerified < tenAgo) {
            resetMobileDataValues()
            return false
        }

        val muteUntil = MySharedPref.getMuteUntil(ctx)
        if (muteUntil > now) {
            resetMobileDataValues()
            return false
        }

        return true
    }

    private fun resetMobileDataValues() {
        MySharedPref.setTotalMobileUsage(ctx, trafficMobileTotal)
        MySharedPref.setLastVerifiedTime(ctx, System.currentTimeMillis())
    }

    private fun isOnlyMobileNetworkConnected(): Boolean {
        val connMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn = false
        var isMobileConn = false

        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network).apply {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn = isWifiConn or isConnected
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected
                }
            }
        }

        return !isWifiConn && isMobileConn
    }

    private fun showWiFiReminderNotification(diffInBytes: Long) {
        val diffInMegaBytesFormatted = Formatter.formatShortFileSize(ctx, diffInBytes)

        val notificationId = ctx.packageName.length + Math.random().toInt()
        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(
                "channel-01", this.javaClass.simpleName, NotificationManager.IMPORTANCE_HIGH
            ))
        }

        val intentTurnOnWiFi = Intent(ctx, TurnOnWifiReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val pendingIntentTurnOnWiFi = PendingIntent.getBroadcast(ctx, 1, intentTurnOnWiFi, PendingIntent.FLAG_UPDATE_CURRENT)

        val intentMute = Intent(ctx, MuteReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val pendingIntentMute = PendingIntent.getBroadcast(ctx, 1, intentMute, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(ctx, "channel-01").apply {
            this.setSmallIcon(R.drawable.ic_launcher_foreground)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.color = ctx.resources.getColor(R.color.colorPrimary, ctx.theme)
            } else {
                this.color = ctx.resources.getColor(R.color.colorPrimary)
            }
            this.setContentTitle(ctx.getString(R.string.notification_title))
            this.setContentText(ctx.getString(R.string.notification_body, diffInMegaBytesFormatted))
            this.setAutoCancel(true)
            this.addAction(0, ctx.getString(R.string.turn_on_wifi), pendingIntentTurnOnWiFi)
            this.addAction(0, ctx.getString(R.string.button_mute_for_one_hour), pendingIntentMute)
        }

        notificationManager.notify(notificationId, builder.build())
    }
}