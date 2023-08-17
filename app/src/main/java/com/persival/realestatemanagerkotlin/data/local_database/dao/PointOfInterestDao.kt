package com.persival.realestatemanagerkotlin.data.local_database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.data.local_database.model.PointOfInterestDto
import kotlinx.coroutines.flow.Flow

@Dao
interface PointOfInterestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(poi: PointOfInterestDto): Long

    @Insert
    suspend fun insertAll(pois: List<PointOfInterestDto>)

    @Query("SELECT * FROM point_of_interest")
    fun getAllPointsOfInterest(): Flow<List<PointOfInterestDto>>

    @Query("SELECT * FROM point_of_interest WHERE propertyId = :propertyId")
    fun getByPropertyId(propertyId: Long): List<PointOfInterestDto>

    @Query("SELECT * FROM point_of_interest")
    fun getAllPointsOfInterestAsCursor(): Cursor

    @Query("SELECT * FROM point_of_interest WHERE propertyId = :propertyId")
    fun getPointsOfInterestByPropertyIdAsCursor(propertyId: Long): Cursor

    @Update
    suspend fun update(poi: PointOfInterestDto): Int

    @Update
    fun updateBySelection(poi: PointOfInterestDto): Int

    @Delete
    suspend fun delete(poi: PointOfInterestDto)

    @Query("DELETE FROM point_of_interest WHERE propertyId = :propertyId")
    suspend fun deletePOIsByPropertyId(propertyId: Long)

    @Query("DELETE FROM point_of_interest WHERE propertyId = :propertyId")
    fun deleteBySelection(propertyId: Long): Int


}
