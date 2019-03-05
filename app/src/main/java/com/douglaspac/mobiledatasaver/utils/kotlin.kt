package com.douglaspac.mobiledatasaver.utils

import android.content.SharedPreferences
import java.security.InvalidParameterException
import java.util.logging.Logger

const val INTERVAL_BETWEEN_JOBS = 5L * 60L * 1000L

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
                throw InvalidParameterException("Invalid parameter")
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
                throw InvalidParameterException("Invalid parameter")
            }
        }
    }

    editor.apply()
}