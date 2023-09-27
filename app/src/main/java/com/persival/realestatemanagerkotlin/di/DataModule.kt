package com.persival.realestatemanagerkotlin.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.persival.realestatemanagerkotlin.data.local_database.AppDatabase
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun provideWorkManager(application: Application): WorkManager =
        WorkManager.getInstance(application)

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

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

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()


    @Singleton
    @Provides
    fun providePropertyDao(database: AppDatabase): PropertyDao = database.propertyDao()

    @Singleton
    @Provides
    fun providePhotoDao(database: AppDatabase): PhotoDao = database.photoDao()

    @Singleton
    @Provides
    fun providePointOfInterestDao(database: AppDatabase): PointOfInterestDao = database.pointOfInterestDao()

}
