package com.persival.realestatemanagerkotlin.domain.database

interface SyncRepository {
    suspend fun synchronizeData()
}