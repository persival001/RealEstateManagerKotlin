package com.persival.realestatemanagerkotlin.domain.property

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSelectedPropertyIdUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository,
) {
    operator fun invoke(): StateFlow<Long?> {
        return propertyRepository.selectedId
    }
}
