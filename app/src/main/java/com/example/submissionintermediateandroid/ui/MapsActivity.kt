package com.example.submissionintermediateandroid.ui

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.submissionintermediateandroid.R
import com.example.submissionintermediateandroid.data.Result
import com.example.submissionintermediateandroid.databinding.ActivityMapsBinding
import com.example.submissionintermediateandroid.ui.viewmodels.MapsViewModel
import com.example.submissionintermediateandroid.ui.viewmodels.ViewModelFactory
import com.example.submissionintermediateandroid.util.dataStore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this, application.dataStore)
    }
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
        addManyMarker()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this@MapsActivity, "Map style parsing failed", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(this@MapsActivity, "Error: Can't find map style", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addManyMarker() {
        // observing stories
        mapsViewModel.getStoriesWithLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        val response = result.data
                        val listStory = response.listStory
                        listStory.forEach { story ->
                            val latLng = LatLng(story.lat!!, story.lon!!)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(story.name)
                                    .snippet(story.description)
                            )
                            boundsBuilder.include(latLng)
                        }

                        val bounds: LatLngBounds = boundsBuilder.build()
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                resources.displayMetrics.widthPixels,
                                resources.displayMetrics.heightPixels,
                                300
                            )
                        )
                    }
                    is Result.Error -> {
                        showToast("Error: ${result.error}")
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}