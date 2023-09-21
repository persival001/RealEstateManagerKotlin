package com.persival.realestatemanagerkotlin.ui.add_or_modify_property

import com.persival.realestatemanagerkotlin.utils.EquatableCallback

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
        val isFavorite: Boolean,
        val onFavoriteEvent: EquatableCallback,
        val onDeleteEvent: EquatableCallback,
    ) : AddOrModifyPropertyViewStateItem(Type.PHOTO)

    object EmptyState : AddOrModifyPropertyViewStateItem(Type.EMPTY_STATE)
}
