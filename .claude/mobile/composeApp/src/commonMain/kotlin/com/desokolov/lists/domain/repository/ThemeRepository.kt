package com.desokolov.lists.domain.repository

import com.desokolov.lists.ui.theme.ThemeType

interface ThemeRepository {
    fun getTheme(): ThemeType
    fun saveTheme(theme: ThemeType)
}
