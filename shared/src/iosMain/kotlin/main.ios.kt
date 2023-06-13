import androidx.compose.ui.window.ComposeUIViewController
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { App() }

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Darwin) {
    config(this)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        })
    }
    engine {
        configureRequest {
            setAllowsCellularAccess(true)
        }
    }
}