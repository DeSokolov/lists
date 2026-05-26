# Lists Mobile

KMP + Compose Multiplatform приложение для iOS и Android.

## Требования

- JDK 17+
- Android SDK 34
- Xcode 15+ (для iOS)
- Ruby + Bundler (для fastlane)

## Запуск

### Android

```bash
./gradlew :composeApp:assembleDebug
# или запустить через Android Studio
```

### iOS

```bash
./gradlew :composeApp:generateDummyFramework
# затем открыть iosApp/iosApp.xcodeproj в Xcode
```

## Структура

```
mobile/
├── composeApp/
│   └── src/
│       ├── commonMain/   # Весь shared код
│       ├── androidMain/  # Android-специфика
│       └── iosMain/      # iOS-специфика
├── iosApp/               # Xcode project
├── fastlane/             # Деплой
└── CHANGELOG.md
```

## Деплой

```bash
bundle install
bundle exec fastlane ios beta
bundle exec fastlane android beta
```
