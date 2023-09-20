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


class LocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var settingsClient = LocationServices.getSettingsClient(this)

        val builder = LocationSettingsRequest.Builder()
        var mLocationRequest: LocationRequest? = null

        var mLocationCallback: LocationCallback? = null

       /* binding.btnlocationupdate.setOnClickListener {
            requestLocationUpdatesWithCallback()
        } */
        binding.btnlocationupdate.setOnClickListener {
            requestLocationUpdate()
        }

        mLocationRequest = LocationRequest().apply {
            interval = 1000
            needAddress = true
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        if (null == mLocationCallback) { object : LocationCallback() {
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
    private fun requestLocationUpdatesWithCallback() {

        var settingsClient = LocationServices.getSettingsClient(this)

        var mLocationRequest: LocationRequest? = null

        var mLocationCallback: LocationCallback? = null

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()
        // Check the device settings before requesting location updates.
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(locationSettingsRequest)
        try {

            locationSettingsResponseTask.addOnSuccessListener { locationSettingsResponse: LocationSettingsResponse? ->
                // Request location updates.
                fusedLocationProviderClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.getMainLooper()
                )
                    .addOnSuccessListener {
                        binding.textView6.text = "requestLocationUpdatesWithCallback onSuccess"
                        Toast.makeText(this@LocationActivity, "Success", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        val errorMessage = e.message ?: "Unknown error"
                        Toast.makeText(this@LocationActivity, "requestLocationUpdatesWithCallback onFailure: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
            }
                .addOnFailureListener { e: Exception ->
                    val errorMessage = e.message ?: "Unknown error"
                    Toast.makeText(this@LocationActivity, "checkLocationSetting onFailure: $errorMessage", Toast.LENGTH_SHORT).show()
                }

        } catch (e: Exception) {
          Toast.makeText(this@LocationActivity,"requestLocationUpdatesWithCallback exception:${e.message}",Toast.LENGTH_SHORT).show()
        }
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
                    Log.e("LocationXX:",locationResult.lastLocation.latitude.toString())
                    Log.e("LocationXX:",locationResult.lastLocation.longitude.toString())
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
                Toast.makeText(this@LocationActivity, "Success", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(this@LocationActivity, "Fail", Toast.LENGTH_SHORT).show()
            }
    }

}



