package com.persival.realestatemanagerkotlin.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity

@Dao
interface PropertyDao {

    @Insert
    suspend fun insert(propertyEntity: PropertyEntity): Long

    @Update
    suspend fun update(propertyEntity: PropertyEntity)

    @Delete
    suspend fun delete(propertyEntity: PropertyEntity)

    @Query("SELECT * FROM property")
    suspend fun getAllProperties(): List<PropertyEntity>

    @Query("SELECT * FROM property WHERE id = :propertyId")
    suspend fun getPropertyById(propertyId: Long): PropertyEntity
}