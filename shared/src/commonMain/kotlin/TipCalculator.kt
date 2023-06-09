import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TipCalculator(modifier: Modifier = Modifier) {
    var inputText: String by remember { mutableStateOf("") }
    val amount: Double = inputText.toDoubleOrNull() ?: 0.0

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
        EditNumberField(value = inputText) { inputText = it }
        Spacer(Modifier.height(24.dp))
        Text(
            "Tip: ${tipCalculator(amount)}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EditNumberField(modifier: Modifier = Modifier, value: String, onValueChanged: (String) -> Unit) {
    TextField(value,
        onValueChange = onValueChanged,
        modifier = modifier,
        label = { Text("Bill amount") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}

fun tipCalculator(bill: Double, tipPercent: Double = 15.0): String {
    val tip = tipPercent / 100 * bill
    return tip.toString() + "€"
}