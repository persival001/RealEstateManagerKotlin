package com.persival.realestatemanagerkotlin.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.persival.realestatemanagerkotlin.data.local_database.AppDatabase
import com.persival.realestatemanagerkotlin.data.local_database.LocalDatabaseRepository
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.data.local_database.model.PhotoDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.model.PointOfInterestDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyWithPhotosAndPoisDtoMapper
import com.persival.realestatemanagerkotlin.data.permissions.PermissionDataRepository
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

    companion object {
        private const val GOOGLE_PLACES_API_BASE_URL = "https://maps.googleapis.com/"
    }

    @Provides
    fun providePermissionDataRepository(
        @ApplicationContext app: Context
    ): PermissionDataRepository = PermissionDataRepository(app)

    @Provides
    fun provideLocalDatabaseRepository(
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
        propertyDao: PropertyDao,
        photoDao: PhotoDao,
        pointOfInterestDao: PointOfInterestDao,
        propertyMapper: PropertyDtoMapper,
        photoMapper: PhotoDtoMapper,
        poiMapper: PointOfInterestDtoMapper,
        propertyWithPhotosAndPoisDtoMapper: PropertyWithPhotosAndPoisDtoMapper,
    ): LocalDatabaseRepository = LocalDatabaseRepository(
        coroutineDispatcherProvider,
        propertyDao,
        photoDao,
        pointOfInterestDao,
        propertyMapper,
        photoMapper,
        poiMapper,
        propertyWithPhotosAndPoisDtoMapper,
    )

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(app)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "database-room"
    ).build()

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun providePropertyDao(database: AppDatabase): PropertyDao = database.propertyDao()

    @Singleton
    @Provides
    fun providePhotoDao(database: AppDatabase): PhotoDao = database.photoDao()

    @Singleton
    @Provides
    fun providePointOfInterestDao(database: AppDatabase): PointOfInterestDao = database.pointOfInterestDao()

    @Singleton
    @Provides
    fun providePropertyDtoMapper(): PropertyDtoMapper = PropertyDtoMapper()

    @Singleton
    @Provides
    fun providePhotoDtoMapper(): PhotoDtoMapper = PhotoDtoMapper()

    @Singleton
    @Provides
    fun providePointOfInterestDtoMapper(): PointOfInterestDtoMapper = PointOfInterestDtoMapper()

    @Singleton
    @Provides
    fun providePropertyWithPhotosAndPoisDtoMapper(
        propertyMapper: PropertyDtoMapper,
        photoMapper: PhotoDtoMapper,
        poiMapper: PointOfInterestDtoMapper
    ): PropertyWithPhotosAndPoisDtoMapper = PropertyWithPhotosAndPoisDtoMapper(propertyMapper, photoMapper, poiMapper)

}
