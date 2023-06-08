import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.myapplication.MyApplication.MR
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.getImageByFileName

enum class LemonadeState {
    IDLE, PICKING, DRINKING, EMPTY
}

fun title(state: LemonadeState): String {
    return when (state) {
        LemonadeState.IDLE -> "Tap the lemon tree to select a lemon"
        LemonadeState.PICKING -> "Keep tapping the lemon to squeeze it"
        LemonadeState.DRINKING -> "Tap the lemonade to drink it"
        LemonadeState.EMPTY -> "Tap the empty glass to start again"
    }
}

fun imageName(state: LemonadeState): String {
    return when (state) {
        LemonadeState.IDLE -> "lemontree"
        LemonadeState.PICKING -> "lemon"
        LemonadeState.DRINKING -> "lemonade"
        LemonadeState.EMPTY -> "glass"
    }
}

@Composable
fun LemonadeMaker(modifier: Modifier = Modifier) {
    var state: LemonadeState by rememberSaveable { mutableStateOf(LemonadeState.IDLE, policy = referentialEqualityPolicy()) }
    var taps = 0
    val minTaps = (2..4).random()

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(60.dp))
        Text(title(state))
        Spacer(Modifier.height(25.dp))
        Button(onClick = {
            if (state == LemonadeState.PICKING) {
                taps++
                if (taps >= minTaps) {
                    taps = 0
                    state = LemonadeState.DRINKING
                    return@Button
                }
                return@Button
            }
            if (state == LemonadeState.IDLE) {
                state = LemonadeState.PICKING
                return@Button
            }
            if (state == LemonadeState.DRINKING) {
                state = LemonadeState.EMPTY
                return@Button
            }
            if (state == LemonadeState.EMPTY) {
                state = LemonadeState.IDLE
                return@Button
            }
        },
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Image(
                painter = painterResource( MR.images.getImageByFileName(imageName(state)) ?: MR.images.lemontree),
                contentDescription = title(state),
                modifier = Modifier.height(360.dp).wrapContentSize()
            )
        }
    }
 }

