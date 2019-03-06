package com.douglaspac.reminderwifi.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.douglaspac.reminderwifi.R
import com.douglaspac.reminderwifi.utils.BITCOIN_WALLET
import com.douglaspac.reminderwifi.utils.EMAIL
import com.douglaspac.reminderwifi.utils.GITHUB
import com.douglaspac.reminderwifi.utils.LINKEDIN
import com.douglaspac.reminderwifi.utils.PICPAY
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        button_wallet_bitcoin.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(this.getString(R.string.bitcoin_wallet), BITCOIN_WALLET)
            clipboard.primaryClip = clip

            Toast.makeText(this, this.getString(com.douglaspac.reminderwifi.R.string.transfer_area_copied), Toast.LENGTH_LONG).show()
        }
        button_picpay.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PICPAY)))
        }
        button_email.setOnClickListener {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$EMAIL")))
        }
        button_linkedin.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://$LINKEDIN"))
            val packageManager = this.packageManager
            val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.isEmpty()) {
                intent = Intent(Intent.ACTION_VIEW, Uri.parse(LINKEDIN))
            }
            startActivity(intent)
        }
        button_github.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB)))
        }
    }
}
