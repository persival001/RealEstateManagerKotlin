package com.persival.realestatemanagerkotlin.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.data.local_database.Photo

@Dao
interface PhotoDao {

    @Insert
    suspend fun insert(photo: Photo): Long

    @Update
    suspend fun update(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)

    @Query("SELECT * FROM photo")
    suspend fun getAllPhotos(): List<Photo>
}