package com.persival.realestatemanagerkotlin.data.remote_database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.persival.realestatemanagerkotlin.domain.user.FirebaseRepository
import com.persival.realestatemanagerkotlin.domain.user.RealEstateAgentEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseRepository {

    // ----- Get firebaseAuth user -----
    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // ----- Get real estate agent entity -----
    override fun getRealEstateAgentIdentity(): RealEstateAgentEntity {
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null && firebaseUser.displayName != null) {
            RealEstateAgentEntity(
                firebaseUser.uid,
                firebaseUser.displayName!!
            )
        } else {
            throw IllegalStateException("No logged in user or user has no display name.")
        }
    }

}

