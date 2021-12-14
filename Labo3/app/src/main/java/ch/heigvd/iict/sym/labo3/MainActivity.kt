package ch.heigvd.iict.sym.labo3

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ch.heigvd.iict.sym.labo3.manipulations.BeaconActivity
import ch.heigvd.iict.sym.labo3.manipulations.NfcActivity
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback

import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ch.heigvd.iict.sym.labo3.manipulations.CodeBarreActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


/**
 * Activité principale permettant de lancer les 3 manipulations:
 * - NFC
 * - Beacon
 * - Code barre
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_FINE_LOCATION = 1
        private const val CAMERA_PERMISSION_CODE = 100
    }

    // Référence sur le bouton asynchrone.
    protected lateinit var nfcButton: Button

    // Référence sur le bouton retardé.
    protected lateinit var beaconButton: Button

    // Référence sur le bouton sérialisé.
    protected lateinit var codeBarreButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Linkage des boutons selon layout
        nfcButton       = findViewById(R.id.main_btn_nfc)
        beaconButton    = findViewById(R.id.main_btn_beacon)
        codeBarreButton = findViewById(R.id.main_btn_codeBarre)

        // Mise en place des listeners
        nfcButton.setOnClickListener{
            startActivity(Intent(this, NfcActivity::class.java))
        }

        beaconButton.setOnClickListener{
            //checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
            startActivity(Intent(this, BeaconActivity::class.java))
        }

        codeBarreButton.setOnClickListener{
            // Check for permissions, if acquired, starts CodeBarreActivity
            checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
        }
    }

    /**
     * Check for permissions
     * Source: https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
     */
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(permission), requestCode)
        } else {
            if(requestCode == CAMERA_PERMISSION_CODE) {
                startActivity(Intent(this, CodeBarreActivity::class.java))
            }
        }
    }

    /**
     * Check and Ask location permission
     * Source: https://altbeacon.github.io/android-beacon-library/requesting_permission.html
     */
    private fun checkLocalPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ), PERMISSION_REQUEST_FINE_LOCATION
                )
            } else {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Functionality limited")
                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener { }
                builder.show()
            }
        }
    }

    /**
     * Function called when the user accept or decline a permission.
     * In our case, just check for camera.
     * Source: https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
     * Source: https://altbeacon.github.io/android-beacon-library/requesting_permission.html
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_FINE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(ContentValues.TAG, "fine location permission granted")
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
                return
            }
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, CodeBarreActivity::class.java))
                }
            }
        }
    }
}