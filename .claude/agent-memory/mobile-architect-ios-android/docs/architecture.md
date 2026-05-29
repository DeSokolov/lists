# Архитектура — Lists

> Обязательный файл для агента `mobile-architect-ios-android`.
> Все решения и код должны строго соответствовать этим принципам.
> При любом отступлении — явно обосновать причину.

---

## Слои

```
UI  ──►  Presentation  ──►  Domain  ──►  Data
         (ViewModel)       (UseCase)    (Repository impl)
```

| Слой | Пакет | Что содержит | Жёсткие правила |
|------|-------|-------------|-----------------|
| **UI** | `ui/` | Composable-экраны, компоненты | Только рендер `UiState` + отправка событий. **Никакой логики.** Никаких вызовов SDK. |
| **Presentation** | `presentation/` | ViewModel | Держит `StateFlow<UiState>`, вызывает UseCase / Repository. **Не знает о платформе и SDK.** |
| **Domain** | `domain/` | Модели, UseCase, интерфейсы Repository | **Чистый Kotlin. Никаких импортов SDK** (Firebase, Room, RevenueCat и др.). |
| **Data** | `data/` | Реализации Repository | **Единственное** место для вызовов Firebase, Room, API. Интерфейс — только доменные типы. |

### Правила слоёв — никогда не нарушать

- SDK (Firebase, RevenueCat, AdMob) вызываются **только** из Data-слоя (Repository impl)
- ViewModel **не импортирует** ничего из `data/` напрямую — только через интерфейсы Domain
- UI-слой **не держит** бизнес-логику, не обращается к Repository напрямую
- Domain-слой **не зависит** ни от какого внешнего фреймворка

---

## Структура пакетов

```
commonMain/kotlin/com/desokolov/lists/
├── domain/
│   ├── model/
│   │   ├── ShoppingList.kt
│   │   ├── ListItem.kt
│   │   └── User.kt
│   ├── repository/
│   │   ├── ListRepository.kt
│   │   ├── UserRepository.kt
│   │   ├── ThemeRepository.kt
│   │   ├── NameRepository.kt
│   │   └── NotificationsRepository.kt
│   └── usecase/
│       ├── GetListsUseCase.kt
│       ├── CreateListUseCase.kt
│       ├── AddItemUseCase.kt
│       ├── ClaimItemUseCase.kt
│       └── CheckItemUseCase.kt
├── data/
│   ├── repository/
│   │   ├── FirebaseListRepository.kt
│   │   ├── FirebaseUserRepository.kt
│   │   ├── StubListRepository.kt      ← USE_STUBS = true
│   │   ├── StubUserRepository.kt
│   │   ├── SettingsThemeRepository.kt
│   │   ├── SettingsNameRepository.kt
│   │   └── SettingsNotificationsRepository.kt
│   └── local/
│       └── RoomListRepository.kt      ← запланировано
├── presentation/
│   ├── lists/
│   ├── detail/
│   ├── auth/
│   └── settings/
├── ui/
│   ├── lists/
│   ├── detail/
│   ├── auth/
│   ├── settings/
│   ├── nav/Screen.kt
│   └── theme/Theme.kt
└── di/
    ├── AppModule.kt
    └── PlatformSettingsModule.kt      ← expect/actual
```

---

## Ключевые паттерны

### Repository Pattern

```kotlin
// domain/repository/ListRepository.kt
interface ListRepository {
    fun getLists(userId: String): Flow<List<ShoppingList>>
    suspend fun createList(list: ShoppingList): Result<String>
    suspend fun deleteList(id: String): Result<Unit>
}

// data/repository/FirebaseListRepository.kt
class FirebaseListRepository(
    private val firestore: FirebaseFirestore
) : ListRepository {
    override fun getLists(userId: String): Flow<List<ShoppingList>> = TODO()
    override suspend fun createList(list: ShoppingList) = runCatching { TODO() }
    override suspend fun deleteList(id: String) = runCatching { TODO() }
}
```

### UiState Pattern

```kotlin
data class ListsUiState(
    val lists: List<ShoppingList> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ListsViewModel(
    private val getLists: GetListsUseCase,
    private val createList: CreateListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListsUiState(isLoading = true))
    val uiState: StateFlow<ListsUiState> = _uiState.asStateFlow()
}
```

### Обработка ошибок

- Ожидаемые ошибки → `Result<T>` — caller **обязан** обработать
- Программные ошибки (баги) → `throw` — пусть Crashlytics поймает
- `CancellationException` → всегда **rethrow**, никогда не проглатывать

### Навигация

State-based, без навигационных библиотек:
```kotlin
// ui/nav/Screen.kt
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
Переключение экранов через `var screen by remember { mutableStateOf<Screen>(Screen.Lists) }` в `App.kt`.

### DI (Koin 4.0.0)

Модули в `di/AppModule.kt`:
- `platformSettingsModule` — expect/actual, предоставляет `Settings`
- `dataModule` — репозитории (stub или Firebase по флагу `USE_STUBS`)
- `domainModule` — UseCase как `factory`
- `presentationModule` — ViewModel как `factory` с параметрами
- `settingsModule` — SettingsViewModel как `single`

---

## Модель данных (Firestore)

```
/lists/{listId}
  title: String
  ownerId: String
  memberIds: [String]
  createdAt: Timestamp
  type: String  // SHOPPING | TRAVEL | EVENT | MOVING | GENERAL

/lists/{listId}/items/{itemId}
  title: String
  quantity: String?
  claimedBy: String?      // userId
  claimedByName: String?
  isDone: Boolean
  createdAt: Timestamp

/users/{userId}
  displayName: String
  email: String?
  photoUrl: String?
```

**gitlive Firebase SDK:** использовать `contains` (не `arrayContains`) для array-contains запросов:
```kotlin
"memberIds" contains userId
```

---

## Синхронизация и offline

- Firestore SDK обеспечивает offline-кэш автоматически
- Room KMP 2.7.0 запланирован для дополнительного кэширования (фича 017)
- При восстановлении сети — автосинхронизация через Firestore listeners

---

## Stub-режим

`USE_STUBS = true` в `di/AppModule.kt` — приложение работает без Firebase:
- `StubListRepository`, `StubUserRepository` — полноценные in-memory реализации
- Stub-пользователь: `id = "stub-user"`, `displayName = "Денис"`, `email = "dev4@swtlm.com"`
- Переключить на реальный Firebase: добавить `GoogleService-Info.plist` в `iosApp/`, `google-services.json` в `composeApp/`, установить `USE_STUBS = false`

---

## ADR (Architecture Decision Records)

Хранятся в `docs/adr/`. Формат: `NNNN-название.md`.

Принятые решения:
- **[0001](adr/0001-kmp-compose-multiplatform.md)** — KMP + Compose Multiplatform вместо Flutter. Причина: единый Kotlin, лучшая интеграция с Firebase/Android экосистемой, паттерны из репозитория teplitsa.
