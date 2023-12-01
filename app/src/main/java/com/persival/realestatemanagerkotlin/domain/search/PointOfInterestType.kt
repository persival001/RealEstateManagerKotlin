package com.persival.realestatemanagerkotlin.domain.search

enum class PointOfInterestType(val type: String) {
    SCHOOL("School"),
    RESTAURANT("Restaurant"),
    PUBLIC_TRANSPORT("Public transport"),
    HOSPITAL("Hospital"),
    STORE("Store"),
    GREEN_SPACES("Green spaces");

    override fun toString(): String {
        return type
    }
}




