package com.douglaspac.mobiledatasaver

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.TrafficStats
import java.util.*

data class ApplicationNetworkUsageModel(
    val packageName: String,
    val name: String,
    val drawable: Drawable?,
    val total: Double
)

class MobileDataSaverTask(private val context: Context) : TimerTask() {
    override fun run() {
        if (!isOnlyMobileNetwork()) return

        val before = getMobileUsageInformations()
        logger().info("Before")

        before.forEach {
            if (it.packageName.contains("youtube")) {
                logger().info("youtube: ${it.total}")
            }
        }
        Thread.sleep(5000)

        val after = getMobileUsageInformations()
        logger().info("After")

        after.forEach {
            if (it.packageName.contains("youtube")) {
                logger().info("youtube: ${it.total}")
            }
        }
    }

    private fun getMobileUsageInformations(): List<ApplicationNetworkUsageModel> {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(0).toList()

        return packages.map { packageInfo ->
            val uid = packageInfo.applicationInfo.uid
            val packageName = packageInfo.packageName
            val app = try {
                pm.getApplicationInfo(packageName, 0)
            } catch (ex: PackageManager.NameNotFoundException) {
                return@map ApplicationNetworkUsageModel("", "", null,0.0)
            }

            val name = pm.getApplicationLabel(app).toString()
            val icon = pm.getApplicationIcon(app)
            val received = TrafficStats.getUidRxBytes(uid).toDouble() / (1024.0 * 1024.0)
            val send = TrafficStats.getUidTxBytes(uid).toDouble() / (1024.0 * 1024.0)
            val total = if ((received + send) > 0) received + send else 0.0

            ApplicationNetworkUsageModel(packageName, name, icon, total)
        }
    }

    private fun isOnlyMobileNetwork(): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn = false
        var isMobileConn = false

        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network).apply {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn = isWifiConn or isConnected
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected
                }
            }
        }

        return !isWifiConn && isMobileConn
    }
}