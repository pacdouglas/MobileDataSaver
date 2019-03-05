package com.douglaspac.mobiledatasaver.persister

import android.content.Context
import android.preference.PreferenceManager
import com.douglaspac.mobiledatasaver.utils.get
import com.douglaspac.mobiledatasaver.utils.put

object MySharedPref {
    private const val IS_TURN_ON = "IS_TURN_ON"
    private const val TOTAL_MOBILE_USAGE = "TOTAL_MOBILE_USAGE"
    private const val LAST_VERIFIED_TIME = "LAST_VERIFIED_TIME"

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
}