import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

actual fun getPlatformName(): String = "Android"


@Composable fun MainView() = App()