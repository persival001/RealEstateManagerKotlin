package com.persival.realestatemanagerkotlin.data.remote_database

import com.google.firebase.auth.FirebaseAuth
import com.persival.realestatemanagerkotlin.domain.user.LoggedUserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    // ----- Get a logged user -----
    fun getLoggedUser(): LoggedUserEntity? {
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null && firebaseUser.displayName != null) {
            LoggedUserEntity(
                firebaseUser.uid,
                firebaseUser.displayName!!
            )
        } else {
            // TODO: Verify the internet connection
            null
        }
    }

}
