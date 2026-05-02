package app.mihon.shared.data.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.apache5.Apache5

/**
 * Desktop (Windows + Linux) uses Apache HttpClient 5 — supports HTTP/2
 * and works correctly on both JVM targets without additional TLS config.
 */
actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = Apache5
