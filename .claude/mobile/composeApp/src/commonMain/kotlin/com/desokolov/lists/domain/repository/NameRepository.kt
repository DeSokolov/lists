package com.desokolov.lists.domain.repository

interface NameRepository {
    fun getName(): String
    fun saveName(name: String)
}
