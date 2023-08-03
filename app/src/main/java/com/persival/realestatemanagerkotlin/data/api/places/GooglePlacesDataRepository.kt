package com.persival.realestatemanagerkotlin.data.api.places

import com.persival.realestatemanagerkotlin.BuildConfig.MAPS_API_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GooglePlacesDataRepository {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val googlePlacesApi: GooglePlacesApi = retrofit.create(GooglePlacesApi::class.java)

    // Define a CoroutineScope for launching the coroutine
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Define a function for making the API call
    fun getPlaces() {
        coroutineScope.launch {
            try {
                val response = googlePlacesApi.getNearbyPlaces(
                    "lat,long",
                    1000,
                    "school",
                    MAPS_API_KEY
                )
                // Process the response
            } catch (e: Exception) {
                // Handle any exceptions
            }
        }
    }
}
