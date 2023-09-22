package com.example.accountkithms

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.accountkithms.databinding.ActivityLocationBinding
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hmf.tasks.Task
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationAvailability
import com.huawei.hms.location.LocationCallback
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationResult
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.LocationSettingsRequest
import com.huawei.hms.location.LocationSettingsResponse
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment


class LocationActivity :AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var hMap: HuaweiMap? = null

    private var mMapView: MapView? = null

    companion object {
        private const val TAG = "LocationActivity"
        private const val MAPVIEW_BUNDLE_KEY = "LocationBundleKey"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var settingsClient = LocationServices.getSettingsClient(this)

        val builder = LocationSettingsRequest.Builder()
        var mLocationRequest: LocationRequest? = null

        var mLocationCallback: LocationCallback? = null

        MapsInitializer.initialize(this);
        setContentView(R.layout.activity_location);
        var mSupportMapFragment: SupportMapFragment? = null
        mSupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mSupportMapFragment?.getMapAsync(this)


        mMapView = findViewById(R.id.mapView)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle =
                savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mMapView?.apply {
            onCreate(mapViewBundle)
            getMapAsync(this@LocationActivity)
        }



        binding.btnlocationupdate.setOnClickListener {
            requestLocationUpdate()
        }

        mLocationRequest = LocationRequest().apply {
            interval = 1000
            needAddress = true
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        if (null == mLocationCallback) {
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult != null) {
                        val locations: List<Location> =
                            locationResult.locations
                        if (locations.isNotEmpty()) {
                            for (location in locations) {
                                Toast.makeText(
                                    this@LocationActivity,
                                    "onLocationResult location[Longitude,Latitude,Accuracy]:${location.longitude} , ${location.latitude} , ${location.accuracy}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                    locationAvailability?.let {
                        val flag: Boolean = locationAvailability.isLocationAvailable
                        Toast.makeText(
                            this@LocationActivity,
                            "onLocationAvailability isLocationAvailable:$flag",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        settingsClient.checkLocationSettings(locationSettingsRequest)

            .addOnSuccessListener(OnSuccessListener { locationSettingsResponse ->
                val locationSettingsStates = locationSettingsResponse.locationSettingsStates
                val stringBuilder = StringBuilder()

                stringBuilder.append("isLocationUsable=")
                    .append(locationSettingsStates.isLocationUsable)

                stringBuilder.append(",\nisHMSLocationUsable=")
                    .append(locationSettingsStates.isHMSLocationUsable)
                Toast.makeText(
                    this,
                    "checkLocationSetting onComplete:$stringBuilder",
                    Toast.LENGTH_LONG
                ).show()
            })

            .addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(this, "checkLocationSetting onFailure:", Toast.LENGTH_SHORT)
                    .show()
            })
    }


    private fun requestLocationUpdate() {
        var fusedLocationProviderClient: FusedLocationProviderClient? = null
        var mLocationRequest: LocationRequest? = null
        var mLocationCallback: LocationCallback? = null
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 5000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
            //    Log.e("LocationXX:", locationResult.lastLocation.latitude.toString())
            //    Log.e("LocationXX:", locationResult.lastLocation.longitude.toString())
                Toast.makeText(this@LocationActivity, "LocationXX" + locationResult.lastLocation.latitude.toString(), Toast.LENGTH_SHORT).show()
                Toast.makeText(this@LocationActivity, "LocationXX" + locationResult.lastLocation.longitude.toString(), Toast.LENGTH_SHORT).show()
                (locationResult.lastLocation)

            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                super.onLocationAvailability(locationAvailability)
            }
        }
        fusedLocationProviderClient
            ?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
            ?.addOnSuccessListener {
                it
             //   Toast.makeText(this@LocationActivity, "Success", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(this@LocationActivity, "Fail", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onMapReady(map: HuaweiMap) {
        Log.d(TAG, "onMapReady: ")
        Toast.makeText(this@LocationActivity, "Harita yuklendi", Toast.LENGTH_SHORT).show()
        hMap = map
    }
    override fun onStart() {
        super.onStart()
        mMapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy()
    }

    override fun onPause() {
        mMapView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }
}


