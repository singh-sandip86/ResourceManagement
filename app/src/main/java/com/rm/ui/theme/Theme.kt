package com.rm.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// private val DarkColorPalette = darkColors(
//    primary = Purple200,
//    primaryVariant = Purple700,
//    secondary = Teal200
// )

data class Shape(
    val default: RoundedCornerShape = RoundedCornerShape(0.dp),
    val small: RoundedCornerShape = RoundedCornerShape(4.dp),
    val medium: RoundedCornerShape = RoundedCornerShape(4.dp),
    val large: RoundedCornerShape = RoundedCornerShape(0.dp)
)

val LocalShape = compositionLocalOf { Shape() }

val MaterialTheme.shapeScheme: Shape
    @Composable
    @ReadOnlyComposable
    get() = LocalShape.current

private val darkColorScheme = darkColorScheme(
    primary = Purple200,
    secondary = Purple700,
    primaryContainer = Teal200,
    tertiary = Teal200,
    surface = Purple700,
    onSurface = Teal200,
    onSurfaceVariant = Teal200,
    background = Teal200
)

private val lightColorScheme = lightColorScheme(
    primary = Purple500,
    secondary = Purple700,
    tertiary = Teal200,
)

@Composable
fun ResourceManagementTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val appColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> lightColorScheme
        else -> lightColorScheme
    }
    val view = LocalView.current

//    if (!view.isInEditMode) {
//        /* getting the current window by tapping into the Activity */
//        val currentWindow = (view.context as? Activity)?.window
//            ?: throw Exception("Not in an activity - unable to get Window reference")
//
//        SideEffect {
//            /* the default code did the same cast here - might as well use our new variable! */
//            currentWindow.statusBarColor = appColorScheme.primaryContainer.toArgb() //.toArgb()
//            /* accessing the insets controller to change appearance of the status bar, with 100% less deprecation warnings */
//            WindowCompat.getInsetsController(currentWindow, view).isAppearanceLightStatusBars =
//                darkTheme
//        }
//    }

    MaterialTheme(
        // colors = colors,
        colorScheme = appColorScheme,
        typography = Typography,
        // shapes = Shapes,
        content = content
    )
}
