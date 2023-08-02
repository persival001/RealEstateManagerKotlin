package com.persival.realestatemanagerkotlin.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.data.local_database.LocalDatabaseRepository
import com.persival.realestatemanagerkotlin.data.local_database.Photo
import com.persival.realestatemanagerkotlin.data.local_database.Property
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
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
        imageUris: List<String>,
        photoDescriptions: List<String>
    ) {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
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

            val propertyId: Long = repository.insertProperty(property)


            imageUris.forEachIndexed { index, uri ->
                if (uri != null) {
                    val photo = Photo(
                        propertyId = propertyId,
                        description = photoDescriptions.getOrNull(index) ?: "",
                        url = uri
                    )
                    repository.insertPhoto(photo)
                }
            }
        }
    }
}
