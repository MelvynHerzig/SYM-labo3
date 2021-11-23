package ch.heigvd.iict.sym.labo3.manipulations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.iict.sym.labo3.R

/**
 * Activité implémentant la manipulation lié à l'utilisation du iBeacon
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class BeaconActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon)
    }
}