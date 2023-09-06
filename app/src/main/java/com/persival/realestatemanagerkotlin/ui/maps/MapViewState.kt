package com.persival.realestatemanagerkotlin.ui.maps

import com.google.android.gms.maps.model.LatLng

data class MapViewState(
    val id: Long,
    val address: String,
    val latLng: LatLng
)
