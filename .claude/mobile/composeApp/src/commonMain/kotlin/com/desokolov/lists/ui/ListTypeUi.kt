package com.desokolov.lists.ui

import com.desokolov.lists.domain.model.ListType

fun ListType.emoji(): String = when (this) {
    ListType.SHOPPING -> "🛒"
    ListType.TRAVEL   -> "🧳"
    ListType.EVENT    -> "🎉"
    ListType.MOVING   -> "📦"
    ListType.GENERAL  -> "✅"
}

fun ListType.label(): String = when (this) {
    ListType.SHOPPING -> "Покупки"
    ListType.TRAVEL   -> "Поездка"
    ListType.EVENT    -> "Мероприятие"
    ListType.MOVING   -> "Переезд"
    ListType.GENERAL  -> "Общий"
}
