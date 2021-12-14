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
import java.lang.RuntimeException
import android.nfc.tech.Ndef
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.*
import ch.heigvd.iict.sym.labo3.utilities.NfcReaderCoroutine
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


/**
 * Activité liée à l'utilisation des balises NFC
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class NfcActivity : AppCompatActivity() {

    private lateinit var mNfcAdapter: NfcAdapter

    private lateinit var textView: TextView

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnValidate: Button
    private lateinit var btnCancel: Button

    private lateinit var btnAuthMax: Button
    private lateinit var btnAuthMedium: Button
    private lateinit var btnAuthMin: Button

    private val acceptedNfcTag = listOf("test", "1 2 3 4", "é è ê ë ē", "♤ ♡ ♢ ♧")
    private val acceptedEmail = "labo3@sym.ch"
    private val acceptedPassword = "kotlin"

    private var logged = false
    private var tagOk = false

    private var countdown: Int = MAX_LOGGED_TIME
    private var timer = object : CountDownTimer((MAX_LOGGED_TIME * 1000).toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            countdown--

            if(MAX_LOGGED_TIME - TIME_IN_MAX_LEVEL == countdown){
                Toast.makeText(applicationContext, "Losing MAX level auth", Toast.LENGTH_SHORT).show()
            }
            if(MAX_LOGGED_TIME - TIME_IN_MEDIUM_LEVEL == countdown){
                Toast.makeText(applicationContext, "Losing MEDIUM level auth", Toast.LENGTH_SHORT).show()
            }
            if(MAX_LOGGED_TIME - TIME_IN_MIN_LEVEL == countdown){
                Toast.makeText(applicationContext, "Losing MIN level auth", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFinish() {
            tagOk = false
            logged = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        textView = findViewById(R.id.nfc_text)
        btnCancel = findViewById(R.id.nfc_cancel)
        btnValidate = findViewById(R.id.nfc_validate)
        email = findViewById(R.id.nfc_email)
        password = findViewById(R.id.nfc_password)

        btnAuthMax = findViewById(R.id.nfc_btn_auth_max)
        btnAuthMedium = findViewById(R.id.nfc_btn_auth_medium)
        btnAuthMin = findViewById(R.id.nfc_btn_auth_min)

        textView.visibility = View.INVISIBLE
        setVisibilityAuthButtons(View.INVISIBLE)

        btnCancel.setOnClickListener {
            email.text?.clear()
            email.error = null
            password.text?.clear()
            password.error = null
        }

        btnValidate.setOnClickListener {
            email.error = null
            password.error = null

            //on récupère le contenu de deux champs dans des variables de type String
            val emailInput = email.text?.toString()
            val passwordInput = password.text?.toString()

            if (emailInput.isNullOrEmpty() or passwordInput.isNullOrEmpty()) {
                // on affiche un message dans les logs de l'application
                Log.d(TAG, "Au moins un des deux champs est vide")
                // on affiche un message d'erreur sur les champs qui n'ont pas été renseignés
                // la méthode getString permet de charger un String depuis les ressources de
                // l'application à partir de son id
                if (emailInput.isNullOrEmpty())
                    email.error = getString(R.string.nfc_mandatory_field)
                if (passwordInput.isNullOrEmpty())
                    password.error = getString(R.string.nfc_mandatory_field)
                // Pour les fonctions lambda, on doit préciser à quelle fonction l'appel à return
                // doit être appliqué
                return@setOnClickListener
            }

            if (emailInput == acceptedEmail && passwordInput == acceptedPassword) {
                logged = true
                textView.visibility = View.VISIBLE

                textView.setText(R.string.waiting_nfc)
            }
        }

        btnAuthMax.setOnClickListener{
            checkAuthorization(TIME_IN_MAX_LEVEL, "MAX")
        }

        btnAuthMedium.setOnClickListener{
            checkAuthorization(TIME_IN_MEDIUM_LEVEL, "MEDIUM")
        }

        btnAuthMin.setOnClickListener{
            checkAuthorization(TIME_IN_MIN_LEVEL, "MIN")
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, R.string.nfc_not_supported, Toast.LENGTH_LONG).show();
            finish()
            return

        }

        if (!mNfcAdapter.isEnabled) {
            Toast.makeText(this, R.string.nfc_disabled, Toast.LENGTH_LONG).show();
        }
        handleIntent(intent);
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && logged) {
            handleIntent(intent)
        } else {
            Toast.makeText(this, R.string.nfc_logged_required, Toast.LENGTH_LONG).show();
        }
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val type = intent.type
            if (MIME_TEXT_PLAIN == type) {
                val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!
                launchCoroutine(tag)

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
                    launchCoroutine(tag)
                    break
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupForegroundDispatch(this, mNfcAdapter)
    }

    private fun checkAuthorization(timeLimit: Int, authLevel: String) {
        if (MAX_LOGGED_TIME - timeLimit < countdown) {
            textView.text = "Auth $authLevel granted"
        } else {
            textView.text = "Auth $authLevel not granted"
        }
    }

    private fun setVisibilityAuthButtons(visiblity: Int) {
        btnAuthMin.visibility = visiblity
        btnAuthMedium.visibility = visiblity
        btnAuthMax.visibility = visiblity
    }

    private fun launchCoroutine(tag: Tag) {
        GlobalScope.launch {
            val result = NfcReaderCoroutine().execute(tag).getOrDefault(listOf())

            for (rTag in result) {

                if (acceptedNfcTag.contains(rTag)) {
                    tagOk = true
                    runOnUiThread {
                        textView.text = ""
                        setVisibilityAuthButtons(View.VISIBLE)
                    }
                    countdown = MAX_LOGGED_TIME
                    timer.cancel()
                    timer.start()
                    return@launch;
                }
            }

        }
    }

    companion object {
        const val TAG: String = "NfcActivity"

        const val MIME_TEXT_PLAIN: String = "text/plain"

        const val MAX_LOGGED_TIME: Int = 15

        const val TIME_IN_MAX_LEVEL = 5

        const val TIME_IN_MEDIUM_LEVEL = 10

        const val TIME_IN_MIN_LEVEL = MAX_LOGGED_TIME

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