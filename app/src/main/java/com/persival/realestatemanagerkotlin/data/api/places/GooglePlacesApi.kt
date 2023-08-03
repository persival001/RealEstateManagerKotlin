package com.persival.realestatemanagerkotlin.data.api.places

import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApi {

    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String?,
        @Query("radius") radius: Int,
        @Query("type") type: String?,
        @Query("key") apiKey: String?
    ): NearbyPoiResponse?

}