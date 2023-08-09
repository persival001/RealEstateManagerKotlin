package com.persival.realestatemanagerkotlin.data.local_database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity

@Dao
interface PointOfInterestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(poi: PointOfInterestEntity): Long

    @Update
    fun update(poi: PointOfInterestEntity): Int

    @Delete
    fun delete(poi: PointOfInterestEntity): Int

    @Query("DELETE FROM point_of_interest WHERE propertyId = :propertyId")
    suspend fun deletePOIsByPropertyId(propertyId: Long)

    @Query("SELECT * FROM point_of_interest")
    fun getAllPointsOfInterest(): Cursor

    @Query("SELECT * FROM point_of_interest WHERE propertyId = :propertyId")
    fun getPointsOfInterestByPropertyId(propertyId: Long): Cursor

    @Query("DELETE FROM point_of_interest WHERE propertyId = :propertyId")
    fun deleteBySelection(propertyId: Long): Int

    @Update
    fun updateBySelection(poi: PointOfInterestEntity): Int

    @Query("SELECT * FROM point_of_interest WHERE propertyId = :propertyId")
    fun getByPropertyId(propertyId: Long): List<PointOfInterestEntity>

    @Query("SELECT * FROM point_of_interest")
    fun getAllPointsOfInterestAsCursor(): Cursor


}
