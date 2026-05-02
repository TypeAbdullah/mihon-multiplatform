package app.mihon.shared.di

import app.mihon.shared.data.db.DatabaseDriverFactory
import app.mihon.shared.data.db.MangaDatabaseHelper
import app.mihon.shared.data.network.buildMangaHttpClient
import app.mihon.shared.data.repository.MangaRepositoryImpl
import app.mihon.shared.domain.repository.MangaRepository
import app.mihon.shared.presentation.library.LibraryScreenModel
import org.koin.dsl.module

/**
 * Single Koin module for the entire shared layer.
 * Platform-specific modules can extend this with their own bindings
 * (e.g. Android's Context) before calling startKoin { }.
 */
val sharedModule = module {

    // ── Networking ───────────────────────────────────────────────────────────
    single {
        buildMangaHttpClient(enableLogging = true)
    }

    // ── Database ─────────────────────────────────────────────────────────────
    single { DatabaseDriverFactory() }
    single { MangaDatabaseHelper(get()) }

    // ── Repository ───────────────────────────────────────────────────────────
    single<MangaRepository> {
        MangaRepositoryImpl(
            httpClient = get(),
            db         = get(),
        )
    }

    // ── Presentation ─────────────────────────────────────────────────────────
    factory { LibraryScreenModel(get()) }
}
