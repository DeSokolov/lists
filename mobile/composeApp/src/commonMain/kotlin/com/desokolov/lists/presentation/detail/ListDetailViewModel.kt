package com.desokolov.lists.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desokolov.lists.domain.usecase.AddItemUseCase
import com.desokolov.lists.domain.usecase.CheckItemUseCase
import com.desokolov.lists.domain.usecase.ClaimItemUseCase
import com.desokolov.lists.domain.usecase.DeleteItemUseCase
import com.desokolov.lists.domain.usecase.GetItemsUseCase
import com.desokolov.lists.domain.usecase.UnclaimItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListDetailViewModel(
    private val listId: String,
    private val currentUserId: String,
    private val currentUserName: String,
    private val getItems: GetItemsUseCase,
    private val addItem: AddItemUseCase,
    private val claimItem: ClaimItemUseCase,
    private val unclaimItem: UnclaimItemUseCase,
    private val checkItem: CheckItemUseCase,
    private val deleteItem: DeleteItemUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListDetailUiState(isLoading = true))
    val uiState: StateFlow<ListDetailUiState> = _uiState.asStateFlow()

    init {
        getItems(listId)
            .onEach { items -> _uiState.update { it.copy(items = items, isLoading = false) } }
            .catch { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ListDetailEvent) {
        when (event) {
            is ListDetailEvent.AddItem -> launchItem(event.title, event.quantity)
            is ListDetailEvent.ClaimItem -> viewModelScope.launch {
                claimItem(event.item, currentUserId, currentUserName)
                    .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
            }
            is ListDetailEvent.UnclaimItem -> viewModelScope.launch {
                unclaimItem(event.item)
                    .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
            }
            is ListDetailEvent.CheckItem -> viewModelScope.launch {
                checkItem(event.item, event.isDone)
                    .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
            }
            is ListDetailEvent.DeleteItem -> viewModelScope.launch {
                deleteItem(listId, event.item.id)
                    .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
            }
            ListDetailEvent.ToggleFilter -> _uiState.update { it.copy(filterMine = !it.filterMine) }
            ListDetailEvent.ToggleNotifications -> _uiState.update { it.copy(notificationsEnabled = !it.notificationsEnabled) }
            is ListDetailEvent.SnackbarDismissed -> _uiState.update { it.copy(snackbarMessage = null) }
            ListDetailEvent.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun launchItem(title: String, quantity: String?) {
        viewModelScope.launch {
            addItem(listId, title, quantity)
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}
