package com.neworesearchgroup.bemarkalarm.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BemarkColorScheme = lightColorScheme(

    primary = BemarkPrimary,
    onPrimary = Color.White,

    primaryContainer = BemarkPrimaryLight,
    onPrimaryContainer = Color.White,

    secondary = BemarkTextSecondary,
    onSecondary = Color.White,

    background = BemarkBackground,
    onBackground = BemarkTextPrimary,

    surface = BemarkSurface,
    onSurface = BemarkTextPrimary,

    surfaceVariant = BemarkSurfaceVariant,
    onSurfaceVariant = BemarkTextSecondary,

    error = BemarkDanger,
    onError = Color.White,

    outline = BemarkBorder
)

@Composable
fun BemarkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BemarkColorScheme,
        typography = BemarkTypography,
        content = content
    )
}