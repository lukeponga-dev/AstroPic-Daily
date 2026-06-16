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
      primary = AccentPrimaryDark,
      secondary = AccentSecondaryDark,
      tertiary = AccentTertiaryDark,
      background = DarkBackground,
      surface = DarkSurface,
      onPrimary = Color.Black,
      onSecondary = Color.Black,
      onTertiary = Color.White,
      onBackground = Color.White,
      onSurface = Color.White,
      surfaceVariant = BorderMutedDark,
      onSurfaceVariant = TextMutedDark,
      error = ErrorColor,
      onError = Color.White,
      errorContainer = Color(0xFF450A0A),
      onErrorContainer = Color(0xFFFECACA)
  )

private val LightColorScheme =
  lightColorScheme(
      primary = AccentPrimaryLight,
      secondary = AccentSecondaryLight,
      tertiary = AccentTertiaryLight,
      background = LightBackground,
      surface = LightSurface,
      onPrimary = Color.White,
      onSecondary = Color.White,
      onTertiary = Color.Black,
      onBackground = Color.Black,
      onSurface = Color.Black,
      surfaceVariant = BorderMutedLight,
      onSurfaceVariant = TextMutedLight,
      error = ErrorColor,
      onError = Color.White,
      errorContainer = Color(0xFFFEF2F2),
      onErrorContainer = Color(0xFF991B1B)
  )

@Composable
fun NasaDailyTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Disable dynamic colors to showcase cosmic theme
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
