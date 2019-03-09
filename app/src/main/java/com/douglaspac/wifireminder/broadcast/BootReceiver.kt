package com.douglaspac.wifireminder.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.douglaspac.wifireminder.persister.MySharedPref

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val alarmReceiverRegister = AlarmReceiverRegister(context)

        when {
            MySharedPref.isTurnOn(context) -> alarmReceiverRegister.register()
            else -> alarmReceiverRegister.cancel()
        }
    }
}