package com.douglaspac.wifireminder.persister

import android.content.Context
import android.preference.PreferenceManager
import com.douglaspac.wifireminder.utils.get
import com.douglaspac.wifireminder.utils.put

object MySharedPref {
    private const val IS_TURN_ON = "IS_TURN_ON"
    private const val TOTAL_MOBILE_USAGE = "TOTAL_MOBILE_USAGE"
    private const val LAST_VERIFIED_TIME = "LAST_VERIFIED_TIME"
    private const val MUTE_UNTIL = "MUTE_UNTIL"
    private const val NOTIFY_AFTER = "NOTIFY_AFTER"
    private const val NOTIFY_COUNTER = "NOTIFY_COUNTER"

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

    fun getLastVerifiedTime(ctx: Context): Long {
        return PreferenceManager.getDefaultSharedPreferences(ctx).get(LAST_VERIFIED_TIME, 0L)
    }

    fun setLastVerifiedTime(ctx: Context, value: Long) {
        PreferenceManager.getDefaultSharedPreferences(ctx).put(LAST_VERIFIED_TIME, value)
    }

    fun getMuteUntil(ctx: Context): Long {
        return PreferenceManager.getDefaultSharedPreferences(ctx).get(MUTE_UNTIL, 0L)
    }

    fun setMuteUntil(ctx: Context, value: Long) {
        PreferenceManager.getDefaultSharedPreferences(ctx).put(MUTE_UNTIL, value)
    }

    fun getNotifyAfter(ctx: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(ctx).get(NOTIFY_AFTER, 5)
    }

    fun setNotifyAfter(ctx: Context, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(ctx).put(NOTIFY_AFTER, value)
    }

    fun getNotifyCounter(ctx: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(ctx).get(NOTIFY_COUNTER, 1)
    }

    fun setNotifyCounter(ctx: Context, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(ctx).put(NOTIFY_COUNTER, value)
    }
}