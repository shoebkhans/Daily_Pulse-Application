package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class AppPalette(
    val primaryLight: Color,
    val primaryContainerLight: Color,
    val primaryDark: Color,
    val primaryContainerDark: Color,
    val label: String
)

val availablePalettes = mapOf(
    "sage" to AppPalette(
        primaryLight = SagePrimary,
        primaryContainerLight = SagePrimaryContainer,
        primaryDark = SagePrimaryDark,
        primaryContainerDark = SagePrimaryContainerDark,
        label = "Peaceful Sage"
    ),
    "ocean" to AppPalette(
        primaryLight = OceanPrimary,
        primaryContainerLight = OceanPrimaryContainer,
        primaryDark = OceanPrimaryDark,
        primaryContainerDark = OceanPrimaryContainerDark,
        label = "Ocean Calm"
    ),
    "sunset" to AppPalette(
        primaryLight = SunsetPrimary,
        primaryContainerLight = SunsetPrimaryContainer,
        primaryDark = SunsetPrimaryDark,
        primaryContainerDark = SunsetPrimaryContainerDark,
        label = "Sunset Amber"
    ),
    "lavender" to AppPalette(
        primaryLight = LavenderPrimary,
        primaryContainerLight = LavenderPrimaryContainer,
        primaryDark = LavenderPrimaryDark,
        primaryContainerDark = LavenderPrimaryContainerDark,
        label = "Lavender Bloom"
    ),
    "rose" to AppPalette(
        primaryLight = RosePrimary,
        primaryContainerLight = RosePrimaryContainer,
        primaryDark = RosePrimaryDark,
        primaryContainerDark = RosePrimaryContainerDark,
        label = "Rose Quartz"
    )
)

@Composable
fun DailyPulseTheme(
    themePreference: String = "system", // "light", "dark", "system"
    colorOption: String = "sage",       // "sage", "ocean", "sunset", "lavender", "rose"
    content: @Composable () -> Unit
) {
    val darkTheme = when (themePreference) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val palette = availablePalettes[colorOption] ?: availablePalettes["sage"]!!

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.primaryDark,
            onPrimary = Color(0xFF092913),
            primaryContainer = palette.primaryContainerDark,
            onPrimaryContainer = palette.primaryContainerLight,
            secondary = palette.primaryDark.copy(alpha = 0.8f),
            onSecondary = Color(0xFF1E281F),
            secondaryContainer = Color(0xFF3B483B),
            onSecondaryContainer = Color(0xFFDAE7D6),
            tertiary = Color(0xFFE4D2BF),
            onTertiary = Color(0xFF332718),
            tertiaryContainer = Color(0xFF4C3D2D),
            onTertiaryContainer = WarmSandLight,
            background = DarkBackground,
            onBackground = DarkText,
            surface = DarkSurface,
            onSurface = DarkText,
            surfaceVariant = DarkSurfaceVariant,
            onSurfaceVariant = DarkTextMuted,
            outline = Color(0xFF677066),
            outlineVariant = Color(0xFF3D463D)
        )
    } else {
        lightColorScheme(
            primary = palette.primaryLight,
            onPrimary = Color.White,
            primaryContainer = palette.primaryContainerLight,
            onPrimaryContainer = Color(0xFF0D2314),
            secondary = palette.primaryLight.copy(alpha = 0.85f),
            onSecondary = Color.White,
            secondaryContainer = palette.primaryContainerLight.copy(alpha = 0.6f),
            onSecondaryContainer = Color(0xFF131F14),
            tertiary = WarmSand,
            onTertiary = Color.White,
            tertiaryContainer = WarmSandLight,
            onTertiaryContainer = Color(0xFF281E10),
            background = LinenBackground,
            onBackground = TextDark,
            surface = LinenSurface,
            onSurface = TextDark,
            surfaceVariant = LinenSurfaceVariant,
            onSurfaceVariant = TextMuted,
            outline = Color(0xFFB0B9AE),
            outlineVariant = Color(0xFFD4DDD2)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
