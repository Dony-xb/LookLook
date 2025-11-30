package com.looklook.core.repository

import com.looklook.core.datastore.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend fun requestCode(phone: String) {}

    suspend fun verify(phone: String, code: String): Boolean {
        sessionManager.setToken("mock-token")
        return true
    }

    fun isLoggedIn(): Flow<Boolean> = flow { emit(sessionManager.getToken() != null) }
}

