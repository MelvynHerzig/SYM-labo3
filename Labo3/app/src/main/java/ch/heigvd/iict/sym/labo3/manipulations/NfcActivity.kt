package ch.heigvd.iict.sym.labo3.manipulations

import android.content.Intent
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.iict.sym.labo3.R
import android.content.IntentFilter.MalformedMimeTypeException

import android.content.IntentFilter

import android.app.PendingIntent
import android.nfc.Tag
import android.widget.TextView
import android.widget.Toast
import java.lang.RuntimeException
import android.nfc.tech.Ndef
import android.util.Log
import ch.heigvd.iict.sym.labo3.utilities.NfcReaderCoroutine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * Activité liée à l'utilisation des balises NFC
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class NfcActivity : AppCompatActivity() {

    private lateinit var mNfcAdapter: NfcAdapter

    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        textView1 = findViewById(R.id.nfc_text_1)
        textView2 = findViewById(R.id.nfc_text_2)
        textView3 = findViewById(R.id.nfc_text_3)
        textView4 = findViewById(R.id.nfc_text_4)



        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, R.string.nfc_not_supported, Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            textView1.setText(R.string.nfc_disabled)
            textView2.setText(R.string.nfc_disabled)
            textView3.setText(R.string.nfc_disabled)
            textView4.setText(R.string.nfc_disabled)
        }



        handleIntent(intent);
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val type = intent.type
            if (MIME_TEXT_PLAIN == type) {
                val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!

                runBlocking {
                    launch {
                        val result = NfcReaderCoroutine().execute(tag)

                        textView1.text = result.getOrNull()
                    }
                }
            } else {
                Log.d("NfcActivity", "Wrong mime type: $type")
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED == action) {

            // In case we would still use the Tech Discovered Intent
            val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!
            val techList: Array<String> = tag.techList
            val searchedTech = Ndef::class.java.name
            for (tech in techList) {
                if (searchedTech == tech) {
                    runBlocking {
                        launch {
                            val result = NfcReaderCoroutine().execute(tag)

                            textView1.text = result.getOrNull()
                        }
                    }

                    break
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupForegroundDispatch(this, mNfcAdapter)
    }

    companion object {
        val MIME_TEXT_PLAIN: String = "text/plain"

        fun setupForegroundDispatch(activity: AppCompatActivity, adapter: NfcAdapter) {
            val intent = Intent(activity.applicationContext, activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

            val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, 0)

            val filters = arrayOfNulls<IntentFilter>(1)
            val techList = arrayOf<Array<String>>()

            // Notice that this is the same filter as in our manifest.

            // Notice that this is the same filter as in our manifest.
            filters[0] = IntentFilter()
            filters[0]!!.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            filters[0]!!.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                filters[0]!!.addDataType(MIME_TEXT_PLAIN)
            } catch (e: MalformedMimeTypeException) {
                throw RuntimeException("Check your mime type.")
            }

            adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList)
        }

        fun stopForegorundDispatch(activity: AppCompatActivity, adapter: NfcAdapter) {
            adapter.disableForegroundDispatch(activity)
        }
    }
}