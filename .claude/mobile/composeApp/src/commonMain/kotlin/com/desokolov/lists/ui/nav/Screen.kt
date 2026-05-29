package com.desokolov.lists.ui.nav

import com.desokolov.lists.domain.model.ListType

sealed interface Screen {
    data object Lists : Screen
    data object Settings : Screen
    data class ListDetail(
        val listId: String,
        val listTitle: String,
        val listType: ListType,
        val userId: String,
        val userName: String
    ) : Screen
}
