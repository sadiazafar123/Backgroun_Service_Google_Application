package com.example.backgroundservicesgoogleapplication

import com.google.android.gms.maps.model.LatLng

interface LocationListener {
    fun onGetLocation(latLng: LatLng)
   // fun onGetToast(latLng: LatLng)
}