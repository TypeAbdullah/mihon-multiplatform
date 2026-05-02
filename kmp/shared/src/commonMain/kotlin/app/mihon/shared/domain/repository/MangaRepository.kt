package app.mihon.shared.domain.repository

import app.mihon.shared.domain.model.Manga
import kotlinx.coroutines.flow.Flow

/**
 * Pure domain interface — no implementation details leak into this layer.
 */
interface MangaRepository {
    fun getAllMangaFlow(): Flow<List<Manga>>
    fun getFavoritesFlow(): Flow<List<Manga>>
    suspend fun fetchAndCacheManga(sourceUrl: String)
    suspend fun setFavorite(id: Long, favorite: Boolean)
    suspend fun getMangaById(id: Long): Manga?
}
