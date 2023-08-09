package com.persival.realestatemanagerkotlin.data.local_database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photoEntity: PhotoEntity): Long

    @Update
    fun update(photoEntity: PhotoEntity): Int

    @Delete
    fun delete(photoEntity: PhotoEntity): Int

    @Query("DELETE FROM photo WHERE propertyId = :propertyId")
    suspend fun deletePhotosByPropertyId(propertyId: Long)

    @Query("SELECT * FROM photo")
    fun getAllPhotos(): Cursor

    @Query("SELECT * FROM photo WHERE propertyId = :propertyId")
    fun getPhotosByPropertyId(propertyId: Long): Cursor

    @Query("DELETE FROM photo WHERE propertyId = :propertyId")
    fun deleteBySelection(propertyId: Long): Int

    @Update
    fun updateBySelection(photo: PhotoEntity): Int

    @Query("SELECT * FROM photo WHERE propertyId = :propertyId")
    fun getByPropertyId(propertyId: Long): List<PhotoEntity>

    @Query("SELECT * FROM photo")
    fun getAllPhotosAsCursor(): Cursor


}
