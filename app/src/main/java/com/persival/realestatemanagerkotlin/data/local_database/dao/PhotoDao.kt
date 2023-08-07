package com.persival.realestatemanagerkotlin.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Insert
    suspend fun insert(photoEntity: PhotoEntity): Long

    @Insert
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Update
    suspend fun update(photoEntity: PhotoEntity)

    @Delete
    suspend fun delete(photoEntity: PhotoEntity)

    @Query("DELETE FROM photo WHERE propertyId = :propertyId")
    suspend fun deletePhotosByPropertyId(propertyId: Long)

    @Query("SELECT * FROM photo")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

}