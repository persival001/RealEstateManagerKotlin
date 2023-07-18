package com.persival.realestatemanagerkotlin.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentMapBinding
import com.persival.realestatemanagerkotlin.databinding.FragmentPropertiesBinding
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesFragment
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesViewModel
import com.persival.realestatemanagerkotlin.utils.viewBinding

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    companion object {
        fun newInstance() = MapFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create map with Lite Mode enabled
        val mapOptions = GoogleMapOptions().liteMode(true)
        val mapFragment = SupportMapFragment.newInstance(mapOptions)

        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }
}
