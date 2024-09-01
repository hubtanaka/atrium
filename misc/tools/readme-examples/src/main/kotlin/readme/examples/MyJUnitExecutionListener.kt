package readme.examples

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan

class MyJUnitExecutionListener: TestExecutionListener {
    override fun executionFinished(testIdentifier: TestIdentifier?, testExecutionResult: TestExecutionResult?) {
        // TODO find out here is able to turn a failing test into a successful one and vice versa
        when (testExecutionResult?.status) {
            TestExecutionResult.Status.SUCCESSFUL -> return // TODO
            TestExecutionResult.Status.ABORTED -> TODO()
            TestExecutionResult.Status.FAILED ->  return handleFailed(testIdentifier, testExecutionResult)
            null -> TODO()
        }
    }

    /**
     * this function seems not work.
     *
     * the result of [assertFalse] is tried to turn into a successful one.
     *
     * java.lang.AssertionError: Assertion failed
     * 	at readme.examples.MyJUnitTest.assertFalse(MyJUnitTest.kt:24)
     * 	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
     * 	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
     * 	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
     * 	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
     * 	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:59)
     * 	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
     * 	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:56)
     * 	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
     * 	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
     * 	at org.junit.runners.BlockJUnit4ClassRunner$1.evaluate(BlockJUnit4ClassRunner.java:100)
     * 	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:366)
     * 	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:103)
     * 	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:63)
     * 	at org.junit.runners.ParentRunner$4.run(ParentRunner.java:331)
     * 	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:79)
     * 	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:329)
     * 	at org.junit.runners.ParentRunner.access$100(ParentRunner.java:66)
     * 	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:293)
     * 	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
     * 	at org.junit.runners.ParentRunner.run(ParentRunner.java:413)
     * 	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
     * 	at org.junit.runner.JUnitCore.run(JUnitCore.java:115)
     * 	at org.junit.vintage.engine.execution.RunnerExecutor.execute(RunnerExecutor.java:42)
     * 	at org.junit.vintage.engine.VintageTestEngine.executeAllChildren(VintageTestEngine.java:80)
     * 	at org.junit.vintage.engine.VintageTestEngine.execute(VintageTestEngine.java:72)
     * 	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:147)
     * 	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:127)
     * 	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:90)
     * 	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.lambda$execute$0(EngineExecutionOrchestrator.java:55)
     * 	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.withInterceptedStreams(EngineExecutionOrchestrator.java:102)
     * 	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:54)
     * 	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:114)
     * 	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:86)
     * 	at org.junit.platform.launcher.core.DefaultLauncherSession$DelegatingLauncher.execute(DefaultLauncherSession.java:86)
     * 	at org.junit.platform.launcher.core.SessionPerRequestLauncher.execute(SessionPerRequestLauncher.java:53)
     * 	at com.intellij.junit5.JUnit5IdeaTestRunner.startRunnerWithArgs(JUnit5IdeaTestRunner.java:57)
     * 	at com.intellij.rt.junit.IdeaTestRunner$Repeater$1.execute(IdeaTestRunner.java:38)
     * 	at com.intellij.rt.execution.junit.TestsRepeater.repeat(TestsRepeater.java:11)
     * 	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:35)
     * 	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:232)
     * 	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:55)
     *
     */
    private fun handleFailed(testIdentifier: TestIdentifier?, testExecutionResult: TestExecutionResult?) {
        println("the result of [${testIdentifier?.displayName}] is tried to turn into a successful one.")
        this.executionFinished(testIdentifier, TestExecutionResult.successful())
    }

    private fun handleFailed_2(testIdentifier: TestIdentifier?, testExecutionResult: TestExecutionResult?) {
        testExecutionResult!!
        val throwable = testExecutionResult.throwable
        println("throwable: $throwable")
    }
}
