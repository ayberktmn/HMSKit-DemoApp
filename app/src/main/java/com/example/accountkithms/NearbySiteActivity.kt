package com.example.accountkithms

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.google.android.libraries.mapsplatform.transportation.consumer.model.MarkerType
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationCallback
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationResult
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.MapView
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.SupportMapFragment
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.CameraPosition
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.Coordinate
import com.huawei.hms.site.api.model.LocationType
import com.huawei.hms.site.api.model.NearbySearchRequest
import com.huawei.hms.site.api.model.NearbySearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import java.net.URLEncoder

class NearbySiteActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var hMap: HuaweiMap? = null
    private lateinit var searchService: SearchService
    private var mMapView: MapView? = null
    private var mMarker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_site)
        getLocation()
        var mapViewBundle: Bundle? = null

        var mSupportMapFragment: SupportMapFragment? = null
        mSupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mSupportMapFragment?.getMapAsync(this@NearbySiteActivity)
        mMapView = findViewById(R.id.mapView)

        mMapView?.apply {
            onCreate(mapViewBundle)
            getMapAsync(this@NearbySiteActivity)
        }
    }

    private fun getLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                var currentLatitude = locationResult.lastLocation.latitude
                var currentLongitude = locationResult.lastLocation.longitude
                val build =
                    CameraPosition.Builder().target(LatLng(currentLatitude, currentLongitude))
                        .zoom(12f).build()
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(build)
                hMap?.animateCamera(cameraUpdate)
                hMap?.setMaxZoomPreference(20f)
                hMap?.setMinZoomPreference(1f)
                search()
            }
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1800000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener {}.addOnFailureListener {}


    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun search() {

        var searchService: SearchService
        searchService = SearchServiceFactory.create(this, URLEncoder.encode("DAEDAGGSnnwsrUzHM4zgbQmGc+AVPgdEomD1PZf1Pd/Bc6lpp7A+5i67vqu1TS7cj744H+8zjq/YTfQYUsLqaqW9A0xlbhmNohD89w==", "utf-8"))
        val request = NearbySearchRequest()
        val swtchbtn = findViewById<Switch>(R.id.swith_hosp_pharmcy)

        val location = Coordinate(38.613490, 27.4635)
        request.location = location
        if (swtchbtn.isChecked) {
            request.query = "HOSPITAL"
            request.poiType = LocationType.HOSPITAL
            request.radius = 10000
            Toast.makeText(this@NearbySiteActivity,"Yakinda HOSPITAL bulundu",Toast.LENGTH_SHORT).show()
        } else {
            request.query = "PHARMACY"
            request.poiType = LocationType.PHARMACY
            request.radius = 2000
            Toast.makeText(this@NearbySiteActivity,"Yakinda PHARMACY bulundu",Toast.LENGTH_SHORT).show()
        }
        request.language = "tr"
        request.pageIndex = 1
        request.pageSize = 10


        val resultListener: SearchResultListener<NearbySearchResponse?> =
            object : SearchResultListener<NearbySearchResponse?> {
                override fun onSearchResult(results: NearbySearchResponse?) {
                    val sites = results!!.sites
                    if (results == null || results.totalCount <= 0 || sites == null || sites.size <= 0) {
                        return
                    }
                    for (site in sites) {
                        if (site.name != null || site.poi.phone != null || site.formatAddress != null) {
                            val latLng = LatLng(site.location.lat, site.location.lng)
                            val title = site.name ?: ""
                            val snippet = site.formatAddress ?: ""

                            val isHospital = site.poi?.poiTypes?.any { it.contains("HOSPITAL", true) } == true

                            // Check if the site is a pharmacy
                            val isPharmacy = site.poi?.poiTypes?.any { it.contains("PHARMACY", true) } == true
                            // Create a MarkerOptions for the location
                            val options = MarkerOptions()
                                .position(latLng)
                                .title(title)
                                .snippet(snippet)
                            if (swtchbtn.isChecked && isHospital) {
                                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital))
                            } else if (!swtchbtn.isChecked && isPharmacy) {
                                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.drugs))
                            }

                            // Add the marker to the map
                            hMap?.addMarker(options)
                        }
                        Log.i(
                            "TAG",
                            String.format(
                                "siteId: '%s', name: %s\r\n",
                                site.siteId,
                                site.name


                            )
                        )
                    }
                }

                override fun onSearchError(status: SearchStatus) {
                    Log.i(
                        "TAG",
                        "Error : " + status.errorCode + " " + status.errorMessage
                    )
                }
            }
        searchService.nearbySearch(request, resultListener)
    }



    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE])
    override fun onMapReady(map: HuaweiMap) {
        hMap = map
        // Enable the my-location layer.
        hMap!!.isMyLocationEnabled = true
        // Enable the my-location icon.
        hMap!!.uiSettings.isMyLocationButtonEnabled = true

    }
}