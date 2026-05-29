# lists — CLAUDE.md

> Главный файл проекта. Читается первым.
> Правила для всех AI-агентов (Claude Code, Cursor, Copilot и др.).
> Прочитай этот файл **перед** любой работой в репозитории.

---

## Как устроена работа

### Флоу разработки фичи

```
┌─────────────────────────────────────────────────────────────────┐
│  БИЗНЕС (@DeSokolov)                                            │
│  «Хочу добавить фичу / изменить поведение / исправить проблему» │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│  🧑‍💼  АГЕНТ: business-analyst                                     │
│                                                                  │
│  • Обсуждает идею с бизнесом, задаёт уточняющие вопросы         │
│  • Анализирует рынок и сценарии использования                   │
│  • Формирует User Story                                          │
│                                                                  │
│  КРИТЕРИЙ УСПЕХА:                                                │
│  📄 User Story сохранена в docks/user-stories/                  │
└──────────────────────────┬───────────────────────────────────────┘
                           │
              ┌────────────┴────────────┐
              │ (параллельно)           │
              ▼                         ▼
┌─────────────────────────┐  ┌──────────────────────────┐
│ 🏗️  АГЕНТ:              │  │ 🎨  АГЕНТ:               │
│ mobile-architect-       │  │ ui-ux-designer           │
│ ios-android             │  │                          │
│                         │  │ • Читает User Story      │
│ • Читает User Story     │  │ • Проектирует UI/UX      │
│ • Проектирует           │  │ • Создаёт спецификацию   │
│   архитектуру фичи      │  │   экранов и компонентов  │
│ • Определяет слои,      │  │ • Согласует с            │
│   модели, паттерны      │  │   архитектором           │
│                         │  │                          │
└──────────┬──────────────┘  └────────────┬─────────────┘
           │                              │
           └─────────────┬────────────────┘
                         │
                         ▼
           КРИТЕРИЙ УСПЕХА (оба агента):
           📋 Задача создана в docks/backlog/planned/
              (архитектурные + дизайн решения внутри)
                         │
                         ▼
┌────────────────────────────────────────────────────────────────┐
│  📱  АГЕНТ: mobile-developer-ios                               │
│                                                                │
│  • Читает задачу из docks/backlog/planned/                    │
│  • Реализует фичу строго по спецификации                      │
│  • Пишет тесты (ViewModel, UseCase)                           │
│  • Собирает KMP-фреймворк → Xcode build → запуск симулятора  │
│  • Перемещает задачу в docks/backlog/archived/                │
│                                                                │
│  КРИТЕРИЙ УСПЕХА:                                              │
│  ✅ Фича работает на симуляторе Xcode (iPhone 17 Pro)         │
└────────────────────────────────────────────────────────────────┘
```

### Папки агентов

| Папка | Назначение |
|-------|-----------|
| `docks/user-stories/` | User Stories от бизнес-аналитика — вход для архитектора и дизайнера |
| `docks/backlog/planned/` | Задачи с архитектурным + дизайн решением — вход для разработчика |
| `docks/backlog/archived/` | Выполненные задачи |
| `docks/plans/` | Детальные планы реализации |
| `docks/specs/` | Дизайн-спецификации |
| `docks/brainstorm/` | Мозговые штурмы, прототипы, HTML-демо |

### Агенты и их роли

| Агент | Файл | Вход | Выход |
|-------|------|------|-------|
| `business-analyst` | `agents/business-analyst.md` | Идея от бизнеса | `docks/user-stories/` |
| `mobile-architect-ios-android` | `agents/mobile-architect-ios-android.md` | `docks/user-stories/` | `docks/backlog/planned/` |
| `ui-ux-designer` | `agents/ui-ux-designer.md` | `docks/user-stories/` | `docks/backlog/planned/` |
| `mobile-developer-ios` | `agents/mobile-developer-ios.md` | `docks/backlog/planned/` | Xcode симулятор + `archived/` |

### Важно

- Любой агент может быть вызван в любой момент для **уточнения, консультации или брейнсторминга** — флоу линейный, но не жёсткий
- Архитектор и дизайнер работают **параллельно** и **согласуют решения между собой** перед тем как писать задачу в `planned/`
- Разработчик **не берёт задачу** пока оба — архитектор и дизайнер — не завершили свою часть
- Бизнес (@DeSokolov) — финальный арбитр по любым спорным решениям

---

## Концепция

**Lists** — мобильное приложение для совместных списков любого типа: покупки, вещи в поездку, подготовка к мероприятию, переезд. Вдохновение: «Купи Батон» — но значительно шире по сценариям.

**UTP.** Конкуренты решают только часть задачи: Bring!/Купи Батон — только покупки; Packr/PackPoint — только поездки без нормальной совместной работы; SingularityApp/TickTick — таск-менеджеры, слишком сложные для бытового использования. Lists — одно приложение для любого типа списка с живым статусом у всех участников.

**Ключевая механика — «Кто что берёт»:**
1. Участник нажимает «Взять» → все видят «→ Маша»
2. Передумал → нажимает «Снять»
3. Купил/взял → ставит ✓, пункт уезжает вниз
4. Push при добавлении нового пункта в общий список

**Типы списков:** 🛒 Покупки · 🧳 Поездка · 🎉 Мероприятие · 📦 Переезд · ✅ Общий

**Монетизация (Freemium):**

| Бесплатно | Premium (~99–199 ₽/мес) |
|-----------|--------------------------|
| До 2 активных списков | Неограниченные списки |
| До 3 участников | Неограниченные участники |
| Базовые типы | Шаблоны + история |

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

Полная архитектурная документация, паттерны и правила — в отдельном файле:

📄 **[`agent-memory/mobile-architect-ios-android/docs/architecture.md`](agent-memory/mobile-architect-ios-android/docs/architecture.md)**

Агент `mobile-architect-ios-android` обязан читать этот файл перед любой работой.
Ключевые принципы: `UI → Presentation → Domain → Data`, Repository Pattern, UiState Pattern, DI (Koin), Stub-режим, Firestore модель.

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
