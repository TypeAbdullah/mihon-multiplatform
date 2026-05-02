package app.mihon.shared.filesystem

import okio.FileSystem
import okio.Path

/**
 * expect: each platform returns the correct root directory for
 * storing downloaded chapters without any Context/UIKit references
 * leaking into commonMain.
 *
 * Windows → %APPDATA%\MihonKMP\downloads
 * Linux   → ~/.local/share/MihonKMP/downloads
 * Android → app-specific external files dir  (no WRITE_EXTERNAL_STORAGE needed on API 29+)
 * iOS     → <Application Support>/MihonKMP/downloads
 */
expect object AppFileSystem {
    val fileSystem: FileSystem
    val downloadsRoot: Path
}

/**
 * Ensures the downloads directory exists before writing to it.
 */
fun AppFileSystem.ensureDownloadsDirExists() {
    fileSystem.createDirectories(downloadsRoot)
}

/**
 * Returns the path for a given manga's chapter downloads.
 *
 * e.g. <downloads_root>/<sanitised_manga_title>/<chapter_number>/
 */
fun AppFileSystem.chapterPath(mangaTitle: String, chapterNumber: Float): Path =
    downloadsRoot /
        mangaTitle.sanitiseForPath() /
        "ch_${chapterNumber.toInt().toString().padStart(4, '0')}"

// ── Helpers ──────────────────────────────────────────────────────────────────

private fun String.sanitiseForPath(): String =
    replace(Regex("[\\\\/:*?\"<>|]"), "_").trim()
