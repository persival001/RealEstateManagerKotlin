package com.persival.realestatemanagerkotlin.ui.maps

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.persival.realestatemanagerkotlin.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    private val viewModel by viewModels<MapViewModel>()

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        viewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
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

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }
}
