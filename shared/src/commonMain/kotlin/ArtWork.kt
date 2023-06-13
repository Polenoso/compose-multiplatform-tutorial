import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Composable
fun ArtWork(modifier: Modifier = Modifier) {
    Column(modifier = modifier
                        .fillMaxHeight()
                        .padding(12.dp),
            verticalArrangement = Arrangement.Center) {
        ArtContent(modifier = modifier)
    }
}

sealed class ArtWorkState {
    class Loading(): ArtWorkState()
    class Content(var artworks: List<ArtWorkData>): ArtWorkState()
    class Error(val e: Exception): ArtWorkState()
}

@Composable
fun ArtContent(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var state: ArtWorkState by remember { mutableStateOf(ArtWorkState.Loading()) }
    var index: Int by remember { mutableStateOf(0) }
    var currentPage: Int by remember { mutableStateOf(1) }

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
        is ArtWorkState.Content ->  {
            val artWorks = (state as ArtWorkState.Content).artworks
            val art = artWorks[index]
            Column(modifier = modifier.fillMaxHeight().fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ArtWorkImage(art.downloadUrl,
                    modifier = modifier.size(400.dp))
                Column(modifier = modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Author", textAlign = TextAlign.Center, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.size(12.dp))
                    Text(art.author, textAlign = TextAlign.Center, fontSize = 18.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Cursive)
                }
                Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { index-- }, enabled = index > 0, content = { Text("Previous") })
                    Spacer(Modifier.size(12.dp))
                    Button(onClick = { index++ }, enabled = index < artWorks.count() -1, content = { Text("Next") })
                }
                Button(onClick = {
                    currentPage ++
                                 scope.launch {
                                     state = try {
                                         val newArtworks = ArtWorkService().artworksList(currentPage)
                                         val currentArtworks = (state as ArtWorkState.Content).artworks
                                         ArtWorkState.Content(currentArtworks + newArtworks)
                                     } catch (e: Exception) {
                                         ArtWorkState.Error(e)
                                     }
                                 }
                },
                content = {
                    Text("Load More")
                })
            }
        }
        is ArtWorkState.Error -> Text((state as ArtWorkState.Error).e.toString())
    }
}

@Composable
fun ArtWorkImage(value: String, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val resource = asyncPainterResource(data = Url(value), key = value) {
        coroutineContext = scope.coroutineContext
    }

    Box(modifier = modifier.border(BorderStroke(1.dp, Color.Black)), contentAlignment = Alignment.Center) {
        KamelImage(
            resource = resource,
            contentDescription = value,
            onLoading = { progress -> CircularProgressIndicator(progress) },
            onFailure = {
                Text(it.toString())
            }
        )
    }
}

@Composable
fun ArtListContent(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var state: ArtWorkState by remember { mutableStateOf(ArtWorkState.Loading()) }
    var currentPage: Int by remember { mutableStateOf(1) }
    var canLoadMore: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(false) {
        scope.launch {
            state = try {
                canLoadMore = true
                val list = ArtWorkService().artworksList()
                ArtWorkState.Content(list)
            } catch (e: Exception) {
                ArtWorkState.Error(e)
            }
        }
    }
    when (state) {
        is ArtWorkState.Loading -> Text("Loading")
        is ArtWorkState.Content ->  {
            val artWorks = (state as ArtWorkState.Content).artworks
            Column(modifier = modifier.fillMaxHeight().fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
               ArtWorkList(artWorks, modifier = modifier, canLoadMore) {
                   scope.launch {
                       currentPage ++
                       canLoadMore = false
                       state = try {
                           val newArtworks = ArtWorkService().artworksList(currentPage)
                           val currentArtworks = (state as ArtWorkState.Content).artworks
                           ArtWorkState.Content(currentArtworks + newArtworks)
                       } catch (e: Exception) {
                           ArtWorkState.Error(e)
                       }
                       canLoadMore = true
                   }
               }
            }
        }
        is ArtWorkState.Error -> Text((state as ArtWorkState.Error).e.toString())
    }
}

@Composable
fun ArtWorkList(artworks: List<ArtWorkData>, modifier: Modifier = Modifier, isLoading: Boolean, onPagination: CoroutineScope.() -> Unit) {
    val lazyColumnState = rememberLazyListState()
    val shouldPaginate by remember { derivedStateOf {
        (isLoading && lazyColumnState.layoutInfo.totalItemsCount > 0 && (lazyColumnState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -9) >= artworks.size - 1 && lazyColumnState.firstVisibleItemIndex != 0)
    } }

    LazyColumn(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, state = lazyColumnState) {
        items(artworks) {artwork ->
            Card(modifier = Modifier.padding(6.dp), elevation = 4.dp) {
                Column {
                    ArtWorkImage(artwork.downloadUrl, modifier = Modifier.height(194.dp))
                    Text("By: ${artwork.author}", style = MaterialTheme.typography.h4)
                }
            }
        }
        item {
            if (isLoading) {
                LinearProgressIndicator()
            }
        }
    }
    LaunchedEffect(shouldPaginate, onPagination)
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


    suspend fun artworksList(page: Int = 1): List<ArtWorkData> {
        val response: HttpResponse = client.get("https://picsum.photos/v2/list?page=$page&limit=10") {
            headers {
                contentType(ContentType.Application.Json)
            }
        }
        return response.body()
    }
}

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient