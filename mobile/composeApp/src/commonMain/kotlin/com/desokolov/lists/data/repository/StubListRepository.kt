package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.model.ListItem
import com.desokolov.lists.domain.model.ListType
import com.desokolov.lists.domain.model.ShoppingList
import com.desokolov.lists.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class StubListRepository : ListRepository {

    private val lists = MutableStateFlow(
        listOf(
            ShoppingList(id = "1", title = "Продукты", ownerId = "stub-user", type = ListType.SHOPPING),
            ShoppingList(id = "2", title = "В поездку", ownerId = "stub-user", type = ListType.TRAVEL),
        )
    )

    private val items = MutableStateFlow(
        listOf(
            ListItem(id = "i1", listId = "1", title = "Молоко", quantity = "2 л"),
            ListItem(id = "i2", listId = "1", title = "Хлеб", claimedBy = "stub-user", claimedByName = "Денис"),
            ListItem(id = "i3", listId = "1", title = "Яблоки", quantity = "1 кг", claimedBy = "other-user", claimedByName = "Маша", isDone = true),
            ListItem(id = "i4", listId = "1", title = "Сок", quantity = "1 л"),
        )
    )

    override fun getLists(userId: String): Flow<List<ShoppingList>> = lists

    override suspend fun createList(list: ShoppingList): Result<String> {
        val id = "stub-${lists.value.size + 1}"
        lists.value = lists.value + list.copy(id = id)
        return Result.success(id)
    }

    override suspend fun deleteList(id: String): Result<Unit> {
        lists.value = lists.value.filter { it.id != id }
        return Result.success(Unit)
    }

    override fun getItems(listId: String): Flow<List<ListItem>> =
        items.map { all -> all.filter { it.listId == listId } }

    override suspend fun addItem(item: ListItem): Result<String> {
        val id = "stub-item-${items.value.size + 1}"
        items.value = items.value + item.copy(id = id)
        return Result.success(id)
    }

    override suspend fun updateItem(item: ListItem): Result<Unit> {
        items.value = items.value.map { if (it.id == item.id) item else it }
        return Result.success(Unit)
    }

    override suspend fun deleteItem(listId: String, itemId: String): Result<Unit> {
        items.value = items.value.filter { it.id != itemId }
        return Result.success(Unit)
    }
}
