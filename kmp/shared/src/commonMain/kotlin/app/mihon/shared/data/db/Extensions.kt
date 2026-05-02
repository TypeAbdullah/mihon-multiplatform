package app.mihon.shared.data.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

/**
 * Convenience wrapper: collects a SQLDelight [Query] as a [Flow] of lists
 * on the IO dispatcher — keeps the UI thread free.
 *
 * Note: we do NOT re-declare asFlow() here. We delegate directly to
 * SQLDelight's own extension to avoid infinite recursion.
 */
fun <T : Any> Query<T>.asListFlow(): Flow<List<T>> =
    this.asFlow().mapToList(Dispatchers.IO)
