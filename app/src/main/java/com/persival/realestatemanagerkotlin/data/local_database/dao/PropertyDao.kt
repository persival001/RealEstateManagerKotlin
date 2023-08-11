package com.persival.realestatemanagerkotlin.data.local_database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    // Local database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(propertyEntity: PropertyEntity): Long

    @Update
    suspend fun update(propertyEntity: PropertyEntity)

    @Delete
    suspend fun delete(propertyEntity: PropertyEntity)

    @Query("SELECT * FROM property")
    @Transaction
    fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOIEntity>>

    @Query("SELECT * FROM property WHERE id = :propertyId")
    @Transaction
    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity>

    // Content provider
    @Query("DELETE FROM property WHERE id = :propertyId")
    fun deleteBySelection(propertyId: Long): Int

    @Update
    fun updateBySelection(property: PropertyEntity): Int

    @Query("SELECT * FROM property WHERE id = :propertyId")
    fun getById(propertyId: Long): PropertyEntity?

    @Update
    suspend fun updateAsCursor(propertyEntity: PropertyEntity): Int

    @Query("SELECT * FROM property")
    fun getAllPropertiesAsCursor(): Cursor

    @Query("SELECT * FROM property WHERE id = :propertyId")
    @Transaction
    fun getPropertyByIdAsCursor(propertyId: Long): Cursor

    @Query("SELECT latLng FROM property")
    fun getAllLatLng(): Flow<List<String>>


}
