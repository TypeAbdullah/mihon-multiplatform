package app.mihon.shared.data.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

/**
 * iOS uses Darwin (NSURLSession) — the native Apple networking stack.
 * This ensures App Transport Security (ATS) compliance out of the box.
 */
actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = Darwin
