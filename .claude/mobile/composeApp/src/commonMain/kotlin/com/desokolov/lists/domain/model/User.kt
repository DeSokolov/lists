package com.desokolov.lists.domain.model

data class User(
    val id: String,
    val displayName: String,
    val email: String? = null,
    val photoUrl: String? = null
)
