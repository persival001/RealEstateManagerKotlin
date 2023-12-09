package com.persival.realestatemanagerkotlin.data.local_database.point_of_interest

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PointOfInterestDao {

    @Insert
    fun insert(poi: PointOfInterestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pois: List<PointOfInterestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePointsOfInterest(poi: PointOfInterestEntity)

    @Query("SELECT * FROM point_of_interest")
    fun getAllPointsOfInterestAsCursor(): Cursor

    @Query("SELECT * FROM point_of_interest WHERE isSynced = 0")
    fun getUnsyncedPointsOfInterest(): List<PointOfInterestEntity>

    @Update
    suspend fun update(poi: PointOfInterestEntity): Int

    @Query("UPDATE point_of_interest SET isSynced = 1 WHERE id = :poiId")
    suspend fun markAsSynced(poiId: Long)

    @Query("DELETE FROM point_of_interest WHERE propertyId = :propertyId")
    suspend fun deletePOIsByPropertyId(propertyId: Long)

}
