# 001 — Настройка проекта

**Статус:** ✅ Готово

## Описание
Инициализация KMP + Compose Multiplatform проекта с поддержкой iOS и Android из одной кодовой базы.

## Что сделано
- Создан KMP модуль `composeApp` с `commonMain`, `androidMain`, `iosMain`
- Настроен Gradle (AGP 8.7.3, Kotlin 2.1.0, Compose Multiplatform 1.7.3)
- Подключён Koin 4.0.0 для DI
- Настроен xcodegen для Xcode проекта
- Подключён fastlane (match + supply)
- Firebase CocoaPods объявлены с `linkOnly = true`
