package com.persival.realestatemanagerkotlin.data.local_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity

@Database(entities = [PropertyEntity::class, PointOfInterestEntity::class, PhotoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun pointOfInterestDao(): PointOfInterestDao
    abstract fun photoDao(): PhotoDao
}



