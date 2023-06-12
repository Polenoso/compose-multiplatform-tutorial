import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TipCalculator(modifier: Modifier = Modifier) {
    var inputText: String by remember { mutableStateOf("") }
    val amount: Double = inputText.toDoubleOrNull() ?: 0.0
    var inputTip: String by remember { mutableStateOf("") }
    val tip: Double = inputTip.toDoubleOrNull() ?: 0.0
    var roundUp: Boolean by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Tip Calculator",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 24.sp
        )
        Spacer(Modifier.height(16.dp))
        EditNumberField(text = "Bill amount",
            value = inputText,
            onValueChanged = { inputText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
        EditNumberField(text = "Tip (%)",
            value = inputTip,
            onValueChanged = { inputTip = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
        RoundTipRow(roundUp, onRoundUpChange = { roundUp = it })
        Spacer(Modifier.height(24.dp))
        Text(
            "Tip: ${tipCalculator(amount, tip, roundUp)}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RoundTipRow(
    roundUp: Boolean,
    onRoundUpChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth().size(48.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("Round Up")
        Switch(
            roundUp,
            onRoundUpChange,
            modifier = Modifier.fillMaxWidth(),
            colors = SwitchDefaults.colors(uncheckedThumbColor = Color.LightGray)
        )
    }
}

@Composable
fun EditNumberField(
    text: String,
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
) {
    TextField(value,
        onValueChange = onValueChanged,
        modifier = modifier,
        label = { Text(text) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
    )
}

internal fun tipCalculator(bill: Double, tipPercent: Double = 15.0, roundUp: Boolean): String {
    var tip = tipPercent / 100 * bill
    if(roundUp) {
        tip = kotlin.math.ceil(tip)
    }
    return tip.toString() + "€"
}