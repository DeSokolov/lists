package com.desokolov.lists.domain.usecase

import com.desokolov.lists.domain.model.ListItem
import com.desokolov.lists.domain.repository.ListRepository

class CheckItemUseCase(private val repository: ListRepository) {
    suspend operator fun invoke(item: ListItem, isDone: Boolean): Result<Unit> =
        repository.updateItem(item.copy(isDone = isDone))
}
