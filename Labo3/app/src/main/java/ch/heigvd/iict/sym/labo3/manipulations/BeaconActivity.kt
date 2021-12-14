package ch.heigvd.iict.sym.labo3.manipulations

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.iict.sym.labo3.R
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.sym.labo3.beacon.utils.BeaconAdapter
import org.altbeacon.beacon.*

/**
 * Activité implémentant la manipulation lié à l'utilisation du iBeacon
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class BeaconActivity : AppCompatActivity() {

    companion object {
        private const val BEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    }

    private val beaconsArray = ArrayList<Beacon>()
    private val beaconAdapter = BeaconAdapter(beaconsArray)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon)

        // Pop-up de prévention
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Besoins de l'application")
        builder.setMessage("Pensez à activer le bluetooth, à vérifier votre accès internet " +
                "et à avoir la localisation activée pour utiliser cette partie de l'application!")
        builder.setPositiveButton(android.R.string.ok, null)
        builder.setOnDismissListener { }
        builder.show()

        // Setup la RecycleView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = beaconAdapter

        // Setup le BeaconManager
        val beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.clear()
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_FORMAT))

        // Setup la région pour les Beacons à détecter
        val region = Region("all-beacons-region", null, null, null)
        beaconManager.getRegionViewModel(region).rangedBeacons.observe(this, rangingObserver)
        beaconManager.startRangingBeacons(region)
    }

    /**
     * Détecte tous les beacons dans les alentours et notifie la RecycleView
     */
    private val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        for (beacon: Beacon in beacons) {
            // Met à jour le beacon s'il existe déjà dans la liste
            if (beaconsArray.contains(beacon)) {
                beaconsArray[beaconsArray.indexOf(beacon)] = beacon
                beaconAdapter.notifyItemChanged(beaconsArray.indexOf(beacon))
            }
            // Ajoute le beacon s'il n'existe pas dans la liste
            else {
                beaconsArray.add(beacon)
                beaconAdapter.notifyItemInserted(beaconsArray.indexOf(beacon))
            }
        }
    }
}