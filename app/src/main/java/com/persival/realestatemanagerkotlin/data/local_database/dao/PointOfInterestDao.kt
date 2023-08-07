package com.persival.realestatemanagerkotlin.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PointOfInterestDao {

    @Insert
    suspend fun insert(poi: PointOfInterestEntity): Long

    @Insert
    suspend fun insertAll(pois: List<PointOfInterestEntity>)

    @Update
    suspend fun update(poi: PointOfInterestEntity)

    @Delete
    suspend fun delete(poi: PointOfInterestEntity)

    @Query("DELETE FROM point_of_interest WHERE propertyId = :propertyId")
    suspend fun deletePOIsByPropertyId(propertyId: Long)

    @Query("SELECT * FROM point_of_interest")
    fun getAllPointsOfInterest(): Flow<List<PointOfInterestEntity>>
}