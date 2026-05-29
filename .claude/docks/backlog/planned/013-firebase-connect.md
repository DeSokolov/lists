# 013 — Подключить Firebase

**Статус:** 🔲 Backlog  
**Приоритет:** 🔴 Высокий (блокирует 014, 015, 016)

## Описание
Подключение реального Firebase взамен stub-реализаций.

## Что нужно сделать
- [ ] Создать проект в Firebase Console
- [ ] Добавить `GoogleService-Info.plist` в `iosApp/`
- [ ] Добавить `google-services.json` в `composeApp/`
- [ ] Установить `USE_STUBS = false` в `di/AppModule.kt`
- [ ] Запустить `./gradlew :composeApp:generateDummyFramework`
- [ ] Выполнить `pod install` в `mobile/iosApp/`
- [ ] Проверить `FirebaseApp.configure()` в `iOSApp.swift`

## Зависимости
Разблокирует: 014, 015, 016
