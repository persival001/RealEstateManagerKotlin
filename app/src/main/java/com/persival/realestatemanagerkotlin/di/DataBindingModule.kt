package com.persival.realestatemanagerkotlin.di

import com.persival.realestatemanagerkotlin.data.local_database.LocalDatabaseRepository
import com.persival.realestatemanagerkotlin.data.location.LocationDataRepository
import com.persival.realestatemanagerkotlin.data.permissions.PermissionDataRepository
import com.persival.realestatemanagerkotlin.data.property.PropertyDataRepository
import com.persival.realestatemanagerkotlin.data.remote_database.firebase.FirebaseDataRepository
import com.persival.realestatemanagerkotlin.data.search.SearchDataRepository
import com.persival.realestatemanagerkotlin.data.shared_prefs.SharedPrefsRepository
import com.persival.realestatemanagerkotlin.data.synchronize_database.DataSyncRepository
import com.persival.realestatemanagerkotlin.domain.conversion.SharedPreferencesRepository
import com.persival.realestatemanagerkotlin.domain.database.SyncRepository
import com.persival.realestatemanagerkotlin.domain.location.LocationRepository
import com.persival.realestatemanagerkotlin.domain.permissions.PermissionRepository
import com.persival.realestatemanagerkotlin.domain.property.PropertyRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.search.SearchRepository
import com.persival.realestatemanagerkotlin.domain.user.FirebaseRepository
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

    @Singleton
    @Binds
    abstract fun bindFirebaseRepository(firebaseDataRepository: FirebaseDataRepository): FirebaseRepository

    @Singleton
    @Binds
    abstract fun bindPermissionRepository(permissionDataRepository: PermissionDataRepository): PermissionRepository

    @Singleton
    @Binds
    abstract fun bindLocalRepository(localDatabaseRepository: LocalDatabaseRepository): LocalRepository

    @Singleton
    @Binds
    abstract fun bindPropertyRepository(propertyDataRepository: PropertyDataRepository): PropertyRepository

    @Singleton
    @Binds
    abstract fun bindSyncRepository(dataSyncRepository: DataSyncRepository): SyncRepository

    @Singleton
    @Binds
    abstract fun bindSharedPreferencesRepository(sharedPrefsRepository: SharedPrefsRepository): SharedPreferencesRepository

    @Singleton
    @Binds
    abstract fun bindSearchRepository(searchDataRepository: SearchDataRepository): SearchRepository
}