package com.desokolov.lists.domain.repository

import com.desokolov.lists.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun currentUser(): Flow<User?>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithPhone(credential: Any): Result<User>
    suspend fun signOut(): Result<Unit>
}
