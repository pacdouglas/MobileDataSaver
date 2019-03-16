package com.douglaspac.wifireminder.utils

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.logging.Logger

const val INTERVAL_BETWEEN_JOBS = 2L * 60L * 1000L
const val EMAIL = "douglas.pac@gmail.com"
const val LINKEDIN = "https://www.linkedin.com/in/douglasmartinsdm/"
const val GITHUB = "https://github.com/pacdouglas/WiFiReminder"

fun Any.logger(): Logger = Logger.getLogger(this.javaClass.name)

inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T): T {
    return when(T::class) {
        Boolean::class -> this.getBoolean(key, defaultValue as Boolean) as T
        Float::class -> this.getFloat(key, defaultValue as Float) as T
        Int::class -> this.getInt(key, defaultValue as Int) as T
        Long::class -> this.getLong(key, defaultValue as Long) as T
        String::class -> this.getString(key, defaultValue as String) as T
        else -> {
            if (defaultValue is Set<*>) {
                this.getStringSet(key, defaultValue as Set<String>) as T
            } else {
                throw RuntimeException("Invalid parameter")
            }
        }
    }
}

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    val editor = this.edit()

    when(T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String)
        else -> {
            if (value is Set<*>) {
                editor.putStringSet(key, value as Set<String>)
            } else {
                throw RuntimeException("Invalid parameter")
            }
        }
    }

    editor.apply()
}

const val EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID"
fun removeNotification(ctx: Context, intent: Intent) {
    val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)

    if (notificationId != 0) {
        notificationManager.cancel(notificationId)
    }

    ctx.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
}