package com.persival.realestatemanagerkotlin.data.property

import com.persival.realestatemanagerkotlin.domain.property.PropertyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyDataRepository @Inject constructor() : PropertyRepository {

    // TODO Use a stateflow here to avoid destroying a detail fragment every click on list
    private var selectedId: Long? = null

    override fun getSelectedId(): Long? = selectedId

    override fun setSelectedId(id: Long?) {
        selectedId = id
    }
}
