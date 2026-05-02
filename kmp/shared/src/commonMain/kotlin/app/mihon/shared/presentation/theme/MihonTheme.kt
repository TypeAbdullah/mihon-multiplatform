package app.mihon.shared.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val md_primary         = Color(0xFF0877D2)
private val md_on_primary      = Color(0xFFFFFFFF)
private val md_primary_container = Color(0xFFD6E4FF)
private val md_secondary       = Color(0xFF545F71)
private val md_background      = Color(0xFFFAFAFA)
private val md_surface         = Color(0xFFFFFFFF)
private val md_error           = Color(0xFFBA1A1A)

private val LightColors = lightColorScheme(
    primary          = md_primary,
    onPrimary        = md_on_primary,
    primaryContainer = md_primary_container,
    secondary        = md_secondary,
    background       = md_background,
    surface          = md_surface,
    error            = md_error,
)

private val DarkColors = darkColorScheme(
    primary          = Color(0xFF9ECAFF),
    onPrimary        = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    secondary        = Color(0xFFBBC8DB),
    background       = Color(0xFF191C1E),
    surface          = Color(0xFF191C1E),
    error            = Color(0xFFFFB4AB),
)

@Composable
fun MihonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = MaterialTheme.typography,
        content     = content,
    )
}
