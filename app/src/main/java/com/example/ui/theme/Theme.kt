package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
      primary = CoolTealDark,
      secondary = StarAmberDark,
      tertiary = NebulaRoseDark,
      background = DarkBackground,
      surface = DarkSurface,
      onPrimary = Color(0xFF090414),
      onSecondary = Color(0xFF090414),
      onTertiary = Color(0xFF090414),
      onBackground = Color(0xFFFAF5FF),
      onSurface = Color(0xFFFAF5FF),
      surfaceVariant = BorderMutedDark,
      onSurfaceVariant = TextMutedDark,
      error = ErrorColor,
      onError = Color.White,
      errorContainer = Color(0xFF881337),
      onErrorContainer = Color(0xFFFFD1D9)
  )

private val LightColorScheme =
  lightColorScheme(
      primary = CoolTealLight,
      secondary = StarAmberLight,
      tertiary = NebulaRoseLight,
      background = LightBackground,
      surface = LightSurface,
      onPrimary = Color.White,
      onSecondary = Color.White,
      onTertiary = Color.White,
      onBackground = Color(0xFF1E1045),
      onSurface = Color(0xFF1E1045),
      surfaceVariant = BorderMutedLight,
      onSurfaceVariant = TextMutedLight,
      error = ErrorColor,
      onError = Color.White,
      errorContainer = Color(0xFFFFE4E6),
      onErrorContainer = Color(0xFF9F1239)
  )

@Composable
fun NasaDailyTheme(
  darkTheme: Boolean = isSystemInDarkTheme(), // Follow system theme setting for modern responsiveness
  dynamicColor: Boolean = false, // We'll keep our hand-crafted clean layout, but users can toggle
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
