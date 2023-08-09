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

@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(propertyEntity: PropertyEntity): Long

    @Update
    fun update(propertyEntity: PropertyEntity): Int

    @Delete
    fun delete(propertyEntity: PropertyEntity): Int

    @Query("SELECT * FROM property")
    fun getAllProperties(): Cursor

    @Query("SELECT * FROM property WHERE id = :propertyId")
    @Transaction
    fun getPropertyById(propertyId: Long): Cursor

    @Query("DELETE FROM property WHERE id = :propertyId")
    fun deleteBySelection(propertyId: Long): Int

    @Update
    fun updateBySelection(property: PropertyEntity): Int

    @Query("SELECT * FROM property WHERE id = :propertyId")
    fun getById(propertyId: Long): PropertyEntity?

}
