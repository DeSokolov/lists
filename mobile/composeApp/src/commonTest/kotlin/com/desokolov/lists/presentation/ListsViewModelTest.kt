package com.desokolov.lists.presentation

import app.cash.turbine.test
import com.desokolov.lists.domain.model.ShoppingList
import com.desokolov.lists.domain.repository.ListRepository
import com.desokolov.lists.domain.usecase.CreateListUseCase
import com.desokolov.lists.domain.usecase.GetListsUseCase
import com.desokolov.lists.presentation.lists.ListsEvent
import com.desokolov.lists.presentation.lists.ListsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ListsViewModelTest {

    private val listsFlow = MutableSharedFlow<List<ShoppingList>>()
    private val fakeRepository = object : ListRepository {
        override fun getLists(userId: String): Flow<List<ShoppingList>> = listsFlow
        override suspend fun createList(list: ShoppingList): Result<String> = Result.success("id-1")
        override suspend fun deleteList(id: String): Result<Unit> = Result.success(Unit)
        override fun getItems(listId: String) = throw NotImplementedError()
        override suspend fun addItem(item: com.desokolov.lists.domain.model.ListItem) = throw NotImplementedError()
        override suspend fun updateItem(item: com.desokolov.lists.domain.model.ListItem) = throw NotImplementedError()
        override suspend fun deleteItem(listId: String, itemId: String) = throw NotImplementedError()
    }

    private val viewModel = ListsViewModel(
        getLists = GetListsUseCase(fakeRepository),
        createList = CreateListUseCase(fakeRepository),
        userId = "user-1"
    )

    @Test
    fun `emits lists from repository`() = runTest {
        val testList = ShoppingList(id = "1", title = "Покупки", ownerId = "user-1")

        viewModel.uiState.test {
            awaitItem() // initial loading state

            listsFlow.emit(listOf(testList))
            val state = awaitItem()

            assertEquals(listOf(testList), state.lists)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `error is cleared on ErrorDismissed event`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.onEvent(ListsEvent.ErrorDismissed)
            val state = awaitItem()
            assertNull(state.error)
        }
    }
}
