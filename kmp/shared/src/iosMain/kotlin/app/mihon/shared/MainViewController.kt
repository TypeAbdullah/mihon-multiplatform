package app.mihon.shared

import androidx.compose.ui.window.ComposeUIViewController
import app.mihon.shared.di.sharedModule
import app.mihon.shared.presentation.library.LibraryScreen
import app.mihon.shared.presentation.theme.MihonTheme
import cafe.adriel.voyager.navigator.Navigator
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.GlobalContext
import platform.UIKit.UIViewController

/**
 * Called from Swift/Objective-C in the iOS Xcode project.
 *
 * In your ContentView.swift:
 *   struct ComposeView: UIViewControllerRepresentable {
 *       func makeUIViewController(context: Context) -> UIViewController {
 *           MainViewControllerKt.MainViewController()
 *       }
 *       func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
 *   }
 *
 * Guard against duplicate startKoin() calls — iOS can recreate the
 * UIViewController (e.g. during hot-reload or scene restoration), which
 * would throw KoinApplicationAlreadyStartedException without this check.
 */
fun MainViewController(): UIViewController {
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(sharedModule)
        }
    }

    return ComposeUIViewController {
        MihonTheme {
            Navigator(screen = LibraryScreen())
        }
    }
}
