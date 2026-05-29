package com.desokolov.lists.domain.model

data class ShoppingList(
    val id: String = "",
    val title: String,
    val ownerId: String,
    val memberIds: List<String> = emptyList(),
    val type: ListType = ListType.GENERAL,
    val createdAt: Long = 0L
)

enum class ListType {
    SHOPPING,
    TRAVEL,
    EVENT,
    MOVING,
    GENERAL
}
