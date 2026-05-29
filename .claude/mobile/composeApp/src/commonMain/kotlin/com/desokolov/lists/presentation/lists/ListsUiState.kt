package com.desokolov.lists.presentation.lists

import com.desokolov.lists.domain.model.ListType
import com.desokolov.lists.domain.model.ShoppingList

data class ListsUiState(
    val lists: List<ShoppingList> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ListsEvent {
    data class CreateList(val title: String, val listType: ListType) : ListsEvent
    data class DeleteList(val id: String) : ListsEvent
    data object ErrorDismissed : ListsEvent
}
