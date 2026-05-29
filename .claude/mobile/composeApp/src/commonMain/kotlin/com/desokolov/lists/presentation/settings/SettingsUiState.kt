package com.desokolov.lists.presentation.settings

import com.desokolov.lists.ui.theme.ThemeType

data class SettingsUiState(
    val name: String = "",
    val theme: ThemeType = ThemeType.WHITE,
    val notificationsEnabled: Boolean = true
)
