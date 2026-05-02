package app.mihon.shared.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.mihon.shared.filesystem.AppFileSystem
import java.io.File
import java.util.Properties

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // Store the database alongside other app data so it survives reinstalls.
        val dbDir = File(AppFileSystem.downloadsRoot.parent!!.toString())
        dbDir.mkdirs()
        val dbFile = File(dbDir, "mihon.db")

        // Capture BEFORE the driver opens/creates the file so the flag is correct.
        val isNewDatabase = !dbFile.exists()

        val driver = JdbcSqliteDriver(
            url        = "jdbc:sqlite:${dbFile.absolutePath}",
            properties = Properties().apply { put("foreign_keys", "true") },
        )

        // Only run schema creation on a brand-new database file.
        if (isNewDatabase) {
            MihonDatabase.Schema.create(driver)
        }

        return driver
    }
}
