package app.mihon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.mihon.shared.presentation.library.LibraryScreen
import app.mihon.shared.presentation.theme.MihonTheme
import cafe.adriel.voyager.navigator.Navigator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MihonTheme {
                // Voyager Navigator is the single entry point for all screens.
                Navigator(screen = LibraryScreen())
            }
        }
    }
}
