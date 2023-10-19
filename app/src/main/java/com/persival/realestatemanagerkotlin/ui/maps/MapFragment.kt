package com.persival.realestatemanagerkotlin.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.ui.gps_dialog.GpsDialogFragment
import com.persival.realestatemanagerkotlin.ui.navigation.NavigationHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private val viewModel by viewModels<MapViewModel>()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // Check GPS activation
        viewModel.isGpsActivated().asLiveData().observe(viewLifecycleOwner) { gps ->
            if (gps == false) {
                GpsDialogFragment().show(
                    requireActivity().supportFragmentManager,
                    "GpsDialogFragment"
                )
            }
        }

        // Add marker for user position
        viewModel.locationUpdates.observe(viewLifecycleOwner) { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)

                val userMarkerOptions = MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.your_position))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                googleMap?.addMarker(userMarkerOptions)

                // Move the camera to the current location
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))

                // Add a circle around the current location
                val circleOptions = CircleOptions()
                    .center(latLng)
                    .radius(5000.0)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x110000FF)
                googleMap?.addCircle(circleOptions)
            }
        }

        // Initialize requestPermissionLauncher
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    viewModel.refreshLocationPermission()
                }
            }

    }

    override fun onResume() {
        // Refresh GPS activation and location permission
        viewModel.refreshGpsActivation()
        viewModel.refreshLocationPermission()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        super.onResume()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Observe the propertiesLatLng from the ViewModel
        viewModel.propertiesLatLngWithId.asLiveData().observe(viewLifecycleOwner) { mapViewStateList ->
            mapViewStateList.forEach { mapViewState ->
                val markerOptions = MarkerOptions()
                    .position(mapViewState.latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(mapViewState.address)
                val marker = googleMap?.addMarker(markerOptions)
                marker?.tag = mapViewState.id
            }
        }

        // Address must be entered with Google autocomplete
        viewModel.hasNullValues.asLiveData().observe(viewLifecycleOwner) { hasNull ->
            if (hasNull) {
                Toast.makeText(context, getString(R.string.add_address_with_autocomplete), Toast.LENGTH_SHORT).show()
            }
        }

        // When user click on a marker, the details are displayed
        googleMap?.setOnMarkerClickListener { marker ->
            val propertyId = marker.tag as? Long
            if (propertyId != null) {
                viewModel.updateSelectedPropertyId(propertyId)
                (activity as? NavigationHandler)?.navigateToDetail()
            }
            true
        }


    }

}
