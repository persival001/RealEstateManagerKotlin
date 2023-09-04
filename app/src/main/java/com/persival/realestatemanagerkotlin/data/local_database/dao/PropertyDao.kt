package com.persival.realestatemanagerkotlin.data.local_database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyDto
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyWithPhotosAndPoisDto
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Insert
    fun insert(propertyDto: PropertyDto): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(propertyDto: List<PropertyDto>)

    @Query("SELECT * FROM property WHERE id == :propertyId")
    fun getById(propertyId: Long): PropertyDto?

    @Query("SELECT * FROM property")
    fun getAllPropertiesAsCursor(): Cursor

    @Query("SELECT * FROM property WHERE id == :propertyId")
    fun getPropertyByIdAsCursor(propertyId: Long): Cursor

    @Query("SELECT latLng FROM property")
    fun getAllLatLng(): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM property")
    fun getAllProperties(): Flow<List<PropertyWithPhotosAndPoisDto>>

    @Transaction
    @Query("SELECT * FROM property WHERE id == :propertyId")
    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPoisDto>

    @Query("SELECT * FROM property WHERE isSynced == 0")
    fun getUnsyncedProperties(): List<PropertyDto>

    @Update
    suspend fun update(propertyDto: PropertyDto): Int

    @Update
    fun updateBySelection(propertyDto: PropertyDto): Int

    @Query("UPDATE property SET isSynced = 1 WHERE id == :propertyId")
    suspend fun markAsSynced(propertyId: Long)

    @Delete
    suspend fun delete(propertyDto: PropertyDto)

    @Query("DELETE FROM property WHERE id == :propertyId")
    fun deleteBySelection(propertyId: Long): Int
}
