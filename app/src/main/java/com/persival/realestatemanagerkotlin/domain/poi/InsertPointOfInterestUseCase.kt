package com.persival.realestatemanagerkotlin.domain.poi

import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertPointOfInterestUseCase @Inject constructor(
    private val pointOfInterestDao: PointOfInterestDao
) {
    suspend fun invoke(pointOfInterestEntity: PointOfInterestEntity) {
        pointOfInterestDao.insert(pointOfInterestEntity)
    }
}