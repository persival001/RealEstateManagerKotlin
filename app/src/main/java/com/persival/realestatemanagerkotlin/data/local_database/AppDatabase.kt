package com.persival.realestatemanagerkotlin.data.local_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDto
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDto
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDao
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDto

@Database(
    entities = [
        PropertyDto::class,
        PointOfInterestDto::class,
        PhotoDto::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun photoDao(): PhotoDao
    abstract fun pointOfInterestDao(): PointOfInterestDao

    companion object {
        private const val DATABASE_NAME = "RealEstateManager_database"

        /*fun create(
            application: Application,
            workManager: WorkManager,
            gson: Gson,
        ): AppDatabase {
            val builder = Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                DATABASE_NAME
            )

            builder.addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    val propertiesAsJson = gson.toJson(PropertyDto)
                    val photosAsJson = gson.toJson(PhotoDto)
                    val pointOfInterestAsJson = gson.toJson(PointOfInterestDto)

                    workManager.enqueue(
                        OneTimeWorkRequestBuilder<InitializeDatabaseWorker>()
                            .setInputData(
                                workDataOf(
                                    InitializeDatabaseWorker.KEY_WORKER_INIT_DB_PROPERTIES to propertiesAsJson,
                                    InitializeDatabaseWorker.KEY_WORKER_INIT_DB_PHOTOS to photosAsJson,
                                    InitializeDatabaseWorker.KEY_WORKER_INIT_DB_POINTS_OF_INTEREST to pointOfInterestAsJson,
                                )
                            )
                            .build()
                    )
                }
            })

            return builder.build()
        }*/
    }
}




