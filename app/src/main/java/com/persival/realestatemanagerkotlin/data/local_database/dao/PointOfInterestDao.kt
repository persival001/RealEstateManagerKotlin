package com.persival.realestatemanagerkotlin.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.poi.PointOfInterestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PointOfInterestDao {

    @Insert
    suspend fun insert(poi: PointOfInterestEntity): Long

    @Update
    suspend fun update(poi: PointOfInterestEntity)

    @Delete
    suspend fun delete(poi: PointOfInterestEntity)

    @Query("SELECT * FROM point_of_interest")
    fun getAllPointsOfInterest(): Flow<List<PointOfInterestEntity>>

    @Query("SELECT * FROM point_of_interest WHERE propertyId = :propertyId")
    suspend fun getPOIsByPropertyId(propertyId: Long): List<PointOfInterestEntity>
}