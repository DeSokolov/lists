package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.repository.ThemeRepository
import com.desokolov.lists.ui.theme.ThemeType
import com.russhwolf.settings.Settings

class SettingsThemeRepository(private val settings: Settings) : ThemeRepository {

    override fun getTheme(): ThemeType =
        ThemeType.entries
            .find { it.name == settings.getStringOrNull(KEY) }
            ?: ThemeType.WHITE

    override fun saveTheme(theme: ThemeType) =
        settings.putString(KEY, theme.name)

    companion object { private const val KEY = "selected_theme" }
}
