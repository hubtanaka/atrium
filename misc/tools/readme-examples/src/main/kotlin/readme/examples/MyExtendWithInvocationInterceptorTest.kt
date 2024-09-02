package readme.examples

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MyNeverFailExtension::class)
class MyExtendWithInvocationInterceptorTest {
    @Test
    fun assertTrue() = assert(true)

    @Test
    fun assertFalse() = assert(false)
}
