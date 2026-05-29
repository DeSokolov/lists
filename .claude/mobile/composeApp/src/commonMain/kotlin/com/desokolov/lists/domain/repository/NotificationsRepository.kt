package com.desokolov.lists.domain.repository

interface NotificationsRepository {
    fun isEnabled(): Boolean
    fun setEnabled(value: Boolean)
}
