package com.persival.realestatemanagerkotlin.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.persival.realestatemanagerkotlin.data.current_mail.CurrentPropertyIdRepository
import com.persival.realestatemanagerkotlin.utils.Event
import com.persival.realestatemanagerkotlin.utils.asLiveDataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    currentPropertyIdRepository: CurrentPropertyIdRepository
) : ViewModel() {

    private var isTablet: Boolean = false

    // Can't use viewModelScope.launch{}, because we would collect the flow even when user put the application on background
    val mainViewActionLiveData: LiveData<Event<MainViewAction>> =
        currentPropertyIdRepository.currentPropertyIdChannel.asLiveDataEvent(Dispatchers.IO) {
            if (!isTablet) {
                emit(MainViewAction.NavigateToDetailActivity)
            }
        }

    fun onResume(isTablet: Boolean) {
        this.isTablet = isTablet
    }
}