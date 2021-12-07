package ch.heigvd.iict.sym.labo3.beacon.utils

/**
 * Modélise un Beacon
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
data class Beacon (var UUID: String,
                   var major: Int,
                   var minor: Int,
                   var RSSI: String,
                   var distance: Double)