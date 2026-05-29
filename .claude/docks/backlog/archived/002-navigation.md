# 002 — Навигация

**Статус:** ✅ Готово

## Описание
State-based навигация без сторонних библиотек.

## Что сделано
- `Screen` — sealed interface в `ui/nav/Screen.kt`
- `Screen.Lists` — главный экран списков
- `Screen.ListDetail(listId, listTitle, listType, userId, userName)` — детали списка
- Переходы управляются через состояние, без Compose Navigation
