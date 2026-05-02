package app.mihon.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.mihon.shared.di.sharedModule
import app.mihon.shared.presentation.library.LibraryScreen
import app.mihon.shared.presentation.theme.MihonTheme
import cafe.adriel.voyager.navigator.Navigator
import org.koin.core.context.startKoin

fun main() {
    // Initialise Koin before any composable accesses it.
    startKoin {
        modules(sharedModule)
    }

    application {
        val windowState = rememberWindowState(
            size = DpSize(width = 1280.dp, height = 800.dp),
        )

        Window(
            onCloseRequest = ::exitApplication,
            title          = "Mihon",
            state          = windowState,
        ) {
            MihonTheme {
                Navigator(screen = LibraryScreen())
            }
        }
    }
}
