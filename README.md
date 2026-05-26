# Lists

Мобильное приложение для совместных списков любого типа — покупки, вещи в поездку, подготовка к мероприятию. Каждый участник группы видит кто что взял/сделал в реальном времени.

## Стек

- **Kotlin Multiplatform + Compose Multiplatform** — iOS и Android из одной кодовой базы
- **Firebase** — Firestore (real-time sync), Auth, Analytics, Crashlytics
- **RevenueCat** — подписки
- **Room (KMP)** — локальная база данных

## Структура

```
lists/
├── claude.md           # Правила для AI-агентов
├── docs/               # Документация
│   ├── requirements.md
│   ├── architecture.md
│   └── adr/
├── mobile/             # KMP-приложение
│   ├── composeApp/
│   ├── iosApp/
│   └── fastlane/
└── landing/            # Лендинг (опционально)
```

## Запуск

```bash
cd mobile
./gradlew :composeApp:assembleDebug        # Android
./gradlew :composeApp:iosSimulatorArm64    # iOS framework
```

## Деплой

```bash
cd mobile
bundle exec fastlane ios beta
bundle exec fastlane android beta
```

## Документация

- [Требования](docs/requirements.md)
- [Архитектура](docs/architecture.md)
- [Идея и концепция](lists.md)
