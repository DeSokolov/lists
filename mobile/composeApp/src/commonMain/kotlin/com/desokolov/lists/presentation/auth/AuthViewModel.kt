package com.desokolov.lists.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desokolov.lists.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(isLoading = true))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        userRepository.currentUser()
            .onEach { user -> _uiState.update { it.copy(user = user, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.SignInWithGoogle -> signInWithGoogle(event.idToken)
            AuthEvent.SignOut -> signOut()
            AuthEvent.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.signInWithGoogle(idToken)
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}
