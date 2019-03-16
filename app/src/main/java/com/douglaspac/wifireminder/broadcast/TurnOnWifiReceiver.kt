package com.douglaspac.wifireminder.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.douglaspac.wifireminder.utils.removeNotification

class TurnOnWifiReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        removeNotification(ctx, intent)
        val wifiManager = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        } else {
            ctx.startActivity(Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
}