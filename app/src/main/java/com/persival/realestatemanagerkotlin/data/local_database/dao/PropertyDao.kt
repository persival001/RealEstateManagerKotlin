package com.persival.realestatemanagerkotlin.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Insert
    suspend fun insert(propertyEntity: PropertyEntity): Long

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

}