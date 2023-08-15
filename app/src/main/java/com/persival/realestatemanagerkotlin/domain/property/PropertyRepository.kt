package com.persival.realestatemanagerkotlin.domain.property

interface PropertyRepository {

    fun getSelectedId(): Long?

    fun setSelectedId(id: Long?)
    
}
