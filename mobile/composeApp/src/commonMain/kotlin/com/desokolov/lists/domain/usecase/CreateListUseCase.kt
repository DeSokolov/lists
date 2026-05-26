package com.desokolov.lists.domain.usecase

import com.desokolov.lists.domain.model.ShoppingList
import com.desokolov.lists.domain.repository.ListRepository

class CreateListUseCase(private val repository: ListRepository) {
    suspend operator fun invoke(list: ShoppingList): Result<String> =
        repository.createList(list)
}
