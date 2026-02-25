package com.leafdisease.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = leafDarkColorScheme()

private val LightColorScheme = leafLightColorScheme()

@Composable
fun LeafDiseaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LeafTypography,
        content = content
    )
}

/**
 * Object to access theme properties directly.
 * The colors adapt to the current theme (light/dark).
 */
object LeafDiseaseTheme {
    val colors: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme
    val dimensions: Dimensions
        get() = AppDimensions
    val typography: Typography
        @Composable
        get() = MaterialTheme.typography
    val customTypography: CustomTypography
        get() = CustomLeafTypography
}
