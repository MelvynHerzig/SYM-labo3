package ch.heigvd.iict.sym.labo3.beacon.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.sym.labo3.R
import kotlin.collections.ArrayList

/**
 * Adapteur
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class BeaconAdapter : RecyclerView.Adapter<BeaconAdapter.ViewHolder>() {

    private var beacons = ArrayList<Beacon>()

    override fun getItemCount(): Int {
        return beacons.size
    }

    fun fakePopulate() {
        for (i in 1..20) {
            beacons.add(Beacon("a", i, i, "b", i.toDouble()))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.beacon_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindValues(beacons[position])
    }

    /**
     * Object used to store items GUI elements references
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val uuid: TextView = itemView.findViewById(R.id.UUID)
        private val majeurmineur: TextView = itemView.findViewById(R.id.majeurMineur)
        private val distance: TextView = itemView.findViewById(R.id.distance)
        private val rssi: TextView = itemView.findViewById(R.id.RSSI)

        fun bindValues(beacon : Beacon) {
            //icon.setImageResource()
            uuid.text = beacon.UUID
            majeurmineur.text = "${beacon.major} - ${beacon.minor}"
            distance.text = "${beacon.distance}"
            rssi.text = beacon.RSSI
        }
    }
}