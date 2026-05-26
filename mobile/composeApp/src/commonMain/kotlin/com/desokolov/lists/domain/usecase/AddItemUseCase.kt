package com.desokolov.lists.domain.usecase

import com.desokolov.lists.domain.model.ListItem
import com.desokolov.lists.domain.repository.ListRepository

class AddItemUseCase(private val repository: ListRepository) {
    suspend operator fun invoke(listId: String, title: String, quantity: String?): Result<String> =
        repository.addItem(
            ListItem(listId = listId, title = title, quantity = quantity?.ifBlank { null })
        )
}
