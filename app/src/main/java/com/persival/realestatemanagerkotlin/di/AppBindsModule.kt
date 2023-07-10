package com.persival.realestatemanagerkotlin.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule { /*@Singleton
    @Binds
    public abstract LoggedUserRepository bindLoggedUserRepository(FirebaseRepository implementation);*/
}