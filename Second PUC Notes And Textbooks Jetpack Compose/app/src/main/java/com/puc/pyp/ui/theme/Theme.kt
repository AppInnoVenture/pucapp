package com.puc.pyp.ui.theme

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.graphics.drawable.toDrawable

private val DarkColorScheme = darkColorScheme(
    primary = MainColorDark,
    secondary = ViewCardDark,
    tertiary = ViewLanDark,
    background = BgColorDark,
    surface = PureBlack,
    onPrimary = White,
    onSecondary = TextViewCardDark,
    onTertiary = TextViewLanDark,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = MainColorLight,
    secondary = ViewCardLight,
    tertiary = ViewLanLight,
    background = BgColorLight,
    surface = PureWhite,
    onPrimary = Black,
    onSecondary = TextViewCardLight,
    onTertiary = TextViewLanLight,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun PUCPassingPackageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
            window.setBackgroundDrawable(colorScheme.background.toArgb().toDrawable())
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}