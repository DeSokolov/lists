package com.desokolov.lists.domain.model

data class ListItem(
    val id: String = "",
    val listId: String,
    val title: String,
    val quantity: String? = null,
    val claimedBy: String? = null,
    val claimedByName: String? = null,
    val isDone: Boolean = false,
    val createdAt: Long = 0L
)
