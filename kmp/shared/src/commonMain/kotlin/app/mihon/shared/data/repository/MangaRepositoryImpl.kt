package app.mihon.shared.data.repository

import app.mihon.shared.data.db.MangaDatabaseHelper
import app.mihon.shared.data.network.dto.MangaDto
import app.mihon.shared.data.network.dto.toDomain
import app.mihon.shared.domain.model.Manga
import app.mihon.shared.domain.repository.MangaRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow

class MangaRepositoryImpl(
    private val httpClient: HttpClient,
    private val db: MangaDatabaseHelper,
) : MangaRepository {

    override fun getAllMangaFlow(): Flow<List<Manga>> = db.getAllMangaFlow()

    override fun getFavoritesFlow(): Flow<List<Manga>> = db.getFavoritesFlow()

    /**
     * Fetches manga from the remote source and caches them in the local DB.
     * Replace the URL with a real endpoint or extension source URL.
     */
    override suspend fun fetchAndCacheManga(sourceUrl: String) {
        val dtos = httpClient.get(sourceUrl).body<List<MangaDto>>()
        db.insertAll(dtos.map { it.toDomain() })
    }

    override suspend fun setFavorite(id: Long, favorite: Boolean) {
        db.setFavorite(id, favorite)
    }

    override suspend fun getMangaById(id: Long): Manga? = db.getMangaById(id)
}
