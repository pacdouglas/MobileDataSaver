package com.douglaspac.mobiledatasaver.persister

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.security.InvalidParameterException


object MySharedPref {
    private const val IS_TURN_ON = "IS_TURN_ON"
    private const val TOTAL_MOBILE_USAGE = "TOTAL_MOBILE_USAGE"

    fun setTurnOn(ctx: Context, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(ctx).put(IS_TURN_ON, value)
    }

    fun isTurnOn(ctx: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(ctx).get(IS_TURN_ON, false)
    }

    fun getTotalMobileUsage(ctx: Context): Long {
        return PreferenceManager.getDefaultSharedPreferences(ctx).get(TOTAL_MOBILE_USAGE, 0L)
    }

    fun setTotalMobileUsage(ctx: Context, value: Long) {
        PreferenceManager.getDefaultSharedPreferences(ctx).put(TOTAL_MOBILE_USAGE, value)
    }
}

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