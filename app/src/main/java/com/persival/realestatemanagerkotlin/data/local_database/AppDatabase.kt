package com.persival.realestatemanagerkotlin.data.local_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao

@Database(entities = [Property::class, Photo::class, PointOfInterest::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun photoDao(): PhotoDao
    abstract fun pointOfInterestDao(): PointOfInterestDao
}
