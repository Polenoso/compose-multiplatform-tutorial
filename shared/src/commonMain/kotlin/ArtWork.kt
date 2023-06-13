import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Composable
fun ArtWork(modifier: Modifier = Modifier) {
    val scrollableState = rememberScrollState()
    Column(modifier = modifier.fillMaxHeight().scrollable(scrollableState, Orientation.Vertical), verticalArrangement = Arrangement.Center) {
        ArtContent(modifier = modifier)
    }
}

sealed class ArtWorkState {
    class Loading(): ArtWorkState()
    class Content(val artworks: List<ArtWorkData>): ArtWorkState()
    class Error(val e: Exception): ArtWorkState()
}

@Composable
fun ArtContent(modifier: Modifier) {
    val scope = rememberCoroutineScope()
    var state: ArtWorkState by remember { mutableStateOf(ArtWorkState.Loading()) }

    LaunchedEffect(false) {
        scope.launch {
            state = try {
                val list = ArtWorkService().artworksList()
                ArtWorkState.Content(list)
            } catch (e: Exception) {
                ArtWorkState.Error(e)
            }
        }
    }
    when (state) {
        is ArtWorkState.Loading -> Text("Loading")
        is ArtWorkState.Content -> Text((state as ArtWorkState.Content).artworks.first().author)
        is ArtWorkState.Error -> Text((state as ArtWorkState.Error).e.toString())
    }
}

@Serializable
data class ArtWorkData(
    val id: String,
    val author: String,
    val url: String,
    @SerialName("download_url")
    val downloadUrl: String
)

class ArtWorkService {
    private val client = httpClient()


    suspend fun artworksList(): List<ArtWorkData> {
        val response: HttpResponse = client.get("https://picsum.photos/v2/list?limit=10") {
            headers {
                contentType(ContentType.Application.Json)
            }
        }
        return response.body()
    }
}

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient