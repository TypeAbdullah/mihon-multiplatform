package app.mihon.shared.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * expect: each platform supplies the correct engine factory so
 * commonMain stays free of any engine-specific imports.
 */
expect fun httpClientEngineFactory(): HttpClientEngineFactory<*>

/**
 * Shared HTTP client configuration.
 * Call [buildMangaHttpClient] from the DI module.
 */
fun buildMangaHttpClient(enableLogging: Boolean = false): HttpClient =
    HttpClient(httpClientEngineFactory()) {
        installContentNegotiation()
        if (enableLogging) installLogging()
    }

// ── Private helpers ───────────────────────────────────────────────────────────

private fun HttpClientConfig<*>.installContentNegotiation() {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient         = true
                encodeDefaults    = true
                prettyPrint       = false
            },
        )
    }
}

private fun HttpClientConfig<*>.installLogging() {
    install(Logging) {
        // Logger.SIMPLE is a companion property — no separate top-level import needed.
        logger = Logger.SIMPLE
        level  = LogLevel.HEADERS
    }
}
