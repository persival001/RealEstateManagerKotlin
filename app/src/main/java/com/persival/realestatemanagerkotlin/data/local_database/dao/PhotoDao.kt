package com.persival.realestatemanagerkotlin.data.local_database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.data.local_database.model.PhotoDto
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Insert
    fun insert(photoDto: PhotoDto): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePhotos(photo: PhotoDto)

    @Insert
    suspend fun insertAll(photos: List<PhotoDto>)

    @Query("SELECT * FROM photo WHERE propertyId = :propertyId")
    fun getByPropertyId(propertyId: Long): List<PhotoDto>

    @Query("SELECT * FROM photo WHERE propertyId = :propertyId")
    fun getPhotosByPropertyIdAsCursor(propertyId: Long): Cursor

    @Query("SELECT * FROM photo")
    fun getAllPhotosAsCursor(): Cursor

    @Query("SELECT * FROM photo")
    fun getAllPhotos(): Flow<List<PhotoDto>>

    @Query("SELECT * FROM photo WHERE isSynced = 0")
    fun getUnsyncedPhotos(): List<PhotoDto>

    @Update
    suspend fun update(photoDto: PhotoDto): Int

    @Query(
        """
    UPDATE photo 
    SET description = :description, 
        photo_url = :photoUrl, 
        lastModified = :lastModified, 
        isSynced = 0
    WHERE propertyId = :propertyId
"""
    )
    suspend fun updatePhotoAndDescriptionByPropertyId(
        propertyId: Long,
        description: String,
        photoUrl: String,
        lastModified: Long
    )

    @Update
    fun updateBySelection(photo: PhotoDto): Int

    @Query("UPDATE photo SET isSynced = 1 WHERE id = :photoId")
    suspend fun markAsSynced(photoId: Long)

    @Delete
    suspend fun delete(photoDto: PhotoDto)

    @Query("DELETE FROM photo WHERE propertyId = :propertyId")
    suspend fun deletePhotosByPropertyId(propertyId: Long)

    @Query("DELETE FROM photo WHERE propertyId = :propertyId")
    fun deleteBySelection(propertyId: Long): Int

}
