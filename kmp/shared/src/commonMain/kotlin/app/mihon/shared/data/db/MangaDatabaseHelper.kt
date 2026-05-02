package app.mihon.shared.data.db

import app.mihon.shared.domain.model.Manga
import app.mihon.shared.domain.model.MangaStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Wraps the generated SQLDelight [MihonDatabase] with higher-level helpers
 * and maps between the DB entity type and the clean domain [Manga] model.
 */
class MangaDatabaseHelper(driverFactory: DatabaseDriverFactory) {

    private val database: MihonDatabase = MihonDatabase(driverFactory.createDriver())
    private val queries = database.mangaEntityQueries

    // ── Queries ──────────────────────────────────────────────────────────────

    fun getAllMangaFlow(): Flow<List<Manga>> =
        queries.selectAll()
            .asListFlow()                          // uses the non-recursive extension
            .map { list -> list.map { it.toDomain() } }

    fun getFavoritesFlow(): Flow<List<Manga>> =
        queries.selectFavorites()
            .asListFlow()
            .map { list -> list.map { it.toDomain() } }

    fun getMangaById(id: Long): Manga? =
        queries.selectById(id).executeAsOneOrNull()?.toDomain()

    // ── Mutations ────────────────────────────────────────────────────────────

    fun insertManga(manga: Manga) {
        queries.insert(
            id           = manga.id.takeIf { it != 0L },
            title        = manga.title,
            thumbnailUrl = manga.thumbnailUrl,
            description  = manga.description,
            status       = manga.status.name,
            isFavorite   = if (manga.isFavorite) 1L else 0L,
            source       = manga.source,
            lastUpdated  = manga.lastUpdated,
        )
    }

    fun insertAll(manga: List<Manga>) = manga.forEach(::insertManga)

    fun setFavorite(id: Long, favorite: Boolean) {
        queries.updateFavorite(
            id         = id,
            isFavorite = if (favorite) 1L else 0L,
        )
    }

    fun deleteManga(id: Long) = queries.deleteById(id)

    fun deleteAll() = queries.deleteAll()
}

// ── Mapper ───────────────────────────────────────────────────────────────────

private fun MangaEntity.toDomain(): Manga = Manga(
    id           = id,
    title        = title,
    thumbnailUrl = thumbnailUrl,
    description  = description,
    status       = runCatching { MangaStatus.valueOf(status) }.getOrDefault(MangaStatus.UNKNOWN),
    isFavorite   = isFavorite == 1L,
    source       = source,
    lastUpdated  = lastUpdated,
)
