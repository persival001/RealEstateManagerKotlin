package com.persival.realestatemanagerkotlin.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingModule {

/*@Singleton
    @Binds
    abstract fun bindResourcesRepository(resourcesRepositoryImplementation: ResourcesRepositoryImplementation): ResourcesRepository*/
}