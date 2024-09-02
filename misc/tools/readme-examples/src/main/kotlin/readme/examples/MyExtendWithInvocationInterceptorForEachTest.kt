package readme.examples

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

class MyExtendWithInvocationInterceptorForEachTest {
    @ExtendWith(MyNeverFailExtension::class)
    @Test
    fun failedToPassed() = assert(false)

    @ExtendWith(MyNeverPassExtension::class)
    @Test
    fun passedToFailed() = assert(true)
}
