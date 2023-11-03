package com.persival.realestatemanagerkotlin.utils_for_tests

import io.mockk.Call
import io.mockk.MockKAdditionalAnswerScope
import io.mockk.MockKAnswerScope
import io.mockk.MockKStubScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onStart
import org.assertj.core.api.Assertions.assertThat
import kotlin.coroutines.coroutineContext

fun <T> MockKStubScope<Unit, T>.ensuresDispatcher(
    coroutineDispatcher: CoroutineDispatcher
): MockKAdditionalAnswerScope<Unit, T> = ensuresDispatcher(coroutineDispatcher)

@OptIn(ExperimentalStdlibApi::class)
fun <T, B> MockKStubScope<T, B>.ensuresDispatcher(
    coroutineDispatcher: CoroutineDispatcher,
    answersBlock: suspend MockKAnswerScope<T, B>.(Call) -> T,
): MockKAdditionalAnswerScope<T, B> = coAnswers {
    assertThat(coroutineDispatcher).isEqualTo(coroutineContext[CoroutineDispatcher])
    answersBlock(call)
}

fun <T> emptyFlowOnDispatcher(coroutineDispatcher: CoroutineDispatcher): Flow<T> = emptyFlow<T>().onStart {
    assertThat(coroutineDispatcher).isEqualTo(coroutineDispatcher)
}
