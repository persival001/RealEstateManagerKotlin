package com.persival.realestatemanagerkotlin.ui.add_or_modify_property

import com.persival.realestatemanagerkotlin.utils.EquatableCallbackWithParam

sealed class AddOrModifyPropertyViewStateItem(
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
    ) : AddOrModifyPropertyViewStateItem(Type.PHOTO)

    object EmptyStateOrModify : AddOrModifyPropertyViewStateItem(Type.EMPTY_STATE)
}
