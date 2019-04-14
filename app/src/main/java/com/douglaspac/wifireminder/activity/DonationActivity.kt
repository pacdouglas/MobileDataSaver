package com.douglaspac.wifireminder.activity

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.vending.billing.IInAppBillingService
import com.douglaspac.wifireminder.R
import com.douglaspac.wifireminder.persister.MySharedPref
import com.douglaspac.wifireminder.utils.logger
import com.douglaspac.wifireminder.utils.openSimpleDialog
import kotlinx.android.synthetic.main.activity_donation.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject

class DonationActivity : AppCompatActivity() {
    companion object {
        private val GOOGLE_CATALOG = arrayListOf("donation.5", "donation.10", "donation.20", "donation.50", "donation.100")
        private const val GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtkbfinayR5vKPZr0gIA8czrQheO44gNUvJJw7gO2llYoNTjwgX+hfDIvW0fZTwx5vQnGCaQ9+fFqUKbgZ94JLcjz259aSCVtcOGHPNFfhvaI3sFwZz+gK1KDMa6/1PIMnHH85y2dyOtcaegVA4ZjXHRnH4X0gxYqdwW6Q3ovdPq8Ja94kpaJRjQTkyp3XlgQj5KhaoIDUp96OM8Q0wr2K1opp3JZADlpulKkX56FQiCNKc3BMyNf+Iw5zGTdi6wuGQzbuln9I0AkUxSx2n7xdtr7cX5UYfTspBL/SRRW0U3lRwqmtwvHu/dZgkvoUTtkgLCM7GRedijCFczN5YjdUQIDAQAB"
        private const val PAYPAL_USER = "douglas.pac@gmail.com"
        private const val PAYPAL_CURRENCY_CODE = "USD"
        private const val BITCOIN_ADDRESS = "3KbNqQ127uWWVs3eD6oD5apkuZPpjk1fCF"
        private const val REQUEST_ACTIVITY_CODE = 1001
        private const val INAPP = "inapp"
        private const val ANDROID_BILLING_INTERFACE = "com.android.vending.billing.InAppBillingService.BIND"
        private const val ANDROID_BILLING_PACKAGE = "com.android.vending"
        private const val ITEM_LIST_ID = "ITEM_ID_LIST"
        private const val RESPONSE_CODE = "RESPONSE_CODE"
        private const val INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST"
        private const val PURCHASE_TOKEN = "purchaseToken"
        private const val BUY_INTENT = "BUY_INTENT"
        private const val DETAILS_LIST = "DETAILS_LIST"
        private const val PRODUCT_ID = "productId"
        private const val PRICE = "price"
        private const val BILLING_RESPONSE_RESULT_OK = 0
        private const val BILLING_API_VERSION = 3
    }

    private var mService: IInAppBillingService? = null
    private val mServiceConn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = IInAppBillingService.Stub.asInterface(service)
        }
    }
    private val mValuesAvailable by lazy {
        val valuesDefault = resources.getStringArray(R.array.donation_google_catalog_values)
        val ret = mutableMapOf<String, String>()
        GOOGLE_CATALOG.forEachIndexed { index, s -> ret[s] = valuesDefault[index] }
        ret
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)

        donation_layout.visibility = View.GONE
        donation_progress_layout.visibility = View.VISIBLE

        donation_button_google.setOnClickListener { runGoogleDonation() }
        donation_button_paypal.setOnClickListener { runPayPalDonation() }
        donation_button_bitcoin.setOnClickListener { runBitCoinDonation() }

        val counter = MySharedPref.getNotifyCounter(this)
        val counterTxt = if (counter < 2) { "" } else {
            "\n" + getString(R.string.notification_donation_body, counter.toString())
        }
        donation_textview_description.text = getString(R.string.donations_description) + counterTxt

        tryFillDonationGoogleSpinner {
            donation_layout.visibility = View.VISIBLE
            donation_progress_layout.visibility = View.GONE
        }
    }

    private fun runBitCoinDonation() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(BITCOIN_ADDRESS, BITCOIN_ADDRESS)
        clipboard.primaryClip = clip
        Toast.makeText(this@DonationActivity, R.string.donations_bitcoin_toast_copy, Toast.LENGTH_LONG).show()
    }

    private fun runPayPalDonation() {
        val viewIntent = Intent(Intent.ACTION_VIEW, Uri.Builder().apply {
            scheme("https").authority("www.paypal.com").path("cgi-bin/webscr")
            appendQueryParameter("cmd", "_donations")
            appendQueryParameter("business", PAYPAL_USER)
            appendQueryParameter("lc", "US")
            appendQueryParameter("item_name", getString(R.string.donation_paypal_item))
            appendQueryParameter("no_note", "1")
            appendQueryParameter("no_shipping", "1")
            appendQueryParameter("currency_code", PAYPAL_CURRENCY_CODE)
        }.build())
        val title = resources.getString(R.string.donations_pay_pal)
        val chooser = Intent.createChooser(viewIntent, title)

        if (viewIntent.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        } else {
            showErrorDialog()
        }
    }

    private fun tryFillDonationGoogleSpinner(onFinish: () -> Unit) {
        doAsync {
            try {
                val serviceIntent = Intent(ANDROID_BILLING_INTERFACE)
                serviceIntent.setPackage(ANDROID_BILLING_PACKAGE)
                bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE)

                val querySkus = Bundle().apply { putStringArrayList(ITEM_LIST_ID, GOOGLE_CATALOG) }
                var skuDetails: Bundle? = null
                for (i in 0..10) {
                    skuDetails = mService?.getSkuDetails(BILLING_API_VERSION, packageName, INAPP, querySkus)
                    if (mService != null && skuDetails != null) break
                    Thread.sleep(200)
                }

                if (skuDetails != null && mService != null) {
                    consumePurchases()
                    val response = skuDetails.getInt(RESPONSE_CODE)
                    if (response == BILLING_RESPONSE_RESULT_OK) {
                        val responseList = skuDetails.getStringArrayList(DETAILS_LIST) ?: return@doAsync
                        responseList.forEach { res ->
                            val json = JSONObject(res)
                            val sku = json.getString(PRODUCT_ID)
                            val price = json.getString(PRICE)
                            mValuesAvailable[sku] = price
                        }
                    }
                }
            } catch (ex: Exception) {
                logger().warning(ex.message)
                ex.printStackTrace()
            }
            uiThread {
                donation_spinner_google.adapter = ArrayAdapter<String>(
                    this@DonationActivity,
                    android.R.layout.simple_spinner_item,
                    mValuesAvailable.map { it.value }.toTypedArray()
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                onFinish()
            }
        }
    }

    private fun runGoogleDonation() {
        val selectedItem = donation_spinner_google.selectedItem as String
        val sku = mValuesAvailable.keys.firstOrNull { mValuesAvailable[it] == selectedItem } ?: run { showErrorDialog(); return }
        val buyIntentBundle = mService?.getBuyIntent(BILLING_API_VERSION, packageName, sku, INAPP, GOOGLE_PUBKEY) ?: run { showErrorDialog(); return }
        val pendingIntent = buyIntentBundle.getParcelable(BUY_INTENT) as PendingIntent? ?: run { showErrorDialog(); return }

        startIntentSenderForResult(pendingIntent.intentSender, REQUEST_ACTIVITY_CODE, Intent(), 0, 0, 0)
    }

    private fun showErrorDialog() {
        this.openSimpleDialog(getString(R.string.donation_error), getString(R.string.donation_error_description))
    }

    override fun onDestroy() {
        if (mService != null) {
            unbindService(mServiceConn)
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ACTIVITY_CODE && data != null && data.getIntExtra(RESPONSE_CODE, -1) == BILLING_RESPONSE_RESULT_OK) {
            this.consumePurchases()
            this.openSimpleDialog(
                getString(R.string.donations_thanks_dialog_title),
                getString(R.string.donations_thanks_dialog)
            )
        }
    }

    private fun consumePurchases() {
        val ownedItems = mService?.getPurchases(BILLING_API_VERSION, packageName, INAPP, null)

        if (ownedItems?.getInt(RESPONSE_CODE) == BILLING_RESPONSE_RESULT_OK) {
            val purchaseDataList = ownedItems.getStringArrayList(INAPP_PURCHASE_DATA_LIST)
            purchaseDataList?.forEach { purchaseData ->
                val jsonObj = JSONObject(purchaseData)
                if (jsonObj.has(PURCHASE_TOKEN)) {
                    val token = jsonObj.getString(PURCHASE_TOKEN) ?: return@forEach
                    val ret = mService?.consumePurchase(BILLING_API_VERSION, packageName, token)
                    logger().info("Consuming purchase result: $ret")
                }
            }
        }
    }
}
