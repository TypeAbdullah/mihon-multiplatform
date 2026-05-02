package app.mihon.shared.domain.model

import kotlinx.serialization.Serializable

/**
 * Pure domain model — no Android/platform imports allowed here.
 */
@Serializable
data class Manga(
    val id: Long,
    val title: String,
    val thumbnailUrl: String,
    val description: String,
    val status: MangaStatus,
    val isFavorite: Boolean,
    val source: String,
    val lastUpdated: Long,
)

@Serializable
enum class MangaStatus {
    UNKNOWN,
    ONGOING,
    COMPLETED,
    LICENSED,
    PUBLISHING_FINISHED,
    CANCELLED,
    ON_HIATUS,
}
