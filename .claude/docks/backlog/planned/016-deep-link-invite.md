# 016 — Deep Link — приглашение по ссылке

**Статус:** 🔲 Backlog  
**Приоритет:** 🔴 Высокий

## Описание
Реальная обработка deep link для вступления в список по приглашению.

## Что нужно сделать
- [ ] Настроить Universal Links (iOS) и App Links (Android)
- [ ] Обработать схему `lists://join/{listId}`
- [ ] При открытии ссылки: добавить пользователя в участники списка
- [ ] Если приложение не установлено — редирект в App Store / Google Play

## Зависимости
Требует: [013-firebase-connect.md](013-firebase-connect.md), [015-auth.md](015-auth.md)  
UI готов в: [010-share-link.md](../archived/010-share-link.md)
