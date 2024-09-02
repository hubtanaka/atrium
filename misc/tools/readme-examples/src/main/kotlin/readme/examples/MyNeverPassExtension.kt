package readme.examples

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.InvocationInterceptor
import org.junit.jupiter.api.extension.ReflectiveInvocationContext
import java.lang.reflect.Method

class MyNeverPassExtension: InvocationInterceptor {
    override fun interceptTestMethod(
        invocation: InvocationInterceptor.Invocation<Void>?,
        invocationContext: ReflectiveInvocationContext<Method>?,
        extensionContext: ExtensionContext?
    ) {
        try {
            super.interceptTestMethod(invocation, invocationContext, extensionContext)
        } catch (e: Throwable) {
            throw e
        }

        throw Exception("$invocation did not throw any Throwable")
    }
}
