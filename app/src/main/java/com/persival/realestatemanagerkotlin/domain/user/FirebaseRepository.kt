package com.persival.realestatemanagerkotlin.domain.user

import com.google.firebase.auth.FirebaseUser

interface FirebaseRepository {

    fun getCurrentUser(): FirebaseUser?

    fun getRealEstateAgentIdentity(): RealEstateAgentEntity?

}
