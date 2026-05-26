package com.desokolov.lists

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.desokolov.lists.presentation.auth.AuthViewModel
import com.desokolov.lists.ui.auth.AuthScreen
import com.desokolov.lists.ui.detail.ListDetailScreen
import com.desokolov.lists.ui.lists.ListsScreen
import com.desokolov.lists.domain.model.ListType
import com.desokolov.lists.ui.nav.Screen
import com.desokolov.lists.ui.theme.ListsTheme
import org.koin.compose.koinInject
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun App() {
    ListsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val authViewModel: AuthViewModel = koinInject()
            val authState by authViewModel.uiState.collectAsState()

            var screen by remember { mutableStateOf<Screen>(Screen.Lists) }

            when {
                authState.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                authState.user == null -> AuthScreen(onEvent = authViewModel::onEvent)

                else -> {
                    val user = authState.user!!
                    when (val s = screen) {
                        is Screen.Lists -> ListsScreen(
                            userId = user.id,
                            userName = user.displayName,
                            onListClick = { listId, listTitle, listType ->
                                screen = Screen.ListDetail(
                                    listId = listId,
                                    listTitle = listTitle,
                                    listType = listType,
                                    userId = user.id,
                                    userName = user.displayName
                                )
                            }
                        )
                        is Screen.ListDetail -> ListDetailScreen(
                            listId = s.listId,
                            listTitle = s.listTitle,
                            listType = s.listType,
                            userId = s.userId,
                            userName = s.userName,
                            onBack = { screen = Screen.Lists }
                        )
                    }
                }
            }
        }
    }
}
