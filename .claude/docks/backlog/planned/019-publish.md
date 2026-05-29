# 019 — Публикация App Store + Google Play

**Статус:** 🔲 Backlog  
**Приоритет:** 🟡 Средний

## Описание
Первая публикация приложения в магазинах.

## Что нужно сделать
- [ ] Подготовить метаданные (описание, скриншоты, иконки)
- [ ] Настроить `mobile/fastlane/metadata/`
- [ ] Настроить подписи: `fastlane match` для iOS, keystore для Android
- [ ] GitHub Actions: CI для сборки и публикации
- [ ] Запустить `fastlane ios beta` → TestFlight
- [ ] Запустить `fastlane android beta` → Internal Testing
- [ ] После тестирования — релиз в App Store и Google Play
- [ ] Git-тег `lists/mobile/v0.1.0`

## Зависимости
Требует: 013, 014, 015, 016, 017, 018 — все выполнены
