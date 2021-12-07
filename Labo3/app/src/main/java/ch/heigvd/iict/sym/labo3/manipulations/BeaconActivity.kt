package ch.heigvd.iict.sym.labo3.manipulations

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.iict.sym.labo3.R
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.sym.labo3.beacon.utils.BeaconAdapter
import org.altbeacon.beacon.*

/**
 * Activité implémentant la manipulation lié à l'utilisation du iBeacon
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class BeaconActivity : AppCompatActivity(), BeaconConsumer {

    companion object {
        private const val PERMISSION_REQUEST_FINE_LOCATION = 1
        private const val PERMISSION_REQUEST_BACKGROUND_LOCATION = 2
        private const val BEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon)

        // Setup the recycle view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val beaconAdapter = BeaconAdapter()
        beaconAdapter.fakePopulate()
        recyclerView.adapter = beaconAdapter

        checkLocalPermission()

        val beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_FORMAT))
        beaconManager.bind(this)
    }

    override fun onBeaconServiceConnect() {
        /*val rangingObserver = Observer<Collection<Beacon>> { beacons ->
            Log.d(TAG, "Ranged: ${beacons.count()} beacons")
            for (beacon: Beacon in beacons) {
                Log.d(TAG, "$beacon about ${beacon.distance} meters away")
            }
        }*/
    }

    /**
     * Source: https://altbeacon.github.io/android-beacon-library/requesting_permission.html
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<String>,
                                   grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_FINE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "fine location permission granted")
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
            PERMISSION_REQUEST_BACKGROUND_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "background location permission granted")
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons when in the background.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
                return
            }
        }
    }

    /**
     * Check and Ask location permission
     * Source: https://altbeacon.github.io/android-beacon-library/requesting_permission.html
     */
    private fun checkLocalPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("This app needs background location access")
                    builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener {
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            PERMISSION_REQUEST_BACKGROUND_LOCATION
                        )
                    }
                    builder.show()
                } else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    PERMISSION_REQUEST_FINE_LOCATION
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
}