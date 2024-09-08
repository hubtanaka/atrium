package readme.examples

import org.junit.jupiter.engine.config.DefaultJupiterConfiguration
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor
import org.junit.platform.engine.*
import org.junit.platform.engine.support.descriptor.EngineDescriptor
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
        )

        classes.forEach { aClass ->
            val classTestDescriptor = ClassTestDescriptor(
                uniqueId.append("class", aClass.name),
                aClass,
                configuration
            )
            engineDescriptor.addChild(classTestDescriptor)

            val methods = aClass.methods
            methods.forEach { method: Method ->
                val testMethodDescriptor = TestMethodTestDescriptor(
                    uniqueId.append("method", method.name),
                    aClass,
                    method,
                    configuration
                )
                classTestDescriptor.addChild(testMethodDescriptor)
            }
        }

        dump(engineDescriptor)
        return engineDescriptor
    }

    override fun execute(request: ExecutionRequest) {
        val root = request.rootTestDescriptor
        val listener = request.engineExecutionListener
        listener.executionStarted(root)

        try {
            root.children.forEach {  testDescriptor: TestDescriptor ->
                listener.executionStarted(testDescriptor)
//            listener.reportingEntryPublished(testDescriptor, TODO())
                listener.executionFinished(testDescriptor, TestExecutionResult.successful())
            }
        } catch (e: Throwable) {
            listener.executionFinished(root, TestExecutionResult.failed(e))
        }

        listener.executionFinished(root, TestExecutionResult.successful())

    }

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

    private fun getClasses(packageName: String): List<Class<*>> {
        val resourceName: String = packageName.replace(".", "/")
        val classLoader = Thread.currentThread().contextClassLoader
        val root: URL = classLoader.getResource(resourceName)
            ?: throw IllegalArgumentException("failed to load package: $packageName")

        return when(root.protocol) {
            "file" -> {
                val rootFile: File = File(root.file)
                val files: List<File> = rootFile.listFiles()?.toList()
                    ?: throw IllegalStateException("package does not have any files: $packageName")
                return files.mapNotNull { file: File ->
                    getClass(packageName, file)
                }
            }
            "jar" -> throw NotImplementedError("jar")
            else -> throw NotImplementedError("else")
        }
    }

    private fun getClass(packageName: String, file: File): Class<*>? {
        val fileName = file.name.replace(".class$", "")
        val fullName = "$packageName.$fileName"

        try {
            return Class.forName(fullName)
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        return null
    }
}
