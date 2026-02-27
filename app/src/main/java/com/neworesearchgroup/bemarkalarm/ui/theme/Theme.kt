package com.neworesearchgroup.bemarkalarm.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BemarkColorScheme = lightColorScheme(

    primary = BemarkBlue,
    onPrimary = Color.White,

    background = BemarkBackground,
    onBackground = BemarkTextPrimary,

    surface = BemarkSurface,
    onSurface = BemarkTextPrimary,

    error = BemarkDanger,
    onError = Color.White
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