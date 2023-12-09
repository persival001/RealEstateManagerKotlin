package com.persival.realestatemanagerkotlin.data.local_database.photo

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Insert
    fun insert(photoEntity: PhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePhotos(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("SELECT * FROM photo WHERE propertyId = :propertyId")
    fun getByPropertyId(propertyId: Long): List<PhotoEntity>

    @Query("SELECT * FROM photo")
    fun getAllPhotosAsCursor(): Cursor

    @Query("SELECT * FROM photo WHERE isSynced = 0")
    fun getUnsyncedPhotos(): List<PhotoEntity>

    @Transaction
    @Query("SELECT * FROM photo WHERE propertyId = :propertyId")
    fun getPropertyPhotos(propertyId: Long): Flow<List<PhotoEntity>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(photoEntity: PhotoEntity): Int

    @Query("UPDATE photo SET photo_url = :photoUrl, description = :description WHERE id = :photoId")
    suspend fun updateByPhotoId(photoId: Long, photoUrl: String, description: String): Int

    @Query("UPDATE photo SET isSynced = 1 WHERE id = :photoId")
    suspend fun markAsSynced(photoId: Long)

    @Delete
    suspend fun delete(photoEntity: PhotoEntity)

    @Query("DELETE FROM photo WHERE propertyId = :propertyId AND id = :photoId")
    suspend fun deletePhotoByPropertyIdAndPhotoId(propertyId: Long, photoId: Long)

}
