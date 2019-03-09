package com.douglaspac.wifireminder.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.douglaspac.wifireminder.WifiReminder
import com.douglaspac.wifireminder.utils.INTERVAL_BETWEEN_JOBS

class AlarmReceiverRegister(ctx: Context) {
    private val alarmIntent = Intent(ctx, AlarmReceiver::class.java)
    private val pendingIntent =  PendingIntent.getBroadcast(ctx, 0, alarmIntent, 0)
    private val manager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun register() {
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL_BETWEEN_JOBS, pendingIntent)
    }

    fun cancel() {
        manager.cancel(pendingIntent)
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        WifiReminder(context).run()
    }
}