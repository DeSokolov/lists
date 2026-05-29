# Feature 024 — Settings Screen + Color Themes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Добавить экран Настроек (Имя / Тема / Уведомления) с тремя цветовыми темами (WHITE / BLACK / PINK), сохранением через multiplatform-settings и мгновенным применением без перезапуска.

**Architecture:** `SettingsViewModel` — `single` в Koin, единый источник истины для темы. `App.kt` инжектирует его и передаёт тему в `ListsTheme`. Настройки хранятся через `multiplatform-settings` (Russhwolf) — платформо-специфичная реализация предоставляется через `expect/actual platformSettingsModule`. `ThemeType` уже определён в `Theme.kt`, не дублируем.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, Koin 4.0.0, multiplatform-settings 1.2.0, Turbine (тесты), kotlin-test

---

## Карта файлов

| Действие | Файл |
|----------|------|
| Создать | `domain/repository/ThemeRepository.kt` |
| Создать | `domain/repository/NameRepository.kt` |
| Создать | `domain/repository/NotificationsRepository.kt` |
| Создать | `commonMain/di/PlatformSettingsModule.kt` (expect) |
| Создать | `androidMain/di/PlatformSettingsModule.kt` (actual) |
| Создать | `iosMain/di/PlatformSettingsModule.kt` (actual) |
| Создать | `data/repository/SettingsThemeRepository.kt` |
| Создать | `data/repository/SettingsNameRepository.kt` |
| Создать | `data/repository/SettingsNotificationsRepository.kt` |
| Создать | `presentation/settings/SettingsUiState.kt` |
| Создать | `presentation/settings/SettingsViewModel.kt` |
| Создать | `ui/settings/SettingsScreen.kt` |
| Создать | `commonTest/kotlin/.../presentation/SettingsViewModelTest.kt` |
| Изменить | `gradle/libs.versions.toml` |
| Изменить | `composeApp/build.gradle.kts` |
| Изменить | `ui/nav/Screen.kt` |
| Изменить | `App.kt` |
| Изменить | `ui/lists/ListsScreen.kt` |
| Изменить | `di/AppModule.kt` |

Корень commonMain: `composeApp/src/commonMain/kotlin/com/desokolov/lists/`
Корень тестов: `composeApp/src/commonTest/kotlin/com/desokolov/lists/`

---

## Task 1: Gradle — добавить multiplatform-settings

**Files:**
- Modify: `mobile/gradle/libs.versions.toml`
- Modify: `mobile/composeApp/build.gradle.kts`

- [ ] **Шаг 1: Добавить версию и библиотеку в libs.versions.toml**

В секцию `[versions]` добавить:
```toml
multiplatform-settings = "1.2.0"
```

В секцию `[libraries]` добавить:
```toml
multiplatform-settings = { module = "com.russhwolf:multiplatform-settings", version.ref = "multiplatform-settings" }
```

- [ ] **Шаг 2: Добавить зависимость в commonMain**

В `composeApp/build.gradle.kts`, внутри `sourceSets { commonMain.dependencies { ... } }`, после строки `implementation(libs.koin.compose)` добавить:
```kotlin
implementation(libs.multiplatform.settings)
```

- [ ] **Шаг 3: Проверить синхронизацию**

```bash
cd /Users/desokolov/Desktop/projects/projects_different/lists/.claude/mobile
./gradlew :composeApp:generateDummyFramework
```

Ожидаем: BUILD SUCCESSFUL (или только warnings, не ошибки)

- [ ] **Шаг 4: Commit**

```bash
git add gradle/libs.versions.toml composeApp/build.gradle.kts
git commit -m "chore(deps): add multiplatform-settings 1.2.0"
```

---

## Task 2: Domain — три интерфейса репозиториев

**Files:**
- Create: `domain/repository/ThemeRepository.kt`
- Create: `domain/repository/NameRepository.kt`
- Create: `domain/repository/NotificationsRepository.kt`

- [ ] **Шаг 1: Создать ThemeRepository.kt**

```kotlin
package com.desokolov.lists.domain.repository

import com.desokolov.lists.ui.theme.ThemeType

interface ThemeRepository {
    fun getTheme(): ThemeType
    fun saveTheme(theme: ThemeType)
}
```

- [ ] **Шаг 2: Создать NameRepository.kt**

```kotlin
package com.desokolov.lists.domain.repository

interface NameRepository {
    fun getName(): String
    fun saveName(name: String)
}
```

- [ ] **Шаг 3: Создать NotificationsRepository.kt**

```kotlin
package com.desokolov.lists.domain.repository

interface NotificationsRepository {
    fun isEnabled(): Boolean
    fun setEnabled(value: Boolean)
}
```

- [ ] **Шаг 4: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/desokolov/lists/domain/repository/
git commit -m "feat(settings): add ThemeRepository, NameRepository, NotificationsRepository interfaces"
```

---

## Task 3: TDD — написать падающие тесты для SettingsViewModel

**Files:**
- Create: `commonTest/kotlin/com/desokolov/lists/presentation/SettingsViewModelTest.kt`

- [ ] **Шаг 1: Создать файл теста**

```kotlin
package com.desokolov.lists.presentation

import app.cash.turbine.test
import com.desokolov.lists.domain.repository.NameRepository
import com.desokolov.lists.domain.repository.NotificationsRepository
import com.desokolov.lists.domain.repository.ThemeRepository
import com.desokolov.lists.presentation.settings.SettingsViewModel
import com.desokolov.lists.ui.theme.ThemeType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsViewModelTest {

    private val fakeThemeRepo = object : ThemeRepository {
        private var stored = ThemeType.WHITE
        override fun getTheme() = stored
        override fun saveTheme(t: ThemeType) { stored = t }
    }

    private val fakeNameRepo = object : NameRepository {
        private var stored = "Денис"
        override fun getName() = stored
        override fun saveName(n: String) { stored = n }
    }

    private val fakeNotifRepo = object : NotificationsRepository {
        private var stored = true
        override fun isEnabled() = stored
        override fun setEnabled(value: Boolean) { stored = value }
    }

    private val viewModel = SettingsViewModel(fakeThemeRepo, fakeNameRepo, fakeNotifRepo)

    @Test
    fun `initial state loads from repositories`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(ThemeType.WHITE, state.theme)
            assertEquals("Денис", state.name)
            assertTrue(state.notificationsEnabled)
        }
    }

    @Test
    fun `setTheme updates uiState and persists to repository`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.setTheme(ThemeType.BLACK)
            val state = awaitItem()
            assertEquals(ThemeType.BLACK, state.theme)
            assertEquals(ThemeType.BLACK, fakeThemeRepo.getTheme())
        }
    }

    @Test
    fun `setName updates uiState but does not persist until saveName`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.setName("Маша")
            val state = awaitItem()
            assertEquals("Маша", state.name)
            assertEquals("Денис", fakeNameRepo.getName()) // ещё не сохранено
        }
    }

    @Test
    fun `saveName persists name to repository`() = runTest {
        viewModel.setName("Маша")
        viewModel.saveName()
        assertEquals("Маша", fakeNameRepo.getName())
    }

    @Test
    fun `setNotifications updates uiState and persists`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.setNotifications(false)
            val state = awaitItem()
            assertFalse(state.notificationsEnabled)
            assertFalse(fakeNotifRepo.isEnabled())
        }
    }
}
```

- [ ] **Шаг 2: Запустить тесты — убедиться что падают**

```bash
cd /Users/desokolov/Desktop/projects/projects_different/lists/.claude/mobile
./gradlew :composeApp:testDebugUnitTest --tests "*.SettingsViewModelTest"
```

Ожидаем: FAIL — `error: unresolved reference: SettingsViewModel`

---

## Task 4: Data layer — реализации репозиториев + платформо-специфичный DI

**Files:**
- Create: `commonMain/di/PlatformSettingsModule.kt`
- Create: `androidMain/di/PlatformSettingsModule.kt`
- Create: `iosMain/di/PlatformSettingsModule.kt`
- Create: `data/repository/SettingsThemeRepository.kt`
- Create: `data/repository/SettingsNameRepository.kt`
- Create: `data/repository/SettingsNotificationsRepository.kt`

- [ ] **Шаг 1: Создать expect-декларацию (commonMain)**

Путь: `composeApp/src/commonMain/kotlin/com/desokolov/lists/di/PlatformSettingsModule.kt`
```kotlin
package com.desokolov.lists.di

import org.koin.core.module.Module

expect val platformSettingsModule: Module
```

- [ ] **Шаг 2: Создать Android actual**

Путь: `composeApp/src/androidMain/kotlin/com/desokolov/lists/di/PlatformSettingsModule.kt`
```kotlin
package com.desokolov.lists.di

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformSettingsModule: Module = module {
    single<Settings> {
        AndroidSettings(
            androidContext().getSharedPreferences("lists_prefs", Context.MODE_PRIVATE)
        )
    }
}
```

- [ ] **Шаг 3: Создать iOS actual**

Путь: `composeApp/src/iosMain/kotlin/com/desokolov/lists/di/PlatformSettingsModule.kt`
```kotlin
package com.desokolov.lists.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformSettingsModule: Module = module {
    single<Settings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
}
```

- [ ] **Шаг 4: Создать SettingsThemeRepository.kt**

Путь: `data/repository/SettingsThemeRepository.kt`
```kotlin
package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.repository.ThemeRepository
import com.desokolov.lists.ui.theme.ThemeType
import com.russhwolf.settings.Settings

class SettingsThemeRepository(private val settings: Settings) : ThemeRepository {

    override fun getTheme(): ThemeType =
        ThemeType.entries
            .find { it.name == settings.getStringOrNull(KEY) }
            ?: ThemeType.WHITE

    override fun saveTheme(theme: ThemeType) =
        settings.putString(KEY, theme.name)

    companion object { private const val KEY = "selected_theme" }
}
```

- [ ] **Шаг 5: Создать SettingsNameRepository.kt**

Путь: `data/repository/SettingsNameRepository.kt`
```kotlin
package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.repository.NameRepository
import com.russhwolf.settings.Settings

class SettingsNameRepository(private val settings: Settings) : NameRepository {

    override fun getName(): String = settings.getString(KEY, "")

    override fun saveName(name: String) = settings.putString(KEY, name)

    companion object { private const val KEY = "user_name" }
}
```

- [ ] **Шаг 6: Создать SettingsNotificationsRepository.kt**

Путь: `data/repository/SettingsNotificationsRepository.kt`
```kotlin
package com.desokolov.lists.data.repository

import com.desokolov.lists.domain.repository.NotificationsRepository
import com.russhwolf.settings.Settings

class SettingsNotificationsRepository(private val settings: Settings) : NotificationsRepository {

    override fun isEnabled(): Boolean = settings.getBoolean(KEY, true)

    override fun setEnabled(value: Boolean) = settings.putBoolean(KEY, value)

    companion object { private const val KEY = "notifications_enabled" }
}
```

- [ ] **Шаг 7: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/desokolov/lists/di/PlatformSettingsModule.kt
git add composeApp/src/androidMain/kotlin/com/desokolov/lists/di/PlatformSettingsModule.kt
git add composeApp/src/iosMain/kotlin/com/desokolov/lists/di/PlatformSettingsModule.kt
git add composeApp/src/commonMain/kotlin/com/desokolov/lists/data/repository/Settings*.kt
git commit -m "feat(settings): add platform settings DI and repository implementations"
```

---

## Task 5: SettingsViewModel — сделать тесты зелёными

**Files:**
- Create: `presentation/settings/SettingsUiState.kt`
- Create: `presentation/settings/SettingsViewModel.kt`

- [ ] **Шаг 1: Создать SettingsUiState.kt**

```kotlin
package com.desokolov.lists.presentation.settings

import com.desokolov.lists.ui.theme.ThemeType

data class SettingsUiState(
    val name: String = "",
    val theme: ThemeType = ThemeType.WHITE,
    val notificationsEnabled: Boolean = true
)
```

- [ ] **Шаг 2: Создать SettingsViewModel.kt**

```kotlin
package com.desokolov.lists.presentation.settings

import androidx.lifecycle.ViewModel
import com.desokolov.lists.domain.repository.NameRepository
import com.desokolov.lists.domain.repository.NotificationsRepository
import com.desokolov.lists.domain.repository.ThemeRepository
import com.desokolov.lists.ui.theme.ThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val themeRepository: ThemeRepository,
    private val nameRepository: NameRepository,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            theme = themeRepository.getTheme(),
            name = nameRepository.getName(),
            notificationsEnabled = notificationsRepository.isEnabled()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setTheme(theme: ThemeType) {
        themeRepository.saveTheme(theme)
        _uiState.update { it.copy(theme = theme) }
    }

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun saveName() {
        nameRepository.saveName(_uiState.value.name)
    }

    fun setNotifications(enabled: Boolean) {
        notificationsRepository.setEnabled(enabled)
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }
}
```

- [ ] **Шаг 3: Запустить тесты — убедиться что проходят**

```bash
./gradlew :composeApp:testDebugUnitTest --tests "*.SettingsViewModelTest"
```

Ожидаем: 5 tests PASSED

- [ ] **Шаг 4: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/desokolov/lists/presentation/settings/
git add composeApp/src/commonTest/kotlin/com/desokolov/lists/presentation/SettingsViewModelTest.kt
git commit -m "feat(settings): add SettingsViewModel with TDD (5 tests green)"
```

---

## Task 6: DI — settingsModule + обновить appModules

**Files:**
- Modify: `di/AppModule.kt`

- [ ] **Шаг 1: Добавить импорты и settingsModule в AppModule.kt**

Добавить импорты в начало файла:
```kotlin
import com.desokolov.lists.data.repository.SettingsNameRepository
import com.desokolov.lists.data.repository.SettingsNotificationsRepository
import com.desokolov.lists.data.repository.SettingsThemeRepository
import com.desokolov.lists.domain.repository.NameRepository
import com.desokolov.lists.domain.repository.NotificationsRepository
import com.desokolov.lists.domain.repository.ThemeRepository
import com.desokolov.lists.presentation.settings.SettingsViewModel
```

Добавить модуль перед `val appModules`:
```kotlin
val settingsModule = module {
    singleOf(::SettingsThemeRepository) bind ThemeRepository::class
    singleOf(::SettingsNameRepository) bind NameRepository::class
    singleOf(::SettingsNotificationsRepository) bind NotificationsRepository::class
    singleOf(::SettingsViewModel)
}
```

- [ ] **Шаг 2: Обновить appModules — добавить platformSettingsModule и settingsModule**

Заменить:
```kotlin
val appModules = listOf(dataModule, domainModule, presentationModule)
```

На:
```kotlin
val appModules = listOf(platformSettingsModule, dataModule, domainModule, presentationModule, settingsModule)
```

- [ ] **Шаг 3: Проверить компиляцию**

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

Ожидаем: BUILD SUCCESSFUL

- [ ] **Шаг 4: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/desokolov/lists/di/AppModule.kt
git commit -m "feat(settings): wire settingsModule and platformSettingsModule into Koin"
```

---

## Task 7: Navigation + App.kt — тема применяется глобально

**Files:**
- Modify: `ui/nav/Screen.kt`
- Modify: `App.kt`

- [ ] **Шаг 1: Добавить Screen.Settings в nav**

В `ui/nav/Screen.kt` добавить:
```kotlin
data object Settings : Screen
```

Итоговый файл:
```kotlin
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
```

- [ ] **Шаг 2: Обновить App.kt**

Заменить весь файл:
```kotlin
package com.desokolov.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.desokolov.lists.presentation.auth.AuthViewModel
import com.desokolov.lists.presentation.settings.SettingsViewModel
import com.desokolov.lists.ui.auth.AuthScreen
import com.desokolov.lists.ui.detail.ListDetailScreen
import com.desokolov.lists.ui.lists.ListsScreen
import com.desokolov.lists.ui.nav.Screen
import com.desokolov.lists.ui.settings.SettingsScreen
import com.desokolov.lists.ui.theme.ListsTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val settingsViewModel: SettingsViewModel = koinInject()
    val settingsState by settingsViewModel.uiState.collectAsState()

    ListsTheme(theme = settingsState.theme) {
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
                            },
                            onNavigateToSettings = { screen = Screen.Settings }
                        )
                        is Screen.ListDetail -> ListDetailScreen(
                            listId = s.listId,
                            listTitle = s.listTitle,
                            listType = s.listType,
                            userId = s.userId,
                            userName = s.userName,
                            onBack = { screen = Screen.Lists }
                        )
                        is Screen.Settings -> SettingsScreen(
                            onBack = { screen = Screen.Lists }
                        )
                    }
                }
            }
        }
    }
}
```

- [ ] **Шаг 3: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/desokolov/lists/ui/nav/Screen.kt
git add composeApp/src/commonMain/kotlin/com/desokolov/lists/App.kt
git commit -m "feat(settings): add Screen.Settings, wire SettingsViewModel theme into ListsTheme"
```

---

## Task 8: ListsScreen — добавить ⋮ меню с «Настройки»

**Files:**
- Modify: `ui/lists/ListsScreen.kt`

- [ ] **Шаг 1: Заменить ListsScreen.kt**

```kotlin
package com.desokolov.lists.ui.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onListClick: (listId: String, listTitle: String, listType: ListType) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val viewModel: ListsViewModel = koinInject { parametersOf(userId) }
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddListDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, listType ->
                viewModel.onEvent(ListsEvent.CreateList(title, listType))
                showAddDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои списки") },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Настройки") },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            }
                        )
                    }
                }
            )
        },
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
            state.lists.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Нет списков", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Нажми + чтобы создать первый",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
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
```

- [ ] **Шаг 2: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/desokolov/lists/ui/lists/ListsScreen.kt
git commit -m "feat(settings): add overflow menu with Настройки to ListsScreen"
```

---

## Task 9: SettingsScreen — UI composable

**Files:**
- Create: `ui/settings/SettingsScreen.kt`

- [ ] **Шаг 1: Создать SettingsScreen.kt**

```kotlin
package com.desokolov.lists.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.desokolov.lists.presentation.settings.SettingsViewModel
import com.desokolov.lists.ui.theme.ThemeType
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = koinInject()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveName()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Имя ──────────────────────────────────────
            Text(
                text = "Имя",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 14.dp, top = 8.dp, bottom = 0.dp)
            )
            TextField(
                value = state.name,
                onValueChange = viewModel::setName,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .onFocusChanged { if (!it.isFocused) viewModel.saveName() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.10f)
            )

            // ── Тема ─────────────────────────────────────
            Text(
                text = "Тема",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 14.dp, top = 8.dp, bottom = 4.dp)
            )
            ThemeOption(
                label = "Белая",
                color = Color(0xFFF8F9FF),
                borderColor = Color(0xFFC4BCDA),
                isSelected = state.theme == ThemeType.WHITE,
                onClick = { viewModel.setTheme(ThemeType.WHITE) }
            )
            ThemeOption(
                label = "Чёрная",
                color = Color(0xFF1C1B1F),
                borderColor = Color.Transparent,
                isSelected = state.theme == ThemeType.BLACK,
                onClick = { viewModel.setTheme(ThemeType.BLACK) }
            )
            ThemeOption(
                label = "Розовая",
                color = Color(0xFFFFD6E7),
                borderColor = Color(0xFFF4A5C0),
                isSelected = state.theme == ThemeType.PINK,
                onClick = { viewModel.setTheme(ThemeType.PINK) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.10f)
            )

            // ── Уведомления ───────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Уведомления",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.notificationsEnabled,
                    onCheckedChange = viewModel::setNotifications
                )
            }

            // Пустое место для будущих настроек
            Spacer(modifier = Modifier.weight(1f))

            // Версия
            Text(
                text = "v0.1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ThemeOption(
    label: String,
    color: Color,
    borderColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .then(
                    if (borderColor != Color.Transparent)
                        Modifier.border(1.dp, borderColor, CircleShape)
                    else Modifier
                )
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
```

- [ ] **Шаг 2: Проверить полную компиляцию**

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64
./gradlew :composeApp:assembleDebug
```

Ожидаем: BUILD SUCCESSFUL (оба)

- [ ] **Шаг 3: Запустить все тесты**

```bash
./gradlew :composeApp:testDebugUnitTest
```

Ожидаем: 7 tests PASSED (ListsViewModelTest × 2 + SettingsViewModelTest × 5)

- [ ] **Шаг 4: Commit финальный**

```bash
git add composeApp/src/commonMain/kotlin/com/desokolov/lists/ui/settings/
git commit -m "feat(settings): add SettingsScreen with theme switcher, name, notifications"
```

---

## Итог

После Task 9 работает:
- ⋮ → «Настройки» на главном экране → экран Настроек
- Три темы переключаются мгновенно
- Имя сохраняется при уходе с поля или при нажатии ‹
- Уведомления сохраняются сразу
- Всё персистируется между запусками через NSUserDefaults / SharedPreferences
