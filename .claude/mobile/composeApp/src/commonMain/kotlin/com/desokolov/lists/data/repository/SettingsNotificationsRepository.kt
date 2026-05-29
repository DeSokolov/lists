package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.repository.NotificationsRepository
import com.russhwolf.settings.Settings

class SettingsNotificationsRepository(private val settings: Settings) : NotificationsRepository {

    override fun isEnabled(): Boolean = settings.getBoolean(KEY, true)

    override fun setEnabled(value: Boolean) = settings.putBoolean(KEY, value)

    companion object { private const val KEY = "notifications_enabled" }
}
