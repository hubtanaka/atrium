package readme.examples

import org.junit.jupiter.api.Test
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor
import org.junit.platform.engine.*
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.descriptor.MethodSource
import java.lang.reflect.Method
import java.util.Locale

class ReadmeTestEngine: TestEngine {
    private val examples = mutableMapOf<String, String>()
    private val code = HashSet<String>()
    private val snippets = mutableMapOf<String, String>()

    override fun getId(): String = "junit5-readme"

    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {
        val engineDescriptor = EngineDescriptor(uniqueId, id)
        val configuration = DefaultJupiterConfiguration(discoveryRequest.configurationParameters)

        val classes = listOf( // TODO scan
            FirstExampleSpec::class.java,
            MostExamplesSpec::class.java,
        )

        classes.forEach { aClass ->
            val classTestDescriptor = ClassTestDescriptor(
                uniqueId.append("class", aClass.name),
                aClass,
                configuration
            )
            engineDescriptor.addChild(classTestDescriptor)

            val methods = aClass.methods.filter {
                it.isAnnotationPresent(Test::class.java) // @Test
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
        val default = Locale.getDefault()
        try {
            Locale.setDefault(Locale.UK)

            runJUnitWithCustomListener(request)

            processExamples(request)
        } catch (t: Throwable) {
            t.printStackTrace()
            Locale.setDefault(default)
        }
    }

    private fun processExamples(request: ExecutionRequest) {
        return // TODO implement
    }

    private fun runJUnitWithCustomListener(request: ExecutionRequest) {
        val executionListener = ReadmeExecutionListener(
            request.engineExecutionListener,
            examples,
            code
        )

        executionListener.execute(request.rootTestDescriptor)
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

    private class ReadmeExecutionListener(
        private val listener: EngineExecutionListener,
        private val examples: MutableMap<String, String>,
        private val code: MutableSet<String>,
    ) {
        fun execute(testDescriptor: TestDescriptor) {
            if(testDescriptor.isTest) return invokeTestMethod(listener, testDescriptor)

            if(testDescriptor.isContainer) {
                listener.executionStarted(testDescriptor)

                try {
                    testDescriptor.children.forEach { child: TestDescriptor ->
                        execute(child)
                    }
                } catch (t: Throwable) {
                    return listener.executionFinished(testDescriptor, TestExecutionResult.failed(t))
                }

                return listener.executionFinished(testDescriptor, TestExecutionResult.successful())
            }

            throw IllegalArgumentException("testDescriptor is not a test nor container: ${testDescriptor.displayName}") // TODO
        }

        private fun invokeTestMethod(listener: EngineExecutionListener, testDescriptor: TestDescriptor) {
            val method = testDescriptor.source.get() as MethodSource
            val instance = method.javaClass.getDeclaredConstructor().newInstance()
//            println("instance.javaClass.name = " + instance.javaClass.name)
//            println("method.methodName = " + method.methodName)

            listener.executionStarted(testDescriptor)
            try {
                method.javaMethod.invoke(instance)
                return handleSuccess(testDescriptor)
            } catch (t: Throwable) {
                return handleFailure(testDescriptor, t)
            }
        }

        /**
         * @see readme.examples.ReadmeExecutionListener.handleSuccess
         */
        private fun handleSuccess(testDescriptor: TestDescriptor) {
            val testName: String = testDescriptor.displayName

            if (!testName.startsWith("code")) {
                listener.executionFinished(
                    testDescriptor,
                    TestExecutionResult.failed(IllegalStateException("example tests are supposed to fail"))
                )
                return
            }
            if (code.contains(testName)) {
                listener.executionFinished(
                    testDescriptor,
                    TestExecutionResult.failed(IllegalStateException("code $testName is at least defined twice"))
                )
                return
            }

            code.add(testName)
            listener.executionFinished(testDescriptor, TestExecutionResult.successful())
        }

        /**
         * @see readme.examples.ReadmeExecutionListener.handleFailure
         */
        private fun handleFailure(testDescriptor: TestDescriptor, thrown: Throwable) {
            val testName: String = testDescriptor.displayName

            if (!testName.startsWith("ex")) {
                listener.executionFinished(
                    testDescriptor,
                    TestExecutionResult.failed(
                        IllegalStateException(
                            "only example tests are supposed to fail, not $testName",
                            thrown.cause // TODO verify
                        )
                    )
                )
                return
            }
            when (thrown.cause) { // TODO verify
                is AssertionError -> {
                    examples[testName] = thrown.cause!!.message!! // TODO verify
                    listener.executionFinished(testDescriptor, TestExecutionResult.successful())
                }
                else -> listener.executionFinished(testDescriptor, TestExecutionResult.failed(thrown))
            }
        }
    }
}
