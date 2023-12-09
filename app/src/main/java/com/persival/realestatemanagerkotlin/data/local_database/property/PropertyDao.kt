package com.persival.realestatemanagerkotlin.data.local_database.property

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois.PropertyWithPhotosAndPoisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Insert
    fun insert(propertyEntity: PropertyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(propertyEntity: List<PropertyEntity>)

    @Query("SELECT * FROM property")
    fun getAllPropertiesAsCursor(): Cursor

    @Query("SELECT * FROM property WHERE id == :propertyId")
    fun getPropertyByIdAsCursor(propertyId: Long): Cursor

    @Query("SELECT latLng FROM property")
    fun getAllLatLng(): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM property")
    fun getAllProperties(): Flow<List<PropertyWithPhotosAndPoisEntity>>

    @Transaction
    @Query(
        """
            SELECT * FROM property
WHERE (:type IS NULL OR type = :type) 
AND (:minPrice IS NULL OR price >= :minPrice)
AND (:maxPrice IS NULL OR price <= :maxPrice)
AND (:minArea IS NULL OR area >= :minArea)
AND (:maxArea IS NULL OR area <= :maxArea)
AND (:isSold IS NULL OR isSold = :isSold)
AND (:timeFilter IS NULL OR entryDate >= date('now', :timeFilter))
AND ((:poiSchool IS NULL) OR EXISTS (
    SELECT 1 FROM point_of_interest 
    WHERE point_of_interest.propertyId = property.id 
    AND point_of_interest.poi IN (:poiSchool)
))
AND ((:poiRestaurant IS NULL) OR EXISTS (
    SELECT 1 FROM point_of_interest 
    WHERE point_of_interest.propertyId = property.id 
    AND point_of_interest.poi IN (:poiRestaurant)
))
AND ((:poiPublicTransport IS NULL) OR EXISTS (
    SELECT 1 FROM point_of_interest 
    WHERE point_of_interest.propertyId = property.id 
    AND point_of_interest.poi IN (:poiPublicTransport)
))
AND ((:poiHospital IS NULL) OR EXISTS (
    SELECT 1 FROM point_of_interest 
    WHERE point_of_interest.propertyId = property.id 
    AND point_of_interest.poi IN (:poiHospital)
))
AND ((:poiStore IS NULL) OR EXISTS (
    SELECT 1 FROM point_of_interest 
    WHERE point_of_interest.propertyId = property.id 
    AND point_of_interest.poi IN (:poiStore)
))
AND ((:poiGreenSpaces IS NULL) OR EXISTS (
    SELECT 1 FROM point_of_interest 
    WHERE point_of_interest.propertyId = property.id 
    AND point_of_interest.poi IN (:poiGreenSpaces)
))
"""
    )

    fun getSearchedPropertiesWithPOIs(
        type: String?,
        minPrice: Int?,
        maxPrice: Int?,
        minArea: Int?,
        maxArea: Int?,
        isSold: Boolean?,
        timeFilter: String?,
        poiSchool: String?,
        poiRestaurant: String?,
        poiPublicTransport: String?,
        poiHospital: String?,
        poiStore: String?,
        poiGreenSpaces: String?,
    ): Flow<List<PropertyWithPhotosAndPoisEntity>>

    @Transaction
    @Query("SELECT * FROM property WHERE id == :propertyId")
    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPoisEntity>

    @Query("SELECT * FROM property WHERE isSynced == 0")
    fun getUnsyncedProperties(): List<PropertyEntity>

    @Update
    suspend fun update(propertyEntity: PropertyEntity): Int

    @Query("UPDATE property SET isSynced = 1 WHERE id == :propertyId")
    suspend fun markAsSynced(propertyId: Long)

}
