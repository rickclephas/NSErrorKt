package com.rickclephas.kmp.nserrorkt.sample

import platform.Foundation.NSError
import com.rickclephas.kmp.nserrorkt.*
import kotlinx.coroutines.*

fun testAsNSError(): NSError {
    return IllegalArgumentException("Fancy exception").asNSError()
}

fun getCancellationException(): CancellationException = CancellationException()

fun testThrowAsNSError(throwable: Throwable): NSError {
    return throwable.throwAsNSError(CancellationException::class)
}

fun testAsThrowable(nsError: NSError): Throwable {
    return nsError.asThrowable()
}