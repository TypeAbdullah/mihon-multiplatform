package app.mihon.shared.filesystem

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

/**
 * iOS: stores downloads under <Application Support>/MihonKMP/downloads.
 * Application Support is backed up by iCloud by default.
 */
actual object AppFileSystem {
    actual val fileSystem: FileSystem = FileSystem.SYSTEM

    actual val downloadsRoot: Path by lazy {
        val appSupport = NSSearchPathForDirectoriesInDomains(
            directory    = NSApplicationSupportDirectory,
            domainMask   = NSUserDomainMask,
            expandTilde  = true,
        ).first() as String

        "$appSupport/MihonKMP/downloads".toPath()
    }
}
