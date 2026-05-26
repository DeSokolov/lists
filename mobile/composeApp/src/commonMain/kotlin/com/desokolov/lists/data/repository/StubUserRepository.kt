package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.model.User
import com.desokolov.lists.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class StubUserRepository : UserRepository {

    private val _user = MutableStateFlow<User?>(
        User(id = "stub-user", displayName = "Денис", email = "dev4@swtlm.com")
    )

    override fun currentUser(): Flow<User?> = _user

    override suspend fun signInWithGoogle(idToken: String): Result<User> =
        Result.success(_user.value!!)

    override suspend fun signInWithPhone(credential: Any): Result<User> =
        Result.success(_user.value!!)

    override suspend fun signOut(): Result<Unit> {
        _user.value = null
        return Result.success(Unit)
    }
}
