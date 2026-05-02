package app.mihon.shared.data.db

import app.cash.sqldelight.db.SqlDriver

/**
 * expect declaration — each platform provides its own SqlDriver implementation.
 * commonMain must never reference SqliteDriver, AndroidSqliteDriver, etc. directly.
 */
expect class DatabaseDriverFactory() {
    fun createDriver(): SqlDriver
}
