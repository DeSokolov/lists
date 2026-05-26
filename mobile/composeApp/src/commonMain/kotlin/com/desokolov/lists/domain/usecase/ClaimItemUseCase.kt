package com.desokolov.lists.domain.usecase

import com.desokolov.lists.domain.model.ListItem
import com.desokolov.lists.domain.repository.ListRepository

class ClaimItemUseCase(private val repository: ListRepository) {
    suspend operator fun invoke(item: ListItem, userId: String, displayName: String): Result<Unit> =
        repository.updateItem(
            item.copy(claimedBy = userId, claimedByName = displayName)
        )
}
