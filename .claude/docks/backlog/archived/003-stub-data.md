# 003 — Стаб-данные

**Статус:** ✅ Готово

## Описание
Полностью рабочие in-memory реализации репозиториев для разработки без Firebase.

## Что сделано
- `USE_STUBS = true` в `di/AppModule.kt`
- `StubListRepository` — stub реализация списков
- `StubUserRepository` — stub реализация пользователя
- Stub-пользователь: `id = "stub-user"`, `displayName = "Денис"`

## Как переключить на Firebase
1. Добавить `GoogleService-Info.plist` в `iosApp/`
2. Установить `USE_STUBS = false` в `AppModule.kt`
