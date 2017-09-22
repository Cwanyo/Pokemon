package com.wvn.cwan.pokemon

import android.location.Location
/**
 * Created by C.wan_yo on 21-Sep-17.
 */
class Pokemon {
    var name: String? = null
    var description: String? = null
    var image: Int? = null
    var location: Location? = null
    var isCatch: Boolean? = null
    var power: Double? = null

    //generate constructor

    constructor(name: String?, description: String?, image: Int?, latitude: Double?, longitude: Double?, isCatch: Boolean?, power: Double?) {
        this.name = name
        this.description = description
        this.image = image
        this.location = Location(name)
        this.location!!.latitude = latitude!!
        this.location!!.longitude = longitude!!
        this.isCatch = isCatch
        this.power = power
    }
}