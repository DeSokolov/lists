package com.desokolov.lists.presentation.settings

import androidx.lifecycle.ViewModel
import com.desokolov.lists.domain.repository.NameRepository
import com.desokolov.lists.domain.repository.NotificationsRepository
import com.desokolov.lists.domain.repository.ThemeRepository
import com.desokolov.lists.ui.theme.ThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val themeRepository: ThemeRepository,
    private val nameRepository: NameRepository,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            theme = themeRepository.getTheme(),
            name = nameRepository.getName(),
            notificationsEnabled = notificationsRepository.isEnabled()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setTheme(theme: ThemeType) {
        themeRepository.saveTheme(theme)
        _uiState.update { it.copy(theme = theme) }
    }

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun saveName() {
        nameRepository.saveName(_uiState.value.name)
    }

    fun setNotifications(enabled: Boolean) {
        notificationsRepository.setEnabled(enabled)
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }
}
