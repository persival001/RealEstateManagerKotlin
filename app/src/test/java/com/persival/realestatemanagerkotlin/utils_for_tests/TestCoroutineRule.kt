package com.persival.realestatemanagerkotlin.utils_for_tests


import com.persival.realestatemanagerkotlin.data.utils.CoroutineDispatcherProvider
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestCoroutineRule : TestRule {

    private val testCoroutineScheduler = TestCoroutineScheduler()
    val mainDispatcher = StandardTestDispatcher(testCoroutineScheduler)
    val ioDispatcher = StandardTestDispatcher(testCoroutineScheduler)
    private val testCoroutineScope = TestScope(mainDispatcher)

    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(mainDispatcher)

            base.evaluate()

            // reset main dispatcher
            Dispatchers.resetMain()
        }
    }

    fun runTest(block: suspend TestScope.() -> Unit) = testCoroutineScope.runTest {
        block()
    }


    fun getTestCoroutineDispatcherProvider() = mockk<CoroutineDispatcherProvider> {
        every { main } returns mainDispatcher
        every { io } returns ioDispatcher
    }
}