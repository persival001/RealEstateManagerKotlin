package com.persival.realestatemanagerkotlin.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase
) : ViewModel() {
    private val _details = MutableLiveData<List<DetailViewStateItem>>()
    val details: LiveData<List<DetailViewStateItem>> = _details

    init {
        // loadDetails()
    }

    /*private fun loadDetails() {
        viewModelScope.launch {
            val details = getPropertyWithPhotoAndPOIUseCase.invoke(0)
            _details.value = details.map { detail ->

            }
        }
    }*/
}