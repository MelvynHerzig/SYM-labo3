package ch.heigvd.iict.sym.labo3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ch.heigvd.iict.sym.labo3.manipulations.BeaconActivity
import ch.heigvd.iict.sym.labo3.manipulations.NfcActivity
import ch.heigvd.iict.sym.labo3.manipulations.CodeBarreActivity


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
        private const val FINE_LOCATION_PERMISSION_CODE = 1
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
            // Check for permissions, if acquired, starts BeaconActivity
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSION_CODE)
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
            when (requestCode) {
                CAMERA_PERMISSION_CODE -> {
                    startActivity(Intent(this, CodeBarreActivity::class.java))
                }
                FINE_LOCATION_PERMISSION_CODE -> {
                    startActivity(Intent(this, BeaconActivity::class.java))
                }
            }
        }
    }

    /**
     * Function called when the user accept or decline a permission.
     * In our case, just check for camera.
     * Source: https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FINE_LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, BeaconActivity::class.java))
                }
            }
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, CodeBarreActivity::class.java))
                }
            }
        }
    }
}