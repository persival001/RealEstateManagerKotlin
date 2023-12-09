package com.persival.realestatemanagerkotlin.domain.user

import com.google.firebase.auth.FirebaseUser
import com.persival.realestatemanagerkotlin.domain.user.model.RealEstateAgent

interface FirebaseRepository {

    fun getCurrentUser(): FirebaseUser?

    fun getRealEstateAgentIdentity(): RealEstateAgent?

}
