package app.mihon.shared.filesystem

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * Desktop: follows OS-specific XDG / Windows conventions.
 *
 *  Windows → %APPDATA%\MihonKMP\downloads
 *  Linux   → $XDG_DATA_HOME/MihonKMP/downloads  (fallback: ~/.local/share)
 *  macOS   → ~/Library/Application Support/MihonKMP/downloads
 */
actual object AppFileSystem {
    actual val fileSystem: FileSystem = FileSystem.SYSTEM

    actual val downloadsRoot: Path by lazy {
        val base: String = when {
            isWindows() -> System.getenv("APPDATA")
                ?: "${System.getProperty("user.home")}/AppData/Roaming"

            isMac()     -> "${System.getProperty("user.home")}/Library/Application Support"

            else        -> System.getenv("XDG_DATA_HOME")
                ?: "${System.getProperty("user.home")}/.local/share"
        }
        "$base/MihonKMP/downloads".toPath()
    }

    private fun isWindows() = System.getProperty("os.name")
        ?.lowercase()?.contains("windows") == true

    private fun isMac() = System.getProperty("os.name")
        ?.lowercase()?.contains("mac") == true
}
