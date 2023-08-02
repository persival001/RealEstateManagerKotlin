package com.persival.realestatemanagerkotlin.domain.photo

import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import javax.inject.Singleton

@Singleton
class InsertPhotoUseCase @javax.inject.Inject constructor(
    private val photoDao: PhotoDao
) {
    suspend fun invoke(photoEntity: PhotoEntity) {
        photoDao.insert(photoEntity)
    }
}