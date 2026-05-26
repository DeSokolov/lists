package com.desokolov.lists.ui.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.desokolov.lists.domain.model.ListType
import com.desokolov.lists.presentation.detail.ListDetailEvent
import com.desokolov.lists.presentation.detail.ListDetailViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: String,
    listTitle: String,
    listType: ListType,
    userId: String,
    userName: String,
    onBack: () -> Unit
) {
    val viewModel: ListDetailViewModel = koinInject { parametersOf(listId, userId, userName) }
    val state by viewModel.uiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showParticipants by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var searchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(searchActive) {
        if (searchActive) searchFocusRequester.requestFocus()
    }

    LaunchedEffect(state.snackbarMessage) {
        val msg = state.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.onEvent(ListDetailEvent.SnackbarDismissed)
    }

    val participants by remember(state.items) {
        derivedStateOf {
            state.items
                .filter { it.claimedBy != null && it.claimedByName != null }
                .distinctBy { it.claimedBy }
                .map { it.claimedByName!! }
        }
    }

    val displayItems by remember(state.items, state.filterMine, searchQuery) {
        derivedStateOf {
            state.items
                .let { list -> if (state.filterMine) list.filter { it.claimedBy == userId } else list }
                .let { list -> if (searchQuery.isNotBlank()) list.filter { it.title.contains(searchQuery, ignoreCase = true) } else list }
        }
    }

    if (showAddDialog) {
        AddItemDialog(
            listType = listType,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, quantity ->
                viewModel.onEvent(ListDetailEvent.AddItem(title, quantity))
                showAddDialog = false
            }
        )
    }

    if (showParticipants) {
        ModalBottomSheet(
            onDismissRequest = { showParticipants = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Text(
                text = "Участники",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (participants.isEmpty()) {
                Text(
                    text = "Никто ещё ничего не взял",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                participants.forEach { name ->
                    ListItem(headlineContent = { Text(name) })
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (searchActive) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(searchFocusRequester),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        "Поиск...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                inner()
                            }
                        )
                    } else {
                        Text(listTitle)
                    }
                },
                navigationIcon = {
                    if (searchActive) {
                        IconButton(onClick = {
                            searchActive = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Закрыть поиск")
                        }
                    } else {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                },
                actions = {
                    if (!searchActive) {
                        IconButton(onClick = { searchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Поиск")
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString("lists://join/$listId"))
                            scope.launch { snackbarHostState.showSnackbar("Ссылка скопирована") }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Поделиться")
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Участники") },
                                    onClick = {
                                        showMenu = false
                                        showParticipants = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (state.notificationsEnabled) "Уведомления: вкл"
                                            else "Уведомления: выкл"
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        viewModel.onEvent(ListDetailEvent.ToggleNotifications)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить пункт")
            }
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            else -> Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .padding(top = padding.calculateTopPadding())
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    FilterChip(
                        selected = !state.filterMine,
                        onClick = { if (state.filterMine) viewModel.onEvent(ListDetailEvent.ToggleFilter) },
                        label = { Text("Все") }
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        selected = state.filterMine,
                        onClick = { if (!state.filterMine) viewModel.onEvent(ListDetailEvent.ToggleFilter) },
                        label = { Text("Мои") }
                    )
                }

                if (displayItems.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            when {
                                searchQuery.isNotBlank() -> "Ничего не найдено"
                                state.filterMine -> "Ты ничего не взял"
                                else -> "Список пуст — добавь первый пункт"
                            }
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = padding.calculateBottomPadding())
                    ) {
                        items(displayItems, key = { it.id }) { item ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        viewModel.onEvent(ListDetailEvent.DeleteItem(item))
                                    }
                                    true
                                }
                            )
                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    val color by animateColorAsState(
                                        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                            MaterialTheme.colorScheme.errorContainer
                                        else Color.Transparent
                                    )
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(end = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Удалить",
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            ) {
                                Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
                                    ItemRow(
                                        item = item,
                                        currentUserId = userId,
                                        onClaim = { viewModel.onEvent(ListDetailEvent.ClaimItem(item)) },
                                        onUnclaim = { viewModel.onEvent(ListDetailEvent.UnclaimItem(item)) },
                                        onCheck = { isDone -> viewModel.onEvent(ListDetailEvent.CheckItem(item, isDone)) }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
