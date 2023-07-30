package com.persival.realestatemanagerkotlin.di

import android.content.Context
import androidx.room.Room
import com.persival.realestatemanagerkotlin.data.local_database.AppDatabase
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.data.location.LocationDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideLocationDataRepository(@ApplicationContext app: Context): LocationDataRepository {
        return LocationDataRepository(app)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-room"
        ).build()
    }

    @Provides
    fun providePropertyDao(database: AppDatabase): PropertyDao {
        return database.propertyDao()
    }

    @Provides
    fun providePhotoDao(database: AppDatabase): PhotoDao {
        return database.photoDao()
    }

    @Provides
    fun providePointOfInterestDao(database: AppDatabase): PointOfInterestDao {
        return database.pointOfInterestDao()
    }

}