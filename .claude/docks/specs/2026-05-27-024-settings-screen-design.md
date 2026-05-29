# 024 — Экран Настроек + Цветовые темы

**Дата:** 2026-05-27  
**Статус:** ✅ Дизайн согласован  
**Фича:** 024 из roadmap.md

---

## Цель

Дать пользователю возможность переключать цветовую тему приложения (Белая / Чёрная / Розовая), сохранить имя (отображается участникам) и управлять уведомлениями — всё в одном экране Настроек.

---

## Точка входа

`ListsScreen` → меню ⋮ (DropdownMenu) → пункт **«Настройки»** → `Screen.Settings`

---

## Экран Настроек — UI

### Структура (сверху вниз)

```
TopAppBar
  ← (back)   Настройки

── Имя ──────────────────────────
  [TextField: "Денис"           ]

────────────────────────────────

── Тема ─────────────────────────
  ● Белая       ← выбранная: галочка внутри круга
  ○ Чёрная
  ○ Розовая

────────────────────────────────

── Уведомления ──────────── [●○]

<пустое место для будущих пунктов>

                        v0.1.0
```

### Детали компонентов

| Элемент | Спецификация |
|---------|-------------|
| TopAppBar | Стандартный M3, navigationIcon = ArrowBack, title = «Настройки» |
| Метки секций | `bodyMedium` 14sp, `onBackground` color, padding start = 14dp, padding top = 8dp |
| Поле «Имя» | `OutlinedTextField` → нет обводки, `filled` стиль, `containerColor = surfaceVariant`, border-radius 8dp, margin horizontal 14dp |
| Разделители | `HorizontalDivider`, opacity 10%, margin horizontal 14dp |
| Кружки темы | `Box` 18×18dp, `CircleShape`, padding vertical 3dp на каждый пункт, выбранная — `Icon(Check, tint=primary)` внутри; название `bodySmall` 12sp справа |
| Кружки расположены | вертикально, column, padding horizontal 14dp |
| Toggle уведомлений | `Switch` M3, padding horizontal 14dp, `Row` с `weight(1f)` для лейбла |
| Пустое место | `Spacer(modifier = Modifier.weight(1f))` |
| Версия | `Text("v0.1.0", style=labelSmall, opacity=0.25)`, выравнивание по центру, padding bottom |

---

## Три темы — цветовые токены

### ⚪ WHITE (текущая, `lightColorScheme`)
| Токен | Значение |
|-------|---------|
| background / surface | #F8F9FF |
| primary | #4052B5 |
| surfaceVariant | #E3E1EC |
| onBackground | #1B1B21 |
| onSurfaceVariant | #46464F |

### ⚫ BLACK (`darkColorScheme`)
| Токен | Значение |
|-------|---------|
| background / surface | #111318 |
| primary | #BBC4FF |
| surfaceVariant | #2B2930 |
| onBackground | #E6E0E9 |
| onSurfaceVariant | #928F9A |

### 🩷 PINK (`lightColorScheme`)
| Токен | Значение |
|-------|---------|
| background / surface | #FFF8F9 |
| primary | #B5346B |
| surfaceVariant | #F8D7E3 |
| onBackground | #22191C |
| onSurfaceVariant | #7D5260 |

---

## Архитектура

### Новые файлы

```
commonMain/
  ui/
    settings/
      SettingsScreen.kt          ← Composable экрана
  ui/theme/
    AppTheme.kt                  ← enum AppTheme { WHITE, BLACK, PINK } + colorScheme()
  domain/repository/
    ThemeRepository.kt           ← interface: getTheme() / saveTheme(AppTheme)
    NameRepository.kt            ← interface: getName() / saveName(String)
  data/repository/
    SettingsThemeRepository.kt   ← multiplatform-settings impl
    SettingsNameRepository.kt    ← multiplatform-settings impl
  presentation/settings/
    SettingsViewModel.kt         ← StateFlow<SettingsUiState>
```

### Изменения существующих файлов

| Файл | Изменение |
|------|-----------|
| `ui/nav/Screen.kt` | добавить `Screen.Settings` |
| `App.kt` | koinInject ThemeViewModel, collectAsState AppTheme, передать в ListsTheme; добавить ветку `Screen.Settings` |
| `ui/theme/Theme.kt` | `ListsTheme(theme: AppTheme)` принимает тему как параметр |
| `ui/lists/ListsScreen.kt` | в DropdownMenu добавить пункт «Настройки» → `onNavigateToSettings()` |
| `di/AppModule.kt` | добавить settingsModule: `Settings()`, репозитории, `SettingsViewModel` как `single` |
| `gradle/libs.versions.toml` | `multiplatform-settings = "1.2.0"` |
| `composeApp/build.gradle.kts` | `implementation(libs.multiplatform.settings)` в commonMain |

### UiState

```kotlin
data class SettingsUiState(
    val name: String = "",
    val theme: AppTheme = AppTheme.WHITE,
    val notificationsEnabled: Boolean = true
)
```

### Хранение

| Ключ | Тип | Хранилище |
|------|-----|-----------|
| `selected_theme` | String (enum name) | multiplatform-settings |
| `user_name` | String | multiplatform-settings |
| `notifications_enabled` | Boolean | multiplatform-settings |

---

## Поведение

- Смена темы применяется **немедленно** (без перезапуска) через `StateFlow<AppTheme>` в `ThemeViewModel`
- Имя сохраняется при потере фокуса (`onFocusChanged`) и при нажатии «Назад»
- Уведомления: глобальный переключатель (разрешить / запретить push вообще); Switch сразу пишет в settings; реальный push-механизм — фича 014. Не путать с текущим per-list тоглом в ⋮ меню экрана деталей — тот остаётся
- Кнопка «Назад» возвращает на `Screen.Lists`

---

## Out of scope

- Системная тёмная тема (followSystem) — не нужна сейчас
- Авторизация, аватар — фича 015
- Push-подписка — фича 014
- Язык интерфейса — backlog

---

## Зависимости

- `multiplatform-settings 1.2.0` (Russhwolf) — то же решение что и фича 018
- `ThemeViewModel` объявлен как `single` в Koin — один экземпляр на всё приложение

---

## Мокап

Файл: `.superpowers/brainstorm/62810-1779911192/content/settings-v5.html`  
Сервер: `http://localhost:49482`
