package app.mihon.shared.presentation.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import coil3.compose.AsyncImage

/**
 * Voyager [Screen] — no Android Activity or UIViewController reference here.
 * The Voyager Navigator handles back-stack and transitions on every platform.
 */
class LibraryScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<LibraryScreenModel>()
        val state by screenModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        // Show errors in a Snackbar and then dismiss them from state.
        LaunchedEffect(state.error) {
            state.error?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                screenModel.dismissError()
            }
        }

        LibraryContent(
            state            = state,
            snackbarHostState = snackbarHostState,
            onRefresh        = screenModel::refresh,
            onToggleFavorites = screenModel::toggleFavorites,
            onFavoriteClick  = { manga ->
                screenModel.setFavorite(manga.id, !manga.isFavorite)
            },
        )
    }
}

// ── Stateless UI ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryContent(
    state: LibraryState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onToggleFavorites: () -> Unit,
    onFavoriteClick: (app.mihon.shared.domain.model.Manga) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Library") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                actions = {
                    IconButton(onClick = onToggleFavorites) {
                        Icon(
                            imageVector = if (state.showFavoritesOnly) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FilterList
                            },
                            contentDescription = "Toggle favourites filter",
                        )
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading && state.manga.isEmpty() -> LoadingIndicator()
            else -> MangaGrid(
                manga           = state.manga,
                contentPadding  = innerPadding,
                onFavoriteClick = onFavoriteClick,
            )
        }
    }
}

/**
 * Adaptive grid: narrow screens (phones) get 2 columns,
 * wider screens (tablets, desktops) get more columns automatically.
 *
 * [GridCells.Adaptive] with a min size of 160.dp means:
 *   – A 360 dp phone  → 2 columns
 *   – A 768 dp tablet → 4 columns
 *   – A 1280 dp desktop window → 8 columns
 * Mouse-scroll on desktop works out of the box because LazyVerticalGrid
 * delegates to the platform's scroll physics (Swing on JVM).
 */
@Composable
private fun MangaGrid(
    manga: List<app.mihon.shared.domain.model.Manga>,
    contentPadding: PaddingValues,
    onFavoriteClick: (app.mihon.shared.domain.model.Manga) -> Unit,
) {
    LazyVerticalGrid(
        columns            = GridCells.Adaptive(minSize = 160.dp),
        contentPadding     = PaddingValues(
            start  = 8.dp,
            end    = 8.dp,
            top    = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 8.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement   = Arrangement.spacedBy(8.dp),
        modifier              = Modifier.fillMaxSize(),
    ) {
        items(
            items = manga,
            key   = { it.id },
        ) { item ->
            MangaCard(manga = item, onFavoriteClick = onFavoriteClick)
        }
    }
}

@Composable
private fun MangaCard(
    manga: app.mihon.shared.domain.model.Manga,
    onFavoriteClick: (app.mihon.shared.domain.model.Manga) -> Unit,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier  = Modifier.fillMaxWidth(),
    ) {
        Box {
            // Thumbnail — Coil3 handles caching on all platforms via Ktor.
            AsyncImage(
                model             = manga.thumbnailUrl,
                contentDescription = manga.title,
                contentScale      = ContentScale.Crop,
                modifier          = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
            )

            // Favourite toggle overlaid on top-right of the cover.
            IconButton(
                onClick  = { onFavoriteClick(manga) },
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                Icon(
                    imageVector = if (manga.isFavorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = if (manga.isFavorite) "Remove from library" else "Add to library",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // Title below the cover.
        Text(
            text     = manga.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier.fillMaxSize(),
    ) {
        CircularProgressIndicator()
    }
}
