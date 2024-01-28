package com.example.runaway_aos

interface LocationUpdateListener {
    fun onLocationUpdate(latitude: Double, longitude: Double)
}

object LocationUpdateListenerHolder {
    var listener: LocationUpdateListener? = null
}