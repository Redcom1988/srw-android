package com.redcom1988.srw.theme

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.redcom1988.core.util.inject
import com.redcom1988.domain.preference.ApplicationPreference
import com.redcom1988.domain.theme.Themes
import com.redcom1988.srw.util.collectAsState

val Blue40  = Color(0xFF1A8FE3)
val Blue20  = Color(0xFF0A5FA3)
val Blue80  = Color(0xFF9ECFFA)
val Blue90  = Color(0xFFCBE6FF)

val BlueGrey30 = Color(0xFF2D5068)
val BlueGrey80 = Color(0xFFB0CAE0)

private val LightColorScheme = lightColorScheme(
    primary              = Blue40,
    onPrimary            = Color(0xFFFFFFFF),
    primaryContainer     = Color(0xFFD6EEFF),
    onPrimaryContainer   = Color(0xFF003258),

    secondary            = Color(0xFF3B7DB4),
    onSecondary          = Color(0xFFFFFFFF),
    secondaryContainer   = Color(0xFFD4E8FF),
    onSecondaryContainer = Color(0xFF002D4E),

    tertiary             = Color(0xFF006878),
    onTertiary           = Color(0xFFFFFFFF),
    tertiaryContainer    = Color(0xFFB2EBFF),
    onTertiaryContainer  = Color(0xFF001F26),

    error                = Color(0xFFBA1A1A),
    onError              = Color(0xFFFFFFFF),
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF410002),

    background           = Color(0xFFF4F6FA),
    onBackground         = Color(0xFF1A1C1E),

    surface              = Color(0xFFFFFFFF),
    onSurface            = Color(0xFF1A1C1E),
    surfaceVariant       = Color(0xFFE3EAF2),
    onSurfaceVariant     = Color(0xFF42474E),

    outline              = Color(0xFF72787E),
    outlineVariant       = Color(0xFFC2C8CE),
    inverseSurface       = Color(0xFF2E3133),
    inverseOnSurface     = Color(0xFFF0F1F3),
    inversePrimary       = Color(0xFF92CCFF),

    scrim                = Color(0xFF000000),
    surfaceTint          = Blue40,
)

private val DarkColorScheme = darkColorScheme(
    primary              = Blue20,
    onPrimary            = Color(0xFFFFFFFF),
    primaryContainer     = Color(0xFF004880),
    onPrimaryContainer   = Color(0xFFD1E4FF),

    secondary            = Color(0xFF89B4D8),
    onSecondary          = Color(0xFF003354),
    secondaryContainer   = Color(0xFF004A78),
    onSecondaryContainer = Color(0xFFCBE6FF),

    tertiary             = Color(0xFF5DD5EC),
    onTertiary           = Color(0xFF003640),
    tertiaryContainer    = Color(0xFF004E5C),
    onTertiaryContainer  = Color(0xFFB2EBFF),

    error                = Color(0xFFFFB4AB),
    onError              = Color(0xFF690005),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFFDAD6),

    background           = Color(0xFF0F1214),
    onBackground         = Color(0xFFE2E2E5),

    surface              = Color(0xFF1A1E22),
    onSurface            = Color(0xFFE2E2E5),
    surfaceVariant       = Color(0xFF2A3038),
    onSurfaceVariant     = Color(0xFFC2C8CE),

    outline              = Color(0xFF8C9198),
    outlineVariant       = Color(0xFF42474E),
    inverseSurface       = Color(0xFFE2E2E5),
    inverseOnSurface     = Color(0xFF2E3133),
    inversePrimary       = Blue40,

    scrim                = Color(0xFF000000),
    surfaceTint          = Blue20,
)

val LocalColorScheme = compositionLocalOf { lightColorScheme() }
val darkTheme = DarkColorScheme
val lightTheme = LightColorScheme

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val applicationPreference = inject<ApplicationPreference>()
    val theme by applicationPreference.appTheme().collectAsState()
    val systemBarColor = when (theme) {
        Themes.DARK -> Color.Transparent
        Themes.LIGHT -> Color.White
        else -> if (isDarkTheme) Color.Transparent else Color.White
    }

    val colorScheme = when (theme) {
        Themes.DARK -> darkTheme
        Themes.LIGHT -> lightTheme
        else -> if (isDarkTheme) darkTheme else lightTheme
    }
    CompositionLocalProvider(
        LocalColorScheme provides colorScheme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background),
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content,
            )
            StatusBarColor(systemBarColor, colorScheme)
            NavigationBarColor(systemBarColor, colorScheme)
        }
    }
}

@Suppress("DEPRECATION")
@Composable
fun NavigationBarColor(color: Color, colorScheme: ColorScheme) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = color.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val windowInsetsController = WindowInsetsControllerCompat(window, view)
            windowInsetsController.isAppearanceLightNavigationBars = colorScheme == lightTheme
        }
    }
}

@Suppress("DEPRECATION")
@Composable
fun StatusBarColor(color: Color, colorScheme: ColorScheme) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = color.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val windowInsetsController = WindowInsetsControllerCompat(window, view)
            windowInsetsController.isAppearanceLightStatusBars = colorScheme == lightTheme
        }
    }
}
