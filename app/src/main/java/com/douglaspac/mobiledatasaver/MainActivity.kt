package com.douglaspac.mobiledatasaver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.douglaspac.mobiledatasaver.persister.MySharedPref
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var alarmIntent: Intent
    private lateinit var pendingIntent: PendingIntent
    private var manager: AlarmManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        alarmIntent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)


        switch_turn_on_off.setOnCheckedChangeListener { _, isChecked ->
            MySharedPref.setTurnOn(this, isChecked)

            logger().info("Mobile Data Saver turn on: $isChecked")
            if (isChecked) {
                startAlarm()

            } else {
                cancelAlarm()
            }
        }

        switch_turn_on_off.isChecked = MySharedPref.isTurnOn(this)
    }

    private fun startAlarm() {
        manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val interval = 60000L
        manager?.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent)
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm() {
        manager?.cancel(pendingIntent)
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show()
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.showNotification("hue", "hue")
    }
}