package app.mihon.shared.data.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Android uses OkHttp — battle-tested, supports HTTP/2, WebSockets, and
 * integrates seamlessly with the Android TLS stack.
 */
actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = OkHttp
