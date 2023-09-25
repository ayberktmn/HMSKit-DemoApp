package com.example.accountkithms

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.example.accountkithms.databinding.ActivityLocationBinding
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationCallback
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationResult
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.SettingsClient
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment

class LocationActivity : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private var mLocationCallback: LocationCallback? = null

    // HUAWEI map
    private var hMap: HuaweiMap? = null

    private var mMapView: MapView? = null

    companion object {

        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)


        binding.btnlocationupdate2.setOnClickListener {
            requestLocationUpdates()
        }
        binding.btnlocationstop2.setOnClickListener {
            removeLocationUpdatesWithCallback()
        }
        binding.btnMap.setOnClickListener {
            startActivity(intent)
        }
        binding.txtBack.setOnClickListener {
            val intent = Intent(this@LocationActivity,HomeActivity::class.java)
            startActivity(intent)
        }

        MapsInitializer.initialize(this)
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
    }

    fun requestLocationUpdates(){

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 3000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null) {

                    Toast.makeText(
                        this@LocationActivity,
                        "LocationX" + locationResult.lastLocation.latitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.txtLocX2.text = locationResult.lastLocation.latitude.toString()
                    binding.txtLocY2.text = locationResult.lastLocation.longitude.toString()

                    Toast.makeText(
                        this@LocationActivity,
                        "LocationY" + locationResult.lastLocation.longitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
            .addOnSuccessListener {
                // TODO: Define callback for API call success.
            }
            .addOnFailureListener {
                Toast.makeText(this@LocationActivity, "Fail", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeLocationUpdatesWithCallback() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                .addOnSuccessListener {
                    Toast.makeText(this@LocationActivity, "Location Durduruldu", Toast.LENGTH_SHORT).show()
                    binding.txtLocX2.text = "0000"
                    binding.txtLocY2.text = "0000"
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@LocationActivity, "Location Durdurulamadi", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            // Hata işleme...
        }
    }

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE])
    override fun onMapReady(map: HuaweiMap) {
        hMap = map
        // Enable the my-location layer.
        hMap!!.isMyLocationEnabled = true
        // Enable the my-location icon.
        hMap!!.uiSettings.isMyLocationButtonEnabled = true
    }
}