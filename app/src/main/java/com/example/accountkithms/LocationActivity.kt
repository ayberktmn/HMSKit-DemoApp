package com.example.accountkithms


import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.accountkithms.databinding.ActivityLocationBinding
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationCallback
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationResult
import com.huawei.hms.location.LocationServices
import com.huawei.hms.location.SettingsClient
import com.huawei.hms.maps.MapsInitializer
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.AddressDetail
import com.huawei.hms.site.api.model.HwLocationType
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.TextSearchRequest
import com.huawei.hms.site.api.model.TextSearchResponse
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class LocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private var mLocationCallback: LocationCallback? = null
    private lateinit var searchService: SearchService
    private lateinit var resultTextView: TextView
    private lateinit var queryInput: EditText

    companion object {

        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        MapsInitializer.initialize(this@LocationActivity)
        setContentView(binding.root)

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)  //fusedlocation istemcisini baslatir
        settingsClient = LocationServices.getSettingsClient(this)

        try {
            searchService = SearchServiceFactory.create(this, URLEncoder.encode("DAEDAGGSnnwsrUzHM4zgbQmGc+AVPgdEomD1PZf1Pd/Bc6lpp7A+5i67vqu1TS7cj744H+8zjq/YTfQYUsLqaqW9A0xlbhmNohD89w==", "utf-8"))
        } catch (e: UnsupportedEncodingException) {         // Site kit search service olusturmasini saglar
            Log.e(TAG, "encode apikey error")
        }

        queryInput = findViewById(R.id.edit_text_text_search_query)
        resultTextView = findViewById(R.id.response_text_search)

        binding.btnNearby.setOnClickListener {
            val intent = Intent(this@LocationActivity,NearbySiteActivity::class.java)
            startActivity(intent)
        }

        binding.btnSearch.setOnClickListener {
           search()
        }

        binding.btnlocationupdate2.setOnClickListener {
            requestLocationPermission()
        }
        binding.btnlocationstop2.setOnClickListener {
            removeLocationUpdatesWithCallback()
        }
        binding.txtBack.setOnClickListener {
            val intent = Intent(this@LocationActivity,HomeActivity::class.java)
            startActivity(intent)
        }
    }

    fun search() {
        val textSearchRequest = TextSearchRequest()
        textSearchRequest.query = queryInput.text.toString()
        textSearchRequest.hwPoiType = HwLocationType.TOWER
        searchService.textSearch(textSearchRequest, object : SearchResultListener<TextSearchResponse> {
            override fun onSearchResult(textSearchResponse: TextSearchResponse) {
                val response = StringBuilder("\n")
                response.append("success\n")
                var count = 1
                var addressDetail: AddressDetail
                for (site in textSearchResponse.sites) {
                    addressDetail = site.address
                    response.append(
                        String.format(
                            "[%s]  name: %s, formatAddress: %s, country: %s, countryCode: %s \r\n",
                            "" + count++, site.name, site.formatAddress,
                            if (addressDetail == null) "" else addressDetail.country,
                            if (addressDetail == null) "" else addressDetail.countryCode
                        )
                    )
                }

                Log.d(TAG, "search result is : $response")
                resultTextView.text = response.toString()

            }

            override fun onSearchError(searchStatus: SearchStatus) {
                Log.e(TAG, "onSearchError is: " + searchStatus.errorCode)
                Toast.makeText(
                    this@LocationActivity,
                    "Search Error" ,
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
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
    private fun requestLocationPermission() {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (arePermissionsGranted(permissions)) {
            // İzinler zaten verildi
            requestLocationUpdates()
        } else {
            // İzinleri kullanıcıdan iste
            requestPermissions(permissions, PackageManager.PERMISSION_GRANTED)
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PackageManager.PERMISSION_GRANTED) {
            if (arePermissionsGranted(permissions)) {
                // İzinler verildi
                requestLocationUpdates()
            } else {
                // İzinler reddedildi, kullanıcıyı bilgilendirin
                Toast.makeText(
                    this,
                    "Konum izni reddedildi.",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
}