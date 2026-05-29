package com.desokolov.lists.domain.repository

import com.desokolov.lists.domain.model.ListItem
import com.desokolov.lists.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ListRepository {
    fun getLists(userId: String): Flow<List<ShoppingList>>
    suspend fun createList(list: ShoppingList): Result<String>
    suspend fun deleteList(id: String): Result<Unit>
    fun getItems(listId: String): Flow<List<ListItem>>
    suspend fun addItem(item: ListItem): Result<String>
    suspend fun updateItem(item: ListItem): Result<Unit>
    suspend fun deleteItem(listId: String, itemId: String): Result<Unit>
}
