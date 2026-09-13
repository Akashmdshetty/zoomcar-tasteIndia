package com.example.tasteindia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SpicePrimaryFixed,
    onPrimary = SpiceOnPrimaryFixed,
    primaryContainer = SpicePrimaryContainer,
    onPrimaryContainer = SpiceOnPrimaryContainer,
    secondary = SpiceSecondaryContainer,
    onSecondary = SpiceSecondaryFixedVariant,
    secondaryContainer = SpiceSecondaryContainer,
    onSecondaryContainer = SpiceOnSecondaryContainer,
    tertiary = SpiceTertiaryFixed,
    onTertiary = SpiceTertiaryFixedVariant,
    background = SpiceOnSurface,
    onBackground = SpiceSurface,
    surface = SpiceOnSurface,
    onSurface = SpiceSurface,
    surfaceVariant = SpiceOnSurfaceVariant,
    onSurfaceVariant = SpiceSurfaceContainerHigh,
    error = SpiceError,
    onError = SpiceOnError,
    errorContainer = SpiceErrorContainer,
    onErrorContainer = SpiceOnErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = SpicePrimary,
    onPrimary = SpiceOnPrimary,
    primaryContainer = SpicePrimaryContainer,
    onPrimaryContainer = SpiceOnPrimaryContainer,
    secondary = SpiceSecondary,
    onSecondary = SpiceOnSecondary,
    secondaryContainer = SpiceSecondaryContainer,
    onSecondaryContainer = SpiceOnSecondaryContainer,
    tertiary = SpiceTertiary,
    onTertiary = SpiceOnTertiary,
    tertiaryContainer = SpiceTertiaryContainer,
    onTertiaryContainer = SpiceOnPrimaryContainer,
    background = SpiceSurface,
    onBackground = SpiceOnSurface,
    surface = SpiceSurface,
    onSurface = SpiceOnSurface,
    surfaceVariant = SpiceSurfaceContainerHigh,
    onSurfaceVariant = SpiceOnSurfaceVariant,
    outline = SpiceOutline,
    outlineVariant = SpiceOutlineVariant,
    error = SpiceError,
    onError = SpiceOnError,
    errorContainer = SpiceErrorContainer,
    onErrorContainer = SpiceOnErrorContainer
)

@Composable
fun TasteIndiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our curated warm spice palette for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
