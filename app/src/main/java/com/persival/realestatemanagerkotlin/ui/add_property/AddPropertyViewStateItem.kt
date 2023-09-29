package com.persival.realestatemanagerkotlin.ui.add_property

import com.persival.realestatemanagerkotlin.utils.EquatableCallbackWithParam

sealed class AddPropertyViewStateItem(
    val type: Type,
) {
    enum class Type {
        PHOTO,
        EMPTY_STATE,
    }

    data class Photo(
        val id: Long,
        val propertyId: Long,
        val description: String,
        val photoUrl: String,
        val onDeleteEvent: EquatableCallbackWithParam<Int>,
    ) : AddPropertyViewStateItem(Type.PHOTO)

    object EmptyState : AddPropertyViewStateItem(Type.EMPTY_STATE)
}
