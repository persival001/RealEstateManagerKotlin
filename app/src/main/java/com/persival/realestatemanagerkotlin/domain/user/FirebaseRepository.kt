package com.persival.realestatemanagerkotlin.domain.user

interface FirebaseRepository {
    fun getLoggedUser(): LoggedUserEntity
}