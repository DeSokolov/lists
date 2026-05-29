package com.desokolov.lists.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
// ⚪ WHITE — текущая тема (без изменений)
// ─────────────────────────────────────────────

private val WhiteColors = lightColorScheme(
    primary             = Color(0xFF4052B5),
    onPrimary           = Color(0xFFFFFFFF),
    primaryContainer    = Color(0xFFDDE1FF),
    onPrimaryContainer  = Color(0xFF000F60),
    secondary           = Color(0xFF5C6BC0),
    onSecondary         = Color(0xFFFFFFFF),
    secondaryContainer  = Color(0xFFE8EAF6),
    onSecondaryContainer= Color(0xFF1A237E),
    error               = Color(0xFFBA1A1A),
    onError             = Color(0xFFFFFFFF),
    errorContainer      = Color(0xFFFFDAD6),
    onErrorContainer    = Color(0xFF410002),
    background          = Color(0xFFF8F9FF),
    onBackground        = Color(0xFF1B1B21),
    surface             = Color(0xFFF8F9FF),
    onSurface           = Color(0xFF1B1B21),
    surfaceVariant      = Color(0xFFE3E1EC),
    onSurfaceVariant    = Color(0xFF46464F),
    outline             = Color(0xFF777680),
    outlineVariant      = Color(0xFFC7C5D0),
)

// ─────────────────────────────────────────────
// ⚫ BLACK — тёмная, элегантная
//
// Построена на нейтрально-тёмном фоне #1C1B1F
// (Material 3 dark baseline, не чисто чёрный).
// Акцент осветлён до #BBC2FF — читаем на тёмном.
// Контейнеры инвертированы: насыщенные тёмные.
//
// Контрасты (WCAG AA, мин. 4.5:1):
//   onBackground #E5E1E6 / background #1C1B1F → ~12:1 ✓
//   primary #BBC2FF / background #1C1B1F       → ~8.5:1 ✓
//   onPrimaryContainer #DEE0FF / primaryContainer #2A3D99 → ~7.2:1 ✓
//   onSurfaceVariant #C8C5D0 / surfaceVariant #46464F     → ~5.1:1 ✓
// ─────────────────────────────────────────────

private val BlackColors = darkColorScheme(
    primary             = Color(0xFFBBC2FF),   // светлый индиго — читаем на тёмном
    onPrimary           = Color(0xFF0E1F78),   // тёмно-синий текст на светлом primary
    primaryContainer    = Color(0xFF2A3D99),   // насыщенный синий контейнер
    onPrimaryContainer  = Color(0xFFDEE0FF),   // светлый текст на тёмном контейнере

    secondary           = Color(0xFFBEC5EA),   // приглушённый лавандово-серый
    onSecondary         = Color(0xFF273577),
    secondaryContainer  = Color(0xFF3D4C8E),
    onSecondaryContainer= Color(0xFFDEE3FF),

    error               = Color(0xFFFFB4AB),
    onError             = Color(0xFF690005),
    errorContainer      = Color(0xFF93000A),
    onErrorContainer    = Color(0xFFFFDAD6),

    background          = Color(0xFF1C1B1F),   // нейтрально-тёмный, не #000000
    onBackground        = Color(0xFFE5E1E6),   // мягкий белый, не чисто белый

    surface             = Color(0xFF1C1B1F),
    onSurface           = Color(0xFFE5E1E6),

    surfaceVariant      = Color(0xFF46464F),   // карточки — чуть светлее фона
    onSurfaceVariant    = Color(0xFFC8C5D0),   // серый текст на карточках

    outline             = Color(0xFF918F9A),
    outlineVariant      = Color(0xFF46464F),
)

// ─────────────────────────────────────────────
// 🩷 PINK — тёплая, приятная
//
// Остаётся светлой (lightColorScheme).
// Акцент смещён с индиго к малиново-розовому #B5294A.
// Фон #FFF8F9 — едва уловимый розово-белый тинт
// (аналог лавандового #F8F9FF в WHITE-теме).
// Карточки #F2DCE1 — тёплый розово-серый.
//
// Контрасты (WCAG AA, мин. 4.5:1):
//   primary #B5294A / background #FFF8F9 → ~5.8:1 ✓
//   onBackground #211619 / background #FFF8F9 → ~17:1 ✓
//   onPrimaryContainer #3E0019 / primaryContainer #FFD9E2 → ~11:1 ✓
//   onSurfaceVariant #4E3A3F / surfaceVariant #F2DCE1 → ~5.6:1 ✓
// ─────────────────────────────────────────────

private val PinkColors = lightColorScheme(
    primary             = Color(0xFFB5294A),   // малиново-розовый акцент
    onPrimary           = Color(0xFFFFFFFF),
    primaryContainer    = Color(0xFFFFD9E2),   // бледно-розовый — FAB, активные chip
    onPrimaryContainer  = Color(0xFF3E0019),   // тёмно-бордовый текст

    secondary           = Color(0xFFC2446B),   // чуть мягче primary
    onSecondary         = Color(0xFFFFFFFF),
    secondaryContainer  = Color(0xFFFFD8E6),
    onSecondaryContainer= Color(0xFF3C0024),

    error               = Color(0xFFBA1A1A),
    onError             = Color(0xFFFFFFFF),
    errorContainer      = Color(0xFFFFDAD6),
    onErrorContainer    = Color(0xFF410002),

    background          = Color(0xFFFFF8F9),   // тёплый розово-белый тинт
    onBackground        = Color(0xFF211619),   // почти чёрный с тёплым оттенком

    surface             = Color(0xFFFFF8F9),
    onSurface           = Color(0xFF211619),

    surfaceVariant      = Color(0xFFF2DCE1),   // тёплый розово-серый — цвет карточек
    onSurfaceVariant    = Color(0xFF4E3A3F),   // приглушённый тёплый серый

    outline             = Color(0xFF7E5260),
    outlineVariant      = Color(0xFFE8C4CE),
)

// ─────────────────────────────────────────────
// Enum для выбора темы
// ─────────────────────────────────────────────

enum class ThemeType { WHITE, BLACK, PINK }

// ─────────────────────────────────────────────
// Composable-точка входа
// ─────────────────────────────────────────────

@Composable
fun ListsTheme(
    theme: ThemeType = ThemeType.WHITE,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (theme) {
        ThemeType.WHITE -> WhiteColors
        ThemeType.BLACK -> BlackColors
        ThemeType.PINK  -> PinkColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
