package app.mihon.shared.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DatabaseDriverFactory : KoinComponent {
    private val context: Context by inject()

    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(
            schema   = MihonDatabase.Schema,
            context  = context,
            name     = "mihon.db",
        )
}
