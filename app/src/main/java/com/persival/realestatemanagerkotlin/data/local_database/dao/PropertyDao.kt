package com.persival.realestatemanagerkotlin.data.local_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.persival.realestatemanagerkotlin.data.local_database.Property

@Dao
interface PropertyDao {
    @Query("SELECT * FROM property")
    fun getAll(): List<Property>

    @Insert
    fun insert(property: Property): Long

    @Delete
    fun delete(property: Property)
}