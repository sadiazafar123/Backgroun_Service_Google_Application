package com.example.backgroundservicesgoogleapplication

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationServices : Service() {
    companion object{
        const val CHANNEL_ID= "12345"
    }
    final var UPDATE_INTERVAL_IN_MILISECOND = 3000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationSettingRequest: LocationSettingsRequest
    lateinit var locationCallbackListener: LocationCallback
    lateinit var notificationManager: NotificationManager
    private var lat: Double? = 0.0
    private var lng: Double? = 0.0


    override fun onCreate() {
        super.onCreate()
        Log.v("oncreate","on Create")
        Log.v("services", "oncreate")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForeground(1, prepareForgroundNotification())

        }
        createLocationRequest()

        return START_STICKY

    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setIntervalMillis(5000).build()
        locationCallbackListener = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.v("location","$locationResult")
                var lastLocation = locationResult.lastLocation
                lat = lastLocation?.latitude
                lng = lastLocation?.longitude
/*
                val inten = Intent().apply {
                    action = "GET_LOCATION"
                    putExtra("lat", lat)
                    putExtra("lng", lng)
                }*/
//                inten.action = "GET_LOCAION"
//                inten.putExtra("lat", lat)
//                inten.putExtra("lng", lng)
//                sendBroadcast(inten)

            /*   sendBroadcast(Intent().apply {
                    action = "GET_LOCATION"
                    putExtra("lat", lat)
                    putExtra("lng", lng)
                })
*/
                sendBroadcast(Intent().apply {
                    action="LOCATION_TOAST"
                    putExtra("lat",lat)
                    putExtra("lng",lng)
                })
               sendBroadcast(Intent().apply {
                    action="ADD_MARKER"
                    putExtra("lat",lat)
                    putExtra("lng",lng)
                })

//                if (MyApplication.getLocationListener() != null){
//                    MyApplication.getLocationListener( )!!.onGetLocation(LatLng(lat ?: 0.0, lng ?: 0.0))
//                }
                //to set your desired data ,call here and set in content etc
//                notificationManager.notify(1, prepareForgroundNotification())
//                Toast.makeText(this@LocationServices, "location" +lat + "," +lng, Toast.LENGTH_SHORT).show()
            }

        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallbackListener, Looper.getMainLooper())


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareForgroundNotification(): Notification {
        Log.v("oncreate","on Create")

        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "location", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val pendingIntent: PendingIntent =
            Intent(this, MapsActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE)
            }
        var contentText = "Getting Your Location"
        if (lat == 0.0){
           contentText = "Getting Your Location"
        }
        else{
           contentText = "Your Lacation is ($lat, $lng)"
        }
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("location update")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        //mBuilder.setContentIntent(pendingIntent)
        val notification:Notification = mBuilder.build()
        notificationManager.notify(1, notification)
         return notification
    }



    override fun onBind(intent: Intent) = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallbackListener)
        fusedLocationProviderClient.flushLocations()
        //stopService()
        //stopSelf()


    }
}