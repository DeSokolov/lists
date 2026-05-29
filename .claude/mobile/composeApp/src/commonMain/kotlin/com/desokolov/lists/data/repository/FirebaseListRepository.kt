package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.model.ListItem
import com.desokolov.lists.domain.model.ListType
import com.desokolov.lists.domain.model.ShoppingList
import com.desokolov.lists.domain.repository.ListRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

class FirebaseListRepository(private val firestore: FirebaseFirestore) : ListRepository {

    private val listsRef = firestore.collection("lists")

    override fun getLists(userId: String): Flow<List<ShoppingList>> =
        listsRef
            .where { "memberIds" contains userId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc -> doc.data<ListDto>().toDomain(doc.id) }
            }

    override suspend fun createList(list: ShoppingList): Result<String> = runCatching {
        val dto = list.toDto().copy(
            memberIds = (list.memberIds + list.ownerId).distinct()
        )
        listsRef.add(dto).id
    }

    override suspend fun deleteList(id: String): Result<Unit> = runCatching {
        listsRef.document(id).delete()
    }

    override fun getItems(listId: String): Flow<List<ListItem>> =
        listsRef.document(listId).collection("items")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc -> doc.data<ItemDto>().toDomain(doc.id, listId) }
            }

    override suspend fun addItem(item: ListItem): Result<String> = runCatching {
        listsRef.document(item.listId).collection("items").add(item.toDto()).id
    }

    override suspend fun updateItem(item: ListItem): Result<Unit> = runCatching {
        listsRef.document(item.listId).collection("items").document(item.id).set(item.toDto())
    }

    override suspend fun deleteItem(listId: String, itemId: String): Result<Unit> = runCatching {
        listsRef.document(listId).collection("items").document(itemId).delete()
    }

    // DTO — used only in this file for Firestore serialization

    @Serializable
    private data class ListDto(
        val title: String = "",
        val ownerId: String = "",
        val memberIds: List<String> = emptyList(),
        val type: String = "GENERAL",
        val createdAt: Long = 0L
    )

    @Serializable
    private data class ItemDto(
        val title: String = "",
        val quantity: String? = null,
        val claimedBy: String? = null,
        val claimedByName: String? = null,
        val isDone: Boolean = false,
        val createdAt: Long = 0L
    )

    private fun ListDto.toDomain(id: String) = ShoppingList(
        id = id,
        title = title,
        ownerId = ownerId,
        memberIds = memberIds,
        type = runCatching { ListType.valueOf(type) }.getOrDefault(ListType.GENERAL),
        createdAt = createdAt
    )

    private fun ShoppingList.toDto() = ListDto(
        title = title,
        ownerId = ownerId,
        memberIds = memberIds,
        type = type.name,
        createdAt = createdAt
    )

    private fun ItemDto.toDomain(id: String, listId: String) = ListItem(
        id = id,
        listId = listId,
        title = title,
        quantity = quantity,
        claimedBy = claimedBy,
        claimedByName = claimedByName,
        isDone = isDone,
        createdAt = createdAt
    )

    private fun ListItem.toDto() = ItemDto(
        title = title,
        quantity = quantity,
        claimedBy = claimedBy,
        claimedByName = claimedByName,
        isDone = isDone,
        createdAt = createdAt
    )
}
