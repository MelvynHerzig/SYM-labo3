package ch.heigvd.iict.sym.labo3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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
        nfcButton  .setOnClickListener{
            startActivity(Intent(this, NfcActivity::class.java))
        }

        beaconButton.setOnClickListener{
            startActivity(Intent(this, BeaconActivity::class.java))
        }

        codeBarreButton.setOnClickListener{
            checkPermission(Manifest.permission.CAMERA, MainActivity.CAMERA_PERMISSION_CODE)
        }
    }

    /**
     * Check for permissions
     * Source: https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
     */
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            if(requestCode == CAMERA_PERMISSION_CODE) {
                startActivity(Intent(this, CodeBarreActivity::class.java))
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
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (requestCode == CAMERA_PERMISSION_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, CodeBarreActivity::class.java))
                }
            }
        }
    }
}