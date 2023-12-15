package com.example.backgroundservicesgoogleapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MyBroadCastReceiver:BroadcastReceiver() {
    private var lat: Double? = 0.0
    private var lng: Double? = 0.0

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "ADD_MARKER") {

            lat = intent?.extras?.getDouble("lat", 0.0)
            lng = intent?.extras?.getDouble("lng", 0.0)

            Log.v("lat","$lat")

            MyApplication.getLocationListener()?.onGetLocation(LatLng(lat?:0.0,lng?:0.0))

        }



    }
}