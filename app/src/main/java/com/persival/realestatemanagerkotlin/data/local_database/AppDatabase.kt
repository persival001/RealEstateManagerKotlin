package com.persival.realestatemanagerkotlin.data.local_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDao
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyEntity

@Database(
    entities = [
        PropertyEntity::class,
        PointOfInterestEntity::class,
        PhotoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun photoDao(): PhotoDao
    abstract fun pointOfInterestDao(): PointOfInterestDao

}




