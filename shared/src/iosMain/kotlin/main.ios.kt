import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.ComposeUIViewController
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { App() }