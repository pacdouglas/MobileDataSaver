package com.douglaspac.reminderwifi.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.douglaspac.reminderwifi.broadcast.AlarmReceiverRegister
import com.douglaspac.reminderwifi.persister.MySharedPref
import com.douglaspac.reminderwifi.utils.logger
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.douglaspac.reminderwifi.R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val alarmReceiverRegister = AlarmReceiverRegister(this)

        switch_turn_on_off.isChecked = MySharedPref.isTurnOn(this)
        switch_turn_on_off.setOnCheckedChangeListener { _, isChecked ->
            MySharedPref.setTurnOn(this, isChecked)

            logger().info("Reminder WiFi turn on: $isChecked")
            when {
                isChecked -> alarmReceiverRegister.register()
                else -> alarmReceiverRegister.cancel()
            }
        }
        button_donation.setOnClickListener {
            startActivity(Intent(this@MainActivity, ContactActivity::class.java))
        }
    }
}