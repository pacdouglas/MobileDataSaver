package com.douglaspac.reminderwifi.activity

import android.content.ClipData
import android.content.ClipboardManager
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
import android.widget.Toast
import com.douglaspac.reminderwifi.R
import com.douglaspac.reminderwifi.broadcast.AlarmReceiverRegister
import com.douglaspac.reminderwifi.persister.MySharedPref
import com.douglaspac.reminderwifi.utils.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            val dialogLayout = buildContactDialogView()
            AlertDialog.Builder(this).apply { this.setView(dialogLayout) }.show()
        }
    }

    private fun buildContactDialogView(): View {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.dialog_contact, null).apply {
            this.findViewById<AppCompatButton>(R.id.button_wallet_bitcoin).setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(this@MainActivity.getString(R.string.bitcoin_wallet), BITCOIN_WALLET)
                clipboard.primaryClip = clip

                Toast.makeText(this@MainActivity, this@MainActivity.getString(com.douglaspac.reminderwifi.R.string.transfer_area_copied), Toast.LENGTH_LONG).show()
            }
            this.findViewById<AppCompatButton>(R.id.button_picpay).setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PICPAY)))
            }
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