package com.douglaspac.mobiledatasaver

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import com.douglaspac.mobiledatasaver.broadcast.AlarmReceiverRegister
import com.douglaspac.mobiledatasaver.persister.MySharedPref
import com.douglaspac.mobiledatasaver.utils.logger
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val alarmReceiverRegister = AlarmReceiverRegister(this)

        switch_turn_on_off.isChecked = MySharedPref.isTurnOn(this)
        switch_turn_on_off.setOnCheckedChangeListener { _, isChecked ->
            MySharedPref.setTurnOn(this, isChecked)

            logger().info("Mobile Data Saver turn on: $isChecked")
            when {
                isChecked -> alarmReceiverRegister.register()
                else -> alarmReceiverRegister.cancel()
            }
        }
    }
}

class MobileDataSaver(private val ctx: Context) : Runnable {
    private val trafficMobileTotal by lazy { TrafficStats.getMobileRxBytes() + TrafficStats.getMobileRxBytes() }

    override fun run() {
        if (!canRun()) return

        val lastTotalMobileUsage = MySharedPref.getTotalMobileUsage(ctx)
        val diff = trafficMobileTotal - lastTotalMobileUsage

        if (diff > 5000000L) {
            showNotification("GastouMalandro", diff.toString())
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

        val lastVerified = MySharedPref.getLastVerifiedTime(ctx)
        val tenAgo = System.currentTimeMillis() - (10 * 60 * 1000)
        if (lastVerified < tenAgo) {
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

    private fun showNotification(title: String, body: String) {
        val intent = Intent(ctx, this::class.java)
        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = 1
        val channelId = "channel-01"
        val channelName = "Channel Name"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val mBuilder = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle(title)
            .setContentText(body)

        val stackBuilder = TaskStackBuilder.create(ctx)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)

        notificationManager.notify(notificationId, mBuilder.build())
    }
}