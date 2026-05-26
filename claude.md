# lists — CLAUDE.md

> Правила для всех AI-агентов (Claude Code, Cursor, Copilot и др.).
> Прочитай этот файл **перед** любой работой в репозитории.

---

## Что это за проект

**lists** — мобильное приложение (KMP + Compose Multiplatform) для iOS и Android.
Совместные списки любого типа: покупки, вещи в поездку, мероприятия, переезд.
Ключевая фишка: каждый участник видит, кто что взял, в реальном времени.

Разрабатывается по паттернам, принятым в репозитории [teplitsa](https://github.com/SWTec/teplitsa).

Репозиторий: `https://github.com/DeSokolov/my-projects`, папка `lists/`.

---

## Текущее состояние проекта (май 2026)

### Версии
- Kotlin: 2.1.0
- Compose Multiplatform: 1.7.3
- AGP: 8.7.3
- KSP: 2.1.0-1.0.29
- Koin: 4.0.0
- gitlive Firebase SDK: 2.1.0
- Room KMP: 2.7.0 (объявлено, не реализовано)
- Gradle: 8.10.2

### Firebase и stub-режим
- `USE_STUBS = true` в `di/AppModule.kt` — приложение работает без Firebase
- `StubListRepository` и `StubUserRepository` — полностью рабочие in-memory реализации
- Stub-пользователь: `id = "stub-user"`, `displayName = "Денис"`, `email = "dev4@swtlm.com"`
- Чтобы подключить Firebase: добавить `GoogleService-Info.plist` в `iosApp/`, установить `USE_STUBS = false`

### Firebase CocoaPods (важно)
- Firebase поды объявлены с `linkOnly = true` в `composeApp/build.gradle.kts`
- При изменении `cocoapods {}` блока нужно: `./gradlew :composeApp:generateDummyFramework`, затем `pod install`
- Podfile находится в `mobile/iosApp/Podfile`
- `iOSApp.swift` проверяет наличие `GoogleService-Info.plist` перед `FirebaseApp.configure()`

### gitlive Firebase SDK особенности
- FilterBuilder DSL: использовать `contains` (НЕ `arrayContains`) для array-contains запросов
- Пример: `"memberIds" contains userId`

### Навигация
- State-based, без библиотек
- `Screen` — sealed interface в `ui/nav/Screen.kt`
- `Screen.Lists` — главный экран
- `Screen.ListDetail(listId, listTitle, listType, userId, userName)` — детали списка

### Что реализовано в UI
- Экран списков: создание, отображение карточек, переход в детали
- Экран деталей:
  - Взять / Снять пункт
  - Отметить выполненным (чекбокс)
  - Удалить пункт свайпом влево
  - Умные подсказки при добавлении (фильтр по `ListType`)
  - Поиск по названию (иконка в TopAppBar)
  - Фильтр «Все / Мои» (FilterChip)
  - Поделиться списком (копирует `lists://join/{listId}`)
  - Участники (BottomSheet из меню ⋮)
  - Уведомления вкл/выкл (в меню ⋮, real push — после Firebase)

---

## Структура папки `lists/`

```
lists/
├── claude.md                   # Этот файл — правила для агентов
├── README.md                   # Описание проекта, как запустить, как деплоить
├── CHANGELOG.md                # История изменений (Keep a Changelog)
├── docs/
│   ├── requirements.md         # Что продукт должен делать
│   ├── architecture.md         # Как устроен (слои, зависимости)
│   └── adr/                    # Architecture Decision Records — важные решения
├── mobile/
│   ├── composeApp/             # KMP модуль (commonMain / androidMain / iosMain)
│   │   └── src/
│   │       ├── commonMain/     # Shared code: domain, data, presentation
│   │       ├── androidMain/    # Android-specific
│   │       └── iosMain/        # iOS-specific
│   ├── iosApp/                 # Xcode project (генерируется xcodegen)
│   ├── fastlane/
│   │   ├── Fastfile            # Lanes: ios_beta, android_beta, upload_metadata
│   │   ├── Appfile             # Bundle ID, App ID
│   │   ├── Matchfile           # Подписи через match
│   │   └── metadata/           # Тексты для App Store / Play Store
│   ├── CHANGELOG.md            # Changelog мобильного приложения
│   └── README.md               # Как запустить локально
└── landing/                    # (опционально) Лендинг на Astro
    ├── src/
    ├── scripts/deploy.sh
    └── README.md
```

---

## Правила для агентов — обязательные

### Никогда
- Не коммить секреты, ключи, токены, `.env`-файлы
- Не делай `git push --force` в `master`
- Не вызывай SDK (Firebase, RevenueCat, AdMob) напрямую из UI или ViewModel — только через Repository
- Не выходи за рамки поставленной задачи: баг-фикс не требует рефакторинга вокруг

### Всегда
- Читай этот файл перед началом работы
- Следуй архитектурным слоям (см. раздел «Архитектура»)
- Пиши коммиты в формате Conventional Commits
- Обновляй `CHANGELOG.md` при добавлении фичи или фиксе бага
- Создавай ветку от `master`, не пуши напрямую в `master`

### Когда нужен человек
- Публикация релиза в App Store / Google Play
- Ротация секретов и ключей
- Решение об архитектурных изменениях, влияющих на всю кодовую базу
- Монетизация: изменение цен, IAP, подписок

---

## Архитектура

### Слои (обязательно соблюдать)

```
UI  ──►  Presentation  ──►  Domain  ──►  Data
         (ViewModel)       (UseCase)    (Repository impl)
```

| Слой | Что содержит | Правила |
|------|-------------|---------|
| **UI** | Composable-функции, экраны | Только рендер состояния + отправка событий. Никакой логики. |
| **Presentation** | ViewModel | Держит `StateFlow<UiState>`, вызывает UseCase / Repository, не знает о платформе |
| **Domain** | Модели, UseCase, интерфейсы Repository | Чистый Kotlin. Никаких импортов SDK. |
| **Data** | Реализации Repository | Единственное место для вызовов Firebase, Room, API. Интерфейс — доменные типы. |

### Repository Pattern

```kotlin
// domain/repository/ItemRepository.kt
interface ItemRepository {
    fun getItems(): Flow<List<Item>>
    suspend fun addItem(item: Item): Result<Unit>
    suspend fun deleteItem(id: String): Result<Unit>
}

// data/repository/FirebaseItemRepository.kt
class FirebaseItemRepository(
    private val firestore: FirebaseFirestore
) : ItemRepository {
    override fun getItems(): Flow<List<Item>> = TODO()
    override suspend fun addItem(item: Item) = runCatching { TODO() }.toResult()
    override suspend fun deleteItem(id: String) = runCatching { TODO() }.toResult()
}
```

### UiState Pattern

```kotlin
data class ListsUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ListsViewModel(private val repo: ItemRepository) : ViewModel() {
    val uiState: StateFlow<ListsUiState> = TODO()
}
```

### Обработка ошибок

- Ожидаемые ошибки → `Result<T>` (caller обязан обработать)
- Программные ошибки (баги) → throw (пусть Crashlytics поймает)
- `CancellationException` → всегда rethrow, никогда не проглатывать

---

## Git-правила

### Ветки

Формат: `type/scope/короткое-описание`

| Тип | Когда использовать |
|-----|--------------------|
| `feat` | Новая функциональность |
| `fix` | Исправление бага |
| `chore` | Зависимости, конфиги, служебное |
| `docs` | Только документация |
| `refactor` | Рефакторинг без новой функции |
| `test` | Только тесты |
| `ci` | CI/CD конфиги |

Примеры:
```
feat/lists/add-item-screen
fix/lists/crash-on-empty-state
chore/deps/update-compose-version
```

### Коммиты (Conventional Commits)

```
<type>(<scope>): <imperative summary>

[optional body]

[optional footers]
```

Примеры:
```
feat(lists): add swipe-to-delete for list items
fix(lists): crash when opening empty list on iOS
chore(deps): bump Kotlin to 2.1.0
```

### Merge-стратегия

- **Squash and merge** — один PR = один коммит в `master`
- Не пушить напрямую в `master`
- Ветку удалять после мерджа

---

## Тестирование

### Что тестировать обязательно
- **ViewModel** — всегда (держит состояние)
- **UseCase** — всегда (бизнес-логика)
- **Repository** — только если ты писал логику (композиты, кэши); не тестируй обёртки над SDK

### Что не тестировать
- SDK-обёртки (Firebase, Room)
- Composable-функции (UI) без чёткой причины

### Стек
```kotlin
// build.gradle.kts (commonTest)
implementation(kotlin("test"))
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
implementation("app.cash.turbine:turbine:1.2.0")
// Для UI-тестов (androidTest)
implementation("io.mockk:mockk:1.13.13")
```

### Структура тестов = структура источника
```
commonMain/kotlin/com/example/lists/presentation/ListsViewModel.kt
commonTest/kotlin/com/example/lists/presentation/ListsViewModelTest.kt
```

---

## Деплой

### Мобильное приложение

Вся логика деплоя — в `mobile/fastlane/Fastfile`. CI (GitHub Actions) только запускает lane.

```bash
# Бета-релиз
cd mobile && bundle exec fastlane ios beta
cd mobile && bundle exec fastlane android beta

# Обновить метаданные в App Store / Play
cd mobile && bundle exec fastlane ios upload_metadata
cd mobile && bundle exec fastlane android upload_metadata
```

### Версионирование

- **SemVer:** `MAJOR.MINOR.PATCH`
- Начало: `0.1.0`
- Git-теги на релиз: `lists/mobile/v0.1.0`
- `versionCode` для Android берётся из Play Console + 1 (не задавать вручную)
- Только prod получает теги; TestFlight / Internal testing — нет

### Changelog

- Файл: `mobile/CHANGELOG.md`
- Формат: [Keep a Changelog](https://keepachangelog.com/ru/1.0.0/)
- Новые изменения — в раздел `[Unreleased]`
- При релизе — переименовать в `[0.x.y] - YYYY-MM-DD`

```markdown
# Changelog

## [Unreleased]
### Added
- Добавлен экран создания списка

## [0.1.0] - 2026-05-25
### Added
- Первый релиз
```

---

## Безопасность

### Три категории ключей

| Категория | Где хранить | Примеры |
|-----------|------------|---------|
| Деплой-секреты | GitHub Secrets → env vars в CI | Keystore, ASC API key, Play service account |
| Публичные SDK-идентификаторы | В коде (не секрет) | `google-services.json`, Firebase app ID, RevenueCat public key |
| Настоящие секреты | GitHub Secrets, никогда в коде | Backend API keys, admin credentials |

### Хранение на устройстве
- Токены, PII → iOS Keychain / Android EncryptedSharedPreferences
- Никогда не UserDefaults / SharedPreferences

---

## Стек

| Компонент | Выбор |
|-----------|-------|
| Мобильный UI | Compose Multiplatform (KMP) |
| Язык | Kotlin |
| База данных (локально) | Room (KMP) |
| Backend / Auth | Firebase (Firestore, Auth, Remote Config) |
| Подписки | RevenueCat |
| Аналитика | Firebase Analytics |
| Ошибки | Firebase Crashlytics |
| Сборка iOS | fastlane match + xcodegen |
| Сборка Android | Gradle + fastlane supply |
| Лендинг | Astro (если нужен) |

---

## Чеклист перед первым PR

- [ ] `README.md` с описанием, инструкцией запуска и деплоя
- [ ] `docs/requirements.md` — что приложение делает
- [ ] `docs/architecture.md` — как устроено
- [ ] `mobile/CHANGELOG.md` с разделом `[Unreleased]`
- [ ] Все секреты в `.gitignore` (`.env`, `*.jks`, `GoogleService-Info.plist` с prod-ключами)
- [ ] `mobile/fastlane/Fastfile` с lane `ios beta` и `android beta`
- [ ] Архитектурные слои соблюдены (нет прямых вызовов Firebase из ViewModel)
- [ ] Тесты для ViewModel и UseCase

---

## Контакты

Владелец проекта: @DeSokolov
Репозиторий: https://github.com/DeSokolov/my-projects
