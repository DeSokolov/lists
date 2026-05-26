package com.desokolov.lists.domain.usecase

import com.desokolov.lists.domain.model.ListItem
import com.desokolov.lists.domain.repository.ListRepository

class UnclaimItemUseCase(private val repository: ListRepository) {
    suspend operator fun invoke(item: ListItem): Result<Unit> =
        repository.updateItem(item.copy(claimedBy = null, claimedByName = null))
}
