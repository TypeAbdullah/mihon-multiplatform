package app.mihon.shared.data.network.dto

import app.mihon.shared.domain.model.Manga
import app.mihon.shared.domain.model.MangaStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO returned by the remote API.
 * Mapped to the clean domain [Manga] model before reaching the repository.
 */
@Serializable
data class MangaDto(
    @SerialName("id")           val id: Long,
    @SerialName("title")        val title: String,
    @SerialName("thumbnail")    val thumbnailUrl: String     = "",
    @SerialName("description")  val description: String      = "",
    @SerialName("status")       val status: String           = "UNKNOWN",
    @SerialName("source")       val source: String           = "",
    @SerialName("last_updated") val lastUpdated: Long        = 0L,
)

fun MangaDto.toDomain(): Manga = Manga(
    id           = id,
    title        = title,
    thumbnailUrl = thumbnailUrl,
    description  = description,
    status       = runCatching { MangaStatus.valueOf(status.uppercase()) }
                        .getOrDefault(MangaStatus.UNKNOWN),
    isFavorite   = false,
    source       = source,
    lastUpdated  = lastUpdated,
)
