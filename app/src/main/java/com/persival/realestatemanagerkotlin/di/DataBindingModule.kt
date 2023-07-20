package com.persival.realestatemanagerkotlin.di

import com.persival.realestatemanagerkotlin.data.location.LocationDataRepository
import com.persival.realestatemanagerkotlin.domain.location.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {

@Singleton
    @Binds
    abstract fun bindLocationRepository(locationDataRepository: LocationDataRepository): LocationRepository
}