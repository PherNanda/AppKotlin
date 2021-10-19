package com.snapshop.consumer.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Interface for Coroutine Dispatcher with [DefaultDispatcher] as its primary implementation
 * and [TestDispatcher] for use in tests.
 */
interface Dispatcher {

    fun io(): CoroutineDispatcher

    fun default(): CoroutineDispatcher

    fun unconfined(): CoroutineDispatcher

    fun main(): CoroutineDispatcher
}

/**
 * Primary implementation of [Dispatcher].
 */
class DefaultDispatcher : Dispatcher {

    override fun io(): CoroutineDispatcher = Dispatchers.IO

    override fun default(): CoroutineDispatcher = Dispatchers.Default

    override fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined

    override fun main(): CoroutineDispatcher = Dispatchers.Main
}

/**
 * Implementation of [Dispatcher] for tests.
 */
class TestDispatcher : Dispatcher {

    override fun io(): CoroutineDispatcher = Dispatchers.Unconfined

    override fun default(): CoroutineDispatcher = Dispatchers.Unconfined

    override fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined

    override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
}
