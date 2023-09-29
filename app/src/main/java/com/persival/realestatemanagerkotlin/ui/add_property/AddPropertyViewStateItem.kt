package com.persival.realestatemanagerkotlin.ui.add_property

import com.persival.realestatemanagerkotlin.utils.EquatableCallback

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
        val onDeleteEvent: EquatableCallback,
    ) : AddPropertyViewStateItem(Type.PHOTO)

    object EmptyState : AddPropertyViewStateItem(Type.EMPTY_STATE)
}
