package com.persival.realestatemanagerkotlin.data.storage

import com.persival.realestatemanagerkotlin.domain.property.PropertyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyDataRepository @Inject constructor(

) : PropertyRepository {

    private var selectedId: Long? = null

    override fun getSelectedId(): Long? = selectedId

    override fun setSelectedId(id: Long?) {
        selectedId = id
    }
}
