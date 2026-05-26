package com.desokolov.lists.domain.usecase

import com.desokolov.lists.domain.repository.ListRepository

class DeleteItemUseCase(private val repository: ListRepository) {
    suspend operator fun invoke(listId: String, itemId: String): Result<Unit> =
        repository.deleteItem(listId, itemId)
}
