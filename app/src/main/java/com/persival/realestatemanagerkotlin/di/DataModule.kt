package com.persival.realestatemanagerkotlin.di

import android.app.Application
import android.content.Context
import androidx.work.WorkManager
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

}