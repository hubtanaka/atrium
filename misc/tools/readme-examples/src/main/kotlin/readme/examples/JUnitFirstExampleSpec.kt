package readme.examples

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.creating.Expect
import org.junit.jupiter.api.Test

class JUnitFirstExampleSpec {
    fun <T> expect(t: T): Expect<T> = readme.examples.utils.expect(t)

    @Test
    fun test1() {
        val x = 11
        expect(x).toEqual(11)
    }
}
