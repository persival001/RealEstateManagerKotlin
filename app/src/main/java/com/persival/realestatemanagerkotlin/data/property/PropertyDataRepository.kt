package com.persival.realestatemanagerkotlin.data.property

import com.persival.realestatemanagerkotlin.domain.property.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyDataRepository @Inject constructor() : PropertyRepository {

    private val selectedIdStateFlow = MutableStateFlow<Long?>(null)

    override val selectedId: StateFlow<Long?> get() = selectedIdStateFlow

    override fun setSelectedId(id: Long?) {
        selectedIdStateFlow.value = id
    }
}
