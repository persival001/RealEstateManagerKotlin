package com.persival.realestatemanagerkotlin.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity

@Dao
interface PhotoDao {

    @Insert
    suspend fun insert(photoEntity: PhotoEntity): Long

    @Update
    suspend fun update(photoEntity: PhotoEntity)

    @Delete
    suspend fun delete(photoEntity: PhotoEntity)

    @Query("SELECT * FROM photo")
    suspend fun getAllPhotos(): List<PhotoEntity>

    @Query("SELECT * FROM photo WHERE propertyId = :propertyId")
    suspend fun getPhotosByPropertyId(propertyId: Long): List<PhotoEntity>
}