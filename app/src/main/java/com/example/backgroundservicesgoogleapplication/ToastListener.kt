package com.example.backgroundservicesgoogleapplication

import com.google.android.gms.maps.model.LatLng

interface ToastListener {
    fun onGetToast(latLng: LatLng)

}