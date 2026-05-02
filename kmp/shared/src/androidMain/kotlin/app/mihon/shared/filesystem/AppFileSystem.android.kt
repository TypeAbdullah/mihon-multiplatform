package app.mihon.shared.filesystem

import android.content.Context
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Android: uses the app-specific external files directory.
 * No WRITE_EXTERNAL_STORAGE permission needed on API 29+.
 * Falls back to internal files dir if external storage is unavailable.
 */
actual object AppFileSystem : KoinComponent {
    private val context: Context by inject()

    actual val fileSystem: FileSystem
        get() = FileSystem.SYSTEM

    actual val downloadsRoot: Path
        get() {
            val dir = context.getExternalFilesDir("downloads")
                ?: context.filesDir.resolve("downloads")
            return dir.toOkioPath()
        }
}
