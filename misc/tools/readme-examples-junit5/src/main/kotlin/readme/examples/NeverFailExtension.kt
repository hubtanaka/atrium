package readme.examples

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.InvocationInterceptor
import org.junit.jupiter.api.extension.ReflectiveInvocationContext
import java.lang.reflect.Method

class NeverFailExtension: InvocationInterceptor {
    override fun interceptTestMethod(
        invocation: InvocationInterceptor.Invocation<Void>,
        invocationContext: ReflectiveInvocationContext<Method>,
        extensionContext: ExtensionContext
    ) {
        invocationContext.executable.name
        try {
            super.interceptTestMethod(invocation, invocationContext, extensionContext)
        } catch (t: Throwable) {
            println("grasped: ${t.javaClass.simpleName}")
        }
    }
}
