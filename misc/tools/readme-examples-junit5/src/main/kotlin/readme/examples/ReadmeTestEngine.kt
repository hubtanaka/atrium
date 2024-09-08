package readme.examples

import org.junit.jupiter.engine.config.DefaultJupiterConfiguration
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor
import org.junit.platform.engine.*
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.descriptor.MethodSource
import org.junit.platform.launcher.TestExecutionListener
import java.io.File
import java.lang.reflect.Method
import java.net.URL

class ReadmeTestEngine: TestEngine {
    override fun getId(): String = "junit5-readme"

    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {
        val engineDescriptor = EngineDescriptor(uniqueId, id)
        val configuration = DefaultJupiterConfiguration(discoveryRequest.configurationParameters)

        val classes = listOf( // TODO scan
            FirstExampleSpec::class.java,
//            MostExamplesSpec::class.java,
        )

        classes.forEach { aClass ->
            val classTestDescriptor = ClassTestDescriptor(
                uniqueId.append("class", aClass.name),
                aClass,
                configuration
            )
            engineDescriptor.addChild(classTestDescriptor)

            val methods = aClass.methods.filter {
                it.name.startsWith("ex-") // TODO verify
            }
            methods.forEach { method: Method ->
                val testMethodDescriptor = TestMethodTestDescriptor(
                    classTestDescriptor.uniqueId.append("method", method.name),
                    aClass,
                    method,
                    configuration
                )
                classTestDescriptor.addChild(testMethodDescriptor)
            }
        }

//        dump(engineDescriptor)
        return engineDescriptor
    }

    override fun execute(request: ExecutionRequest) {
        execute(request.engineExecutionListener, request.rootTestDescriptor)
    }

    private fun execute(listener: EngineExecutionListener, testDescriptor: TestDescriptor) {
        listener.executionStarted(testDescriptor)
        if(testDescriptor.isTest) {
            val method = testDescriptor.source.get() as MethodSource
            val instance = method.javaClass.getDeclaredConstructor().newInstance()
            println("instance.javaClass.name = " + instance.javaClass.name)
            println("method.methodName = " + method.methodName)
            method.javaMethod.invoke(instance)
        }

        if(testDescriptor.isContainer) {
            try {
                testDescriptor.children.forEach { child: TestDescriptor ->
                    execute(listener, child)
                }
            } catch (t: Throwable) {
                // fail
                return listener.executionFinished(testDescriptor, TestExecutionResult.failed(t))
            }
        }

        // success
        listener.executionFinished(testDescriptor, TestExecutionResult.successful())
    }

    // debug
    private fun dump(testDescriptor: TestDescriptor) {
        println("uniqueId = " + testDescriptor.uniqueId)
        println("displayName = " + testDescriptor.displayName)
        println("children.size = " + testDescriptor.children.size)
        println("isRoot = " + testDescriptor.isRoot)
        println("isContainer = " + testDescriptor.isContainer)
        println("isTest = " + testDescriptor.isTest)

        if(testDescriptor.isContainer) {
            testDescriptor.children.forEach {
                dump(it)
            }
        }
    }
}
