package com.desokolov.lists.domain.usecase

import com.desokolov.lists.domain.model.ListItem
import com.desokolov.lists.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow

class GetItemsUseCase(private val repository: ListRepository) {
    operator fun invoke(listId: String): Flow<List<ListItem>> =
        repository.getItems(listId)
}
