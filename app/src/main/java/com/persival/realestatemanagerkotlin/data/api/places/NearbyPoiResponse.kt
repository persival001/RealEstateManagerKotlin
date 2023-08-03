package com.persival.realestatemanagerkotlin.data.api.places

data class NearbyPoiResponse(
    val results: List<Result>
) {
    data class Result(
        val name: String,
        val vicinity: String,
        val types: List<String>
        //... d'autres champs en fonction de ce que vous voulez récupérer
    )
}

