package com.douglaspac.wifireminder.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.douglaspac.wifireminder.R
import com.douglaspac.wifireminder.broadcast.AlarmReceiverRegister
import com.douglaspac.wifireminder.persister.MySharedPref
import com.douglaspac.wifireminder.utils.EMAIL
import com.douglaspac.wifireminder.utils.GITHUB
import com.douglaspac.wifireminder.utils.LINKEDIN
import com.douglaspac.wifireminder.utils.logger
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val turnOn = MySharedPref.isTurnOn(this)
        val alarmReceiverRegister = AlarmReceiverRegister(this)

        switch_turn_on_off.isChecked = turnOn
        switch_turn_on_off.setOnCheckedChangeListener { _, isChecked ->
            MySharedPref.setTurnOn(this, isChecked)

            logger().info("Reminder WiFi turn on: $isChecked")
            when {
                isChecked -> alarmReceiverRegister.register()
                else -> alarmReceiverRegister.cancel()
            }
        }

        spinner_notify_conf.adapter = ArrayAdapter.createFromResource(this,
            R.array.notify_conf, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val stringArray = this.resources.getStringArray(R.array.notify_conf)
        spinner_notify_conf.setSelection(stringArray.indexOf(MySharedPref.getNotifyAfter(this).toString() + "MB"))
        spinner_notify_conf.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val value = stringArray[position].replace("MB", "").toInt()
                MySharedPref.setNotifyAfter(this@MainActivity, value)
            }
        }

        button_donation.setOnClickListener {
            val dialogLayout = buildContactDialogView()
            AlertDialog.Builder(this).apply { this.setView(dialogLayout) }.show()
        }

        if (turnOn) {
            alarmReceiverRegister.cancel()
            alarmReceiverRegister.register()
        }
    }

    private fun buildContactDialogView(): View {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.dialog_contact, null).apply {
            this.findViewById<AppCompatButton>(R.id.button_email).setOnClickListener {
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$EMAIL")))
            }
            this.findViewById<AppCompatButton>(R.id.button_linkedin).setOnClickListener {
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://$LINKEDIN"))
                val packageManager = this@MainActivity.packageManager
                val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                if (list.isEmpty()) {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(LINKEDIN))
                }
                startActivity(intent)
            }
            this.findViewById<AppCompatButton>(R.id.button_github).setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB)))
            }
        }
    }
}