package com.example.clinicapypapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Mantén tus esquemas de color (DarkColorScheme y LightColorScheme) como están
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF000000),
    secondary = Color(0xFFE80F0F),
    tertiary = Color(0xFF00FF48),
    // ... otros colores ...
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF000000),
    secondary = Color(0xFFE80F0F),
    tertiary = Color(0xFF00FF48),
    surface = Color(0xFFE8F5E9),
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
    // ... otros colores ...
)

@Composable
fun ClinicaPyPAppTheme(
    // MODIFICACIÓN: Forzamos darkTheme a ser 'false' para usar siempre LightColorScheme
    // darkTheme: Boolean = isSystemInDarkTheme(), // <<== Comenta o elimina esta línea
    darkTheme: Boolean = false, // <<== Añade esta línea para forzar el tema claro
    dynamicColor: Boolean = false, // Puedes mantener dynamicColor si quieres, pero con tema fijo y sin S>=31 no hará nada
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Esta parte de dynamicColor seguirá funcionando si darkTheme = isSystemInDarkTheme() se usara,
        // pero al forzar darkTheme = false, siempre seleccionará el LightColorScheme (o dynamicLightColorScheme si dynamicColor=true y API >= 31).
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Si darkTheme es false (lo que estamos forzando), siempre entrará aquí
        darkTheme -> DarkColorScheme
        else -> LightColorScheme // <<== Siempre usará LightColorScheme al forzar darkTheme=false
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de que Typography está definida en Typography.kt
        content = content
    )
}