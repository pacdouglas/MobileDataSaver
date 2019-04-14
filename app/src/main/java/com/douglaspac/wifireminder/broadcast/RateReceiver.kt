package com.douglaspac.wifireminder.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.douglaspac.wifireminder.utils.removeNotification

class RateReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        removeNotification(ctx, intent)
        val uri = Uri.parse("market://details?id=" + ctx.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try { ctx.startActivity(goToMarket) } catch (ex: Exception) {
            val uriAction = Uri.parse("http://play.google.com/store/apps/details?id=" + ctx.packageName)
            ctx.startActivity(Intent(Intent.ACTION_VIEW, uriAction).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
}