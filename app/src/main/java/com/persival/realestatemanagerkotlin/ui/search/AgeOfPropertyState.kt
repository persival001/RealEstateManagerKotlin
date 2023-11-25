package com.persival.realestatemanagerkotlin.ui.search

enum class AgeOfPropertyState(val timeFilter: String) {
    LESS_THAN_A_MONTH("-1 month"),
    LESS_THAN_SIX_MONTHS("-6 months"),
    LESS_THAN_A_YEAR("-1 year");

    override fun toString(): String {
        return timeFilter
    }
}
