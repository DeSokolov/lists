package com.desokolov.lists.ui.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.desokolov.lists.domain.model.ListType
import com.desokolov.lists.presentation.lists.ListsEvent
import com.desokolov.lists.presentation.lists.ListsViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    userId: String,
    userName: String,
    onListClick: (listId: String, listTitle: String, listType: ListType) -> Unit
) {
    val viewModel: ListsViewModel = koinInject { parametersOf(userId) }
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddListDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title ->
                viewModel.onEvent(ListsEvent.CreateList(title))
                showAddDialog = false
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Мои списки") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Создать список")
            }
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            else -> LazyColumn(contentPadding = padding) {
                items(state.lists, key = { it.id }) { list ->
                    ListCard(
                        list = list,
                        onClick = { onListClick(list.id, list.title, list.type) }
                    )
                }
            }
        }
    }
}
