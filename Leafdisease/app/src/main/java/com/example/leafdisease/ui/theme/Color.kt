package com.example.leafdisease.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Green = Color(0xFF3F7D20)
val ForestMoss = Color(0xFF4C8A20)
val BrightFern = Color(0xFF70B426)
val YellowGreen = Color(0xFF93DD2C)
val LimeCream = Color(0xFFC3E691)
val Porcelain = Color(0xFFf9f7f3)
val LavenderGrey = Color(0xFF9C9CA5)
val CharcoalBlue = Color(0xFF454955)
val ShadowGrey = Color(0xFF292A30)
val Onyx = Color(0xFF0D0A0B)

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Silver = Color(0xFFbfc0c0)

fun leafLightColorScheme(): ColorScheme =
    lightColorScheme(
        primary = BrightFern,
        onPrimary = Onyx,
        primaryContainer = LimeCream,
        onPrimaryContainer = ForestMoss,
        secondary = CharcoalBlue,
        onSecondary = Porcelain,
        secondaryContainer = LavenderGrey,
        onSecondaryContainer = Onyx,
        tertiary = ForestMoss,
        onTertiary = Porcelain,
        tertiaryContainer = Green,
        onTertiaryContainer = LimeCream,
        background = Porcelain,
        onBackground = CharcoalBlue,
        surface = White,
        onSurface = Silver,
        surfaceVariant = LavenderGrey,
        onSurfaceVariant = ShadowGrey,
        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
        outline = LavenderGrey,
        outlineVariant = Color(0xFFCAC4D0),
        scrim = Color(0xFF000000),
        inverseSurface = ShadowGrey,
        inverseOnSurface = Porcelain,
        inversePrimary = YellowGreen,
        surfaceDim = LavenderGrey,
        surfaceBright = Color(0xFFF7F2FA),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Porcelain,
        surfaceContainer = Color(0xFFF3EDF7),
        surfaceContainerHigh = Color(0xFFECE6F0),
        surfaceContainerHighest = Color(0xFFE6E0E9)
    )

fun leafDarkColorScheme(): ColorScheme =
    darkColorScheme(
        primary = YellowGreen,
        onPrimary = Onyx,
        primaryContainer = ForestMoss,
        onPrimaryContainer = LimeCream,
        secondary = LavenderGrey,
        onSecondary = Onyx,
        secondaryContainer = CharcoalBlue,
        onSecondaryContainer = Porcelain,
        tertiary = LimeCream,
        onTertiary = ForestMoss,
        tertiaryContainer = Green,
        onTertiaryContainer = LimeCream,
        background = ShadowGrey,
        onBackground = Porcelain,
        surface = CharcoalBlue,
        onSurface = Porcelain,
        surfaceVariant = ShadowGrey,
        onSurfaceVariant = LavenderGrey,
        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),
        outline = LavenderGrey,
        outlineVariant = Color(0xFF49454F),
        scrim = Color(0xFF000000),
        inverseSurface = Porcelain,
        inverseOnSurface = ShadowGrey,
        inversePrimary = BrightFern,
        surfaceDim = Color(0xFF141218),
        surfaceBright = Color(0xFF3B383E),
        surfaceContainerLowest = Color(0xFF0F0D13),
        surfaceContainerLow = Color(0xFF1D1B20),
        surfaceContainer = Color(0xFF211F26),
        surfaceContainerHigh = Color(0xFF2B2930),
        surfaceContainerHighest = Color(0xFF36343B)
    ) 

