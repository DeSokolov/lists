# 0001 — KMP + Compose Multiplatform вместо Flutter

## Статус
Принято

## Контекст
Для проекта нужен кроссплатформенный стек iOS + Android. Рассматривались Flutter и KMP + Compose Multiplatform.

## Решение
Выбран KMP + Compose Multiplatform:
- Единый язык Kotlin для всего проекта (нет переключения контекста Dart/Kotlin)
- Лучшая интеграция с Firebase Kotlin SDK и Android экосистемой
- Совместимость с паттернами из репозитория teplitsa
- Compose Multiplatform стабилен для iOS (beta → stable в 2024)

## Последствия
- Нужен Xcode для сборки iOS (нет standalone iOS build без Mac)
- Меньший пул разработчиков чем у Flutter
- Зависимость от JetBrains Compose Multiplatform roadmap
