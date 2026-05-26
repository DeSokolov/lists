package com.desokolov.lists.domain.usecase

import com.desokolov.lists.domain.model.ShoppingList
import com.desokolov.lists.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow

class GetListsUseCase(private val repository: ListRepository) {
    operator fun invoke(userId: String): Flow<List<ShoppingList>> =
        repository.getLists(userId)
}
