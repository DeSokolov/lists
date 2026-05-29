package com.desokolov.lists.presentation.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desokolov.lists.domain.model.ListType
import com.desokolov.lists.domain.model.ShoppingList
import com.desokolov.lists.domain.usecase.CreateListUseCase
import com.desokolov.lists.domain.usecase.GetListsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListsViewModel(
    private val getLists: GetListsUseCase,
    private val createList: CreateListUseCase,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListsUiState(isLoading = true))
    val uiState: StateFlow<ListsUiState> = _uiState.asStateFlow()

    init {
        getLists(userId)
            .onEach { lists -> _uiState.update { it.copy(lists = lists, isLoading = false) } }
            .catch { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ListsEvent) {
        when (event) {
            is ListsEvent.CreateList -> createList(event.title, event.listType)
            is ListsEvent.DeleteList -> { /* TODO */ }
            ListsEvent.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun createList(title: String, listType: ListType) {
        viewModelScope.launch {
            createList(ShoppingList(title = title, ownerId = userId, type = listType))
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}
