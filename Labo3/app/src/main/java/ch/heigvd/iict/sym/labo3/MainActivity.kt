package ch.heigvd.iict.sym.labo3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ch.heigvd.iict.sym.labo3.manipulations.BeaconActivity
import ch.heigvd.iict.sym.labo3.manipulations.CodeBarreActivity
import ch.heigvd.iict.sym.labo3.manipulations.NfcActivity

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
            startActivity(Intent(this, CodeBarreActivity::class.java))
        }
    }
}