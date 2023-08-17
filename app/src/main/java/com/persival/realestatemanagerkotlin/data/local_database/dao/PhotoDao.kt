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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photoDto: PhotoDto): Long

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

    @Update
    suspend fun update(photoDto: PhotoDto): Int

    @Update
    fun updateBySelection(photo: PhotoDto): Int

    @Delete
    suspend fun delete(photoDto: PhotoDto)

    @Query("DELETE FROM photo WHERE propertyId = :propertyId")
    suspend fun deletePhotosByPropertyId(propertyId: Long)

    @Query("DELETE FROM photo WHERE propertyId = :propertyId")
    fun deleteBySelection(propertyId: Long): Int

}
