package com.rickclephas.kmp.nserrorkt.sample

import platform.Foundation.NSError
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.rickclephas.kmp.nserrorkt.*
import kotlinx.coroutines.*
import kotlin.native.concurrent.freeze

interface NSErrorCallback {
    fun onCompleted(error: NSError)
}

interface NSErrorTest {
    fun test(callback: NSErrorCallback)
}

object TestTest {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default).freeze()

    fun test(test: NSErrorTest, crash: Boolean, onResult: (NSError?) -> Unit) {
        coroutineScope.launch {
            try {
                testNSError(test)
                onResult(null)
            } catch (e: Throwable) {
                if (crash) {
                    onResult(e.throwAsNSError(CancellationException::class))
                } else {
                    onResult(e.asNSError())
                }
            }
        }
    }

    fun getException() = IllegalArgumentException("Fancy exception").freeze()

    private suspend fun testNSError(test: NSErrorTest) = suspendCoroutine<Unit> { cont ->
        test.test(object : NSErrorCallback {
            override fun onCompleted(error: NSError) {
                cont.resumeWithException(error.asThrowable())
            }
        })
    }

}