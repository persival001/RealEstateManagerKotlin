package com.persival.realestatemanagerkotlin.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentMapBinding
import com.persival.realestatemanagerkotlin.databinding.FragmentPropertiesBinding
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesFragment
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesViewModel
import com.persival.realestatemanagerkotlin.utils.viewBinding

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    companion object {
        fun newInstance() = PropertiesFragment()
    }

    private val binding by viewBinding { FragmentMapBinding.bind(it) }
    private val viewModel by viewModels<MapViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // TODO
    }
}