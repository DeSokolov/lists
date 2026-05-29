package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.repository.NameRepository
import com.russhwolf.settings.Settings

class SettingsNameRepository(private val settings: Settings) : NameRepository {

    override fun getName(): String = settings.getString(KEY, "")

    override fun saveName(name: String) = settings.putString(KEY, name)

    companion object { private const val KEY = "user_name" }
}
