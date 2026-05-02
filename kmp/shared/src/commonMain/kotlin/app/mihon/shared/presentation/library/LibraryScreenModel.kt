package app.mihon.shared.presentation.library

import app.mihon.shared.domain.model.Manga
import app.mihon.shared.domain.repository.MangaRepository
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── State ─────────────────────────────────────────────────────────────────────

data class LibraryState(
    val isLoading: Boolean         = true,
    val manga: List<Manga>         = emptyList(),
    val error: String?             = null,
    val showFavoritesOnly: Boolean = false,
)

// ── ScreenModel ───────────────────────────────────────────────────────────────

/**
 * Voyager [ScreenModel] is the KMP-safe ViewModel equivalent.
 * It is scoped to its Screen and cancelled automatically when the screen leaves
 * the back-stack — no lifecycle boilerplate needed.
 */
class LibraryScreenModel(
    private val repository: MangaRepository,
) : ScreenModel {

    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state

    // Track the active collection job so we can cancel it before restarting.
    private var collectionJob: Job? = null

    init {
        observeLibrary()
    }

    // ── Public API ────────────────────────────────────────────────────────────

    fun refresh(sourceUrl: String = DEFAULT_SOURCE_URL) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.fetchAndCacheManga(sourceUrl) }
                .onFailure { t ->
                    _state.update { it.copy(isLoading = false, error = t.message) }
                }
        }
    }

    fun toggleFavorites() {
        _state.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
        // Cancel the previous collector before starting a new one.
        observeLibrary()
    }

    fun setFavorite(id: Long, favorite: Boolean) {
        screenModelScope.launch {
            repository.setFavorite(id, favorite)
        }
    }

    fun dismissError() = _state.update { it.copy(error = null) }

    // ── Private ───────────────────────────────────────────────────────────────

    private fun observeLibrary() {
        // Cancel any existing collector to avoid duplicate emissions.
        collectionJob?.cancel()
        collectionJob = screenModelScope.launch {
            val flow = if (_state.value.showFavoritesOnly) {
                repository.getFavoritesFlow()
            } else {
                repository.getAllMangaFlow()
            }

            flow
                .catch { t -> _state.update { it.copy(error = t.message, isLoading = false) } }
                .collect { list ->
                    _state.update { it.copy(manga = list, isLoading = false) }
                }
        }
    }

    companion object {
        // Replace with a real API endpoint or extension source URL.
        private const val DEFAULT_SOURCE_URL = "https://api.example.com/manga"
    }
}
