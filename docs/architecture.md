# Архитектура

## Обзор

KMP (Kotlin Multiplatform) + Compose Multiplatform. Весь бизнес-код и UI — в `commonMain`. Платформо-специфичный код минимален.

## Слои

```
UI  ──►  Presentation  ──►  Domain  ──►  Data
         (ViewModel)       (UseCase)    (Repository impl)
```

| Слой | Пакет | Что содержит | Правила |
|------|-------|-------------|---------|
| **UI** | `ui/` | Composable-экраны, компоненты | Только рендер UiState + отправка событий. Никакой логики. |
| **Presentation** | `presentation/` | ViewModel | `StateFlow<UiState>`, вызывает UseCase. Не знает о платформе и SDK. |
| **Domain** | `domain/` | Модели, UseCase, интерфейсы Repository | Чистый Kotlin. Никаких импортов SDK. |
| **Data** | `data/` | Реализации Repository | Единственное место для Firebase, Room, API. |

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
│   │   └── UserRepository.kt
│   └── usecase/
│       ├── GetListsUseCase.kt
│       ├── CreateListUseCase.kt
│       ├── AddItemUseCase.kt
│       ├── ClaimItemUseCase.kt
│       └── CheckItemUseCase.kt
├── data/
│   ├── repository/
│   │   ├── FirebaseListRepository.kt
│   │   └── FirebaseUserRepository.kt
│   └── local/
│       └── RoomListRepository.kt
├── presentation/
│   ├── lists/
│   │   ├── ListsViewModel.kt
│   │   └── ListsUiState.kt
│   ├── detail/
│   │   ├── ListDetailViewModel.kt
│   │   └── ListDetailUiState.kt
│   └── auth/
│       ├── AuthViewModel.kt
│       └── AuthUiState.kt
└── ui/
    ├── lists/
    │   ├── ListsScreen.kt
    │   └── ListCard.kt
    ├── detail/
    │   ├── ListDetailScreen.kt
    │   └── ItemRow.kt
    ├── auth/
    │   └── AuthScreen.kt
    └── theme/
        ├── Theme.kt
        ├── Color.kt
        └── Type.kt
```

## Ключевые паттерны

### Repository

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

### UiState

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
    val uiState: StateFlow<ListsUiState> = TODO()
}
```

### Обработка ошибок

- Ожидаемые ошибки → `Result<T>` (caller обязан обработать)
- Баги → throw (Crashlytics поймает)
- `CancellationException` → всегда rethrow

## Модель данных (Firestore)

```
/lists/{listId}
  - title: String
  - ownerId: String
  - memberIds: [String]
  - createdAt: Timestamp
  - type: String  // shopping | travel | event | moving | other

/lists/{listId}/items/{itemId}
  - title: String
  - quantity: String?
  - claimedBy: String?      // userId
  - claimedByName: String?  // display name
  - isDone: Boolean
  - createdAt: Timestamp

/users/{userId}
  - displayName: String
  - email: String?
  - photoUrl: String?
```

## Синхронизация и offline

- Firestore SDK обеспечивает offline-кэш автоматически
- Room используется для дополнительного кэширования и работы без сети
- При восстановлении сети — автоматическая синхронизация через Firestore listeners
