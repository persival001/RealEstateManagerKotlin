package com.persival.realestatemanagerkotlin.domain.database

import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyDto

interface FirestoreRepository {
    suspend fun getAllProperties(): List<PropertyDto>
}
