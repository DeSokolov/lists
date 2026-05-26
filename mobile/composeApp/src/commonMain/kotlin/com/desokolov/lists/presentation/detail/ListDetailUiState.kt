package com.desokolov.lists.presentation.detail

import com.desokolov.lists.domain.model.ListItem
import com.desokolov.lists.domain.model.ShoppingList

data class ListDetailUiState(
    val list: ShoppingList? = null,
    val items: List<ListItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterMine: Boolean = false,
    val snackbarMessage: String? = null,
    val notificationsEnabled: Boolean = true
)

sealed interface ListDetailEvent {
    data class AddItem(val title: String, val quantity: String? = null) : ListDetailEvent
    data class ClaimItem(val item: ListItem) : ListDetailEvent
    data class UnclaimItem(val item: ListItem) : ListDetailEvent
    data class CheckItem(val item: ListItem, val isDone: Boolean) : ListDetailEvent
    data class DeleteItem(val item: ListItem) : ListDetailEvent
    data object ToggleFilter : ListDetailEvent
    data object ToggleNotifications : ListDetailEvent
    data object SnackbarDismissed : ListDetailEvent
    data object ErrorDismissed : ListDetailEvent
}
