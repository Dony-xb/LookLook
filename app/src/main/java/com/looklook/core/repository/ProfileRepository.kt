package com.looklook.core.repository

import com.looklook.core.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepository @Inject constructor() {
    fun getMyProfile(): Flow<UserProfile> = flow {
        emit(
            UserProfile(
                id = "me",
                name = "LookLook用户",
                avatarUrl = "https://picsum.photos/seed/me/200/200",
                bio = "分享生活与创作"
            )
        )
    }
}

