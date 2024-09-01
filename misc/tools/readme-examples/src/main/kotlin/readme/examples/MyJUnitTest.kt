package readme.examples

import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MyJUnitTest { // TODO delete this file at the end of debug
    @ParameterizedTest
    @CsvSource(
        "true",
        "false",
    )
    fun assertValue(value: Boolean){
        assert(value)
    }

    @Test
    fun assertTrue() {
        assert(true)
    }

    @Test
    fun assertFalse() {
        assert(false)
    }
}
