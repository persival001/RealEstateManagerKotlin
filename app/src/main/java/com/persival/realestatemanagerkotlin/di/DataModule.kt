package com.persival.realestatemanagerkotlin.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.persival.realestatemanagerkotlin.data.local_database.AppDatabase
import com.persival.realestatemanagerkotlin.data.local_database.LocalDatabaseRepository
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.data.location.LocationDataRepository
import com.persival.realestatemanagerkotlin.data.permissions.PermissionDataRepository
import com.persival.realestatemanagerkotlin.data.remote_database.FirebaseDataRepository
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    private val GOOGLE_PLACES_API_BASE_URL = "https://maps.googleapis.com/"

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    fun provideLocationDataRepository(
        application: Application
    ): LocationDataRepository {
        return LocationDataRepository(application)
    }

    @Provides
    fun provideFirebaseDataRepository(
        firebaseAuth: FirebaseAuth
    ): FirebaseDataRepository {
        return FirebaseDataRepository(firebaseAuth)
    }

    @Provides
    fun providePermissionDataRepository(
        @ApplicationContext app: Context
    ): PermissionDataRepository {
        return PermissionDataRepository(app)
    }
    
    @Provides
    fun provideLocalDatabaseRepository(
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
        propertyDao: PropertyDao,
        photoDao: PhotoDao,
        pointOfInterestDao: PointOfInterestDao,
    ): LocalDatabaseRepository {
        return LocalDatabaseRepository(
            coroutineDispatcherProvider,
            propertyDao,
            photoDao,
            pointOfInterestDao
        )
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-room"
        ).build()
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun providePropertyDao(database: AppDatabase): PropertyDao {
        return database.propertyDao()
    }

    @Singleton
    @Provides
    fun providePhotoDao(database: AppDatabase): PhotoDao {
        return database.photoDao()
    }

    @Singleton
    @Provides
    fun providePointOfInterestDao(database: AppDatabase): PointOfInterestDao {
        return database.pointOfInterestDao()
    }

}
