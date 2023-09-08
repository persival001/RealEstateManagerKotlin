package com.persival.realestatemanagerkotlin.domain.property

import kotlinx.coroutines.flow.StateFlow

interface PropertyRepository {

    val selectedId: StateFlow<Long?>

    fun setSelectedId(id: Long?)

}
