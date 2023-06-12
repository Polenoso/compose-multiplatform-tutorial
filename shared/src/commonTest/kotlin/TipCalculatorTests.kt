import kotlin.test.Test
import kotlin.test.assertEquals

class TipCalculatorTests {
    @Test
    fun testTipCalculator() {
        val result = tipCalculator(100.0, 10.0, false)
        assertEquals<String>(expected = "10.0€", actual = result, message = "The result should be 10%")
    }
}