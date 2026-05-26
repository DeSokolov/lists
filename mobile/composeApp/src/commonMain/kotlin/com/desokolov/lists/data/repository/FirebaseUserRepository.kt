package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.model.User
import com.desokolov.lists.domain.repository.UserRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseUserRepository(private val auth: FirebaseAuth) : UserRepository {

    override fun currentUser(): Flow<User?> =
        auth.authStateChanged.map { firebaseUser ->
            firebaseUser?.let {
                User(
                    id = it.uid,
                    displayName = it.displayName ?: it.email ?: it.uid,
                    email = it.email,
                    photoUrl = it.photoURL
                )
            }
        }

    override suspend fun signInWithGoogle(idToken: String): Result<User> = runCatching {
        val credential = dev.gitlive.firebase.auth.GoogleAuthProvider.credential(idToken, null)
        val result = auth.signInWithCredential(credential)
        val user = result.user ?: error("Sign-in returned no user")
        User(
            id = user.uid,
            displayName = user.displayName ?: user.email ?: user.uid,
            email = user.email,
            photoUrl = user.photoURL
        )
    }

    override suspend fun signInWithPhone(credential: Any): Result<User> = runCatching {
        @Suppress("UNCHECKED_CAST")
        val firebaseCredential = credential as dev.gitlive.firebase.auth.AuthCredential
        val result = auth.signInWithCredential(firebaseCredential)
        val user = result.user ?: error("Sign-in returned no user")
        User(
            id = user.uid,
            displayName = user.displayName ?: user.phoneNumber ?: user.uid,
            email = user.email,
            photoUrl = user.photoURL
        )
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
    }
}
