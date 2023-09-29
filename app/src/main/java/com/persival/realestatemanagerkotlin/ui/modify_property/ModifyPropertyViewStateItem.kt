package com.persival.realestatemanagerkotlin.ui.modify_property

import com.persival.realestatemanagerkotlin.utils.EquatableCallback

sealed class ModifyPropertyViewStateItem(
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
    ) : ModifyPropertyViewStateItem(Type.PHOTO)

    object EmptyState : ModifyPropertyViewStateItem(Type.EMPTY_STATE)
}
