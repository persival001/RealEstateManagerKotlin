package com.persival.realestatemanagerkotlin.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.data.local_database.PointOfInterest

@Dao
interface PointOfInterestDao {

    @Insert
    suspend fun insert(poi: PointOfInterest): Long

    @Update
    suspend fun update(poi: PointOfInterest)

    @Delete
    suspend fun delete(poi: PointOfInterest)

    @Query("SELECT * FROM point_of_interest")
    suspend fun getAllPointsOfInterest(): List<PointOfInterest>
}