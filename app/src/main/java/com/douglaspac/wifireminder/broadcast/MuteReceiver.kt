package com.douglaspac.wifireminder.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.TimeZoneFormat
import android.text.format.DateFormat
import android.widget.Toast
import com.douglaspac.wifireminder.R
import com.douglaspac.wifireminder.persister.MySharedPref
import com.douglaspac.wifireminder.utils.removeNotification
import java.util.*

class MuteReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        removeNotification(ctx, intent)
        val inOneHour = System.currentTimeMillis() + (60 * 60 * 1000)
        MySharedPref.setMuteUntil(ctx, inOneHour)

        val formattedHour = DateFormat.getTimeFormat(ctx).format(Date(inOneHour))
        Toast.makeText(ctx, ctx.getString(R.string.mute_toast, formattedHour), Toast.LENGTH_LONG).show()
    }
}