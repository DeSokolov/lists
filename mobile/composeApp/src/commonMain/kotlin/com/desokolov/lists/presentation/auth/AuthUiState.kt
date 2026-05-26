package com.desokolov.lists.presentation.auth

import com.desokolov.lists.domain.model.User

data class AuthUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface AuthEvent {
    data class SignInWithGoogle(val idToken: String) : AuthEvent
    data object SignOut : AuthEvent
    data object ErrorDismissed : AuthEvent
}
