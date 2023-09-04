package com.persival.realestatemanagerkotlin.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
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
        viewModel.isGpsActivatedLiveData().observe(viewLifecycleOwner) { gps ->
            if (gps == false) {
                GpsDialogFragment().show(
                    requireActivity().supportFragmentManager,
                    "GpsDialogFragment"
                )
            }
        }

        // Add marker for user position
        viewModel.getLocationLiveData().observe(viewLifecycleOwner) { location ->
            Log.d("MapFragment", "Received location update: $location")
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
                    viewModel.onResume()
                }
            }

        // Observe the propertiesLatLng from the ViewModel
        viewModel.propertiesLatLng.observe(viewLifecycleOwner) { mapViewStateList ->
            mapViewStateList.forEach { mapViewState ->
                val markerOptions = MarkerOptions()
                    .position(mapViewState.latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title("Property")
                googleMap?.addMarker(markerOptions)
            }
        }
    }

    override fun onResume() {
        // Refresh GPS activation and location permission
        Log.d("MapFragment", "onResume called")
        viewModel.refreshGpsActivation()
        viewModel.onResume()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        super.onResume()
    }


    override fun onMapReady(map: GoogleMap) {
        Log.d("MapFragment", "Map is ready")
        googleMap = map
        viewModel.getAllPropertiesLatLng()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopLocation()
    }
}
