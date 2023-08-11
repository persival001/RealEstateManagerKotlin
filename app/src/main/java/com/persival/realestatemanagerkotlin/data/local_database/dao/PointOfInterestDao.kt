package com.persival.realestatemanagerkotlin.data.local_database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PointOfInterestDao {

    // Local database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(poi: PointOfInterestEntity): Long

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

    // Content provider
    @Query("DELETE FROM point_of_interest WHERE propertyId = :propertyId")
    fun deleteBySelection(propertyId: Long): Int

    @Update
    fun updateBySelection(poi: PointOfInterestEntity): Int

    @Query("SELECT * FROM point_of_interest WHERE propertyId = :propertyId")
    fun getByPropertyId(propertyId: Long): List<PointOfInterestEntity>

    @Query("SELECT * FROM point_of_interest")
    fun getAllPointsOfInterestAsCursor(): Cursor

    @Query("SELECT * FROM point_of_interest WHERE propertyId = :propertyId")
    fun getPointsOfInterestByPropertyIdAsCursor(propertyId: Long): Cursor

}
