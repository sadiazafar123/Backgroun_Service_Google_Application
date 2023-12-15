package com.example.backgroundservicesgoogleapplication

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.example.backgroundservicesgoogleapplication.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,ToastListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var permissionCode = 101
    var services: Intent? = null
    lateinit var btnStop: AppCompatButton
    private var lat: Double? = 0.0
    private var lng: Double? = 0.0
    private lateinit var myBroadCastReceiver: MyBroadCastReceiver
    lateinit var toastBroadCastReceiver:ToastBroadCastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyApplication.setLocationListener(this)
        MyApplication.setToastListener(this)
        //register broadcast receiver
        myBroadCastReceiver = MyBroadCastReceiver()
        toastBroadCastReceiver=ToastBroadCastReceiver()
//        var intentFilter = IntentFilter("GET_LOCATION")
        var intentFilter2 = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
       var intentFilter3 = IntentFilter("LOCATION_TOAST")

//        registerReceiver(myBroadCastReceiver, intentFilter)
        registerReceiver(myBroadCastReceiver, intentFilter2)
      //  registerReceiver(myBroadCastReceiver, intentFilter3)
        registerReceiver(toastBroadCastReceiver,intentFilter3)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnStop = findViewById(R.id.btnStop)
        services = Intent(this, LocationServices::class.java)

        if (isServiceRunning()) {
            btnStop.text = "service stop"
        } else {
            btnStop.text = " start service"
        }

        btnStop.setOnClickListener() {
            if (!isServiceRunning()) {
                if (ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ), permissionCode
                    )
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        startForegroundService(services)
                        btnStop.text = "service stop"

                    } else {
                        startService(services)
                        btnStop.text = "service stop"

                    }
                }


            } else {
                btnStop.text = " start service"
                stopService(Intent(applicationContext, LocationServices::class.java))
                Toast.makeText(this, "service has stopped", Toast.LENGTH_SHORT).show()


            }

        }
        //permission checked


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    //second method of checking if service is running etc
    /*  private fun isMyServiceRunning(serviceClass: Class<LocationServices>): Boolean {
          val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
          for (service in manager.getRunningServices(Int.MAX_VALUE)) {
              if (serviceClass.name == service.service.className) {
                  return true
              }
          }
          return false
      }
    */
    private fun isServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            //package name of service class.class name
            if ("com.example.backgroundservicesgoogleapplication.LocationServices" == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionCode) {

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MapsActivity, "Camera Permission Granted", Toast.LENGTH_SHORT)
                    .show()

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(services)
                } else {
                    startService(services)
                }
            } else {
                Toast.makeText(this, " Permission Denied", Toast.LENGTH_SHORT).show()
                //finish()
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), permissionCode
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.setLocationListener(null)
        MyApplication.setToastListener(null)
        if (::myBroadCastReceiver.isInitialized) {
            unregisterReceiver(myBroadCastReceiver)
        }
        if (::toastBroadCastReceiver.isInitialized){
            unregisterReceiver(toastBroadCastReceiver)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onGetLocation(latLng: LatLng) {
        Log.v("latlng","$latLng")
        if (::mMap.isInitialized) {
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Your current location"))
            mMap.moveCamera(
                CameraUpdateFactory
                    .newLatLngZoom(latLng, 15f)
            )
//            .newLatLng(latLng))
        }
//        Toast.makeText(this, "(${latLng.latitude}, ${latLng.longitude})", Toast.LENGTH_SHORT).show()
    }

    override fun onGetToast(latLng: LatLng) {
        Toast.makeText(this, "${latLng.latitude},${latLng.longitude}", Toast.LENGTH_SHORT).show()


    }

    /* override fun onGetToast(latLng: LatLng) {
         Toast.makeText(this, "(${latLng.latitude}, ${latLng.longitude})", Toast.LENGTH_SHORT).show()
     }
 */

    inner class MyBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "GET_LOCATION") {

                lat = intent?.extras?.getDouble("lat", 0.0)
                lng = intent?.extras?.getDouble("lng", 0.0)
                //?:0.0 mean (if null then initialized 0 otherwise coming value

                 if (::mMap.isInitialized){
                     mMap.clear()
                     mMap.addMarker(MarkerOptions().position(LatLng(lat?:0.0, lng?:0.0)).title("Your current location"))
                     mMap.moveCamera(CameraUpdateFactory
                         .newLatLngZoom(LatLng(lat?:0.0, lng?:0.0), 15f))
                 }
                Toast.makeText(this@MapsActivity, "($lat, $lng)", Toast.LENGTH_SHORT).show()
            }
            if (intent?.action == "ADD_MARKER") {
                lat = intent.extras?.getDouble("lat", 0.0)
                lng = intent.extras?.getDouble("lng", 0.0)
                if (::mMap.isInitialized) {
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(lat ?: 0.0, lng ?: 0.0))
                            .title("your curent location")
                    )
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lat ?: 0.0,
                                lng ?: 0.0
                            ), 15f
                        )
                    )
                }
            }
            //   Toast.makeText(this@MapsActivity, "($lat ,$lng)", Toast.LENGTH_SHORT).show()
            if (intent?.action == "LOCATION_TOAST") {

                lat = intent.extras?.getDouble("lat", 0.0)
                lng = intent.extras?.getDouble("lng", 0.0)
                Toast.makeText(this@MapsActivity, "($lat, $lng)", Toast.LENGTH_SHORT).show()

                if (::mMap.isInitialized) {

                }
            }
        }
    }


}