package com.persival.realestatemanagerkotlin.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.data.local_database.LocalDatabaseRepository
import com.persival.realestatemanagerkotlin.data.local_database.Photo
import com.persival.realestatemanagerkotlin.data.local_database.PointOfInterest
import com.persival.realestatemanagerkotlin.data.local_database.Property
import com.persival.realestatemanagerkotlin.domain.user.GetLoggedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val repository: LocalDatabaseRepository,
    private val getLoggedUserUseCase: GetLoggedUserUseCase
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
        saleDate: String?
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

    fun addPhotosForProperty(
        description: String,
        uri: String
    ) {
        viewModelScope.launch {
            val photo = Photo(
                0,
                1,
                description,
                uri
            )

            repository.insertPhoto(photo)
        }
    }

    fun addPoiForProperty(
        propertyId: Long,
        school: Boolean,
        park: Boolean,
        store: Boolean,
        hospital: Boolean,
        bus: Boolean
    ) {
        viewModelScope.launch {
            val poi = PointOfInterest(
                0,
                1,
                school,
                park,
                store,
                hospital,
                bus
            )

            repository.insertPointOfInterest(poi)
        }
    }
}