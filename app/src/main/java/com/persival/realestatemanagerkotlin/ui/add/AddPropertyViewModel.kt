package com.persival.realestatemanagerkotlin.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.data.local_database.LocalDatabaseRepository
import com.persival.realestatemanagerkotlin.data.local_database.Property
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val repository: LocalDatabaseRepository
) : ViewModel() {

    fun addNewProperty(
        type: String,
        address: String,
        area: Int,
        rooms: Int,
        bathrooms: Int,
        bedrooms: Int,
        description: String,
        price: Int,
        entryDate: String,
        saleDate: String?,
        poi: String
    ) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val property = Property(
                0,
                type,
                address,
                area,
                rooms,
                bathrooms,
                bedrooms,
                description,
                price,
                true,
                entryDate,
                saleDate,
                1
            )

            repository.insertProperty(property)
        }
    }
}