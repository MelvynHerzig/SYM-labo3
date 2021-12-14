package ch.heigvd.iict.sym.labo3.beacon.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.sym.labo3.R
import org.altbeacon.beacon.Beacon;
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

/**
 * Adapteur utilisant une RecycleView pour afficher les Beacons
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
data class BeaconAdapter(val beacons : ArrayList<Beacon>)
    : RecyclerView.Adapter<BeaconAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return beacons.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.beacon_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindValues(beacons[position], holder)
    }

    /**
     * Récupère les éléments de la vue qui seront utilisés pour y mettre les valeurs des beacons
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val uuid: TextView = itemView.findViewById(R.id.UUID)
        val major: TextView = itemView.findViewById(R.id.Major)
        val minor: TextView = itemView.findViewById(R.id.Minor)
        val rssi: TextView = itemView.findViewById(R.id.RSSI)
        val detection: TextView = itemView.findViewById(R.id.Detection)
    }

    /**
     * Permet de bind les valeurs d'un objet de la vue avec celle d'un Beacon
     */
    private fun bindValues(beacon : Beacon, holder : ViewHolder) {
        holder.uuid.text = beacon.id1.toString()
        holder.minor.text = "Mineur: ${beacon.id2}"
        holder.major.text = "Majeur: ${beacon.id3}"
        holder.rssi.text = "RSSI: ${beacon.rssi}"
        holder.detection.text = "Dernière détection: ${getDate(beacon.lastCycleDetectionTimestamp)}"
    }

    /**
     * Permet de récupérer la date au format yyyy-mm-dd hh:mm:ss
     */
    private fun getDate(timestamp: Long) : Date {
        return Timestamp(timestamp)
    }
}