package com.rickclephas.kmp.nserrorkt.sample

import platform.Foundation.NSError
import com.rickclephas.kmp.nserrorkt.*
import kotlinx.coroutines.*

public fun testAsNSError(): NSError {
    return IllegalArgumentException("Fancy exception").asNSError()
}

public fun getCancellationException(): CancellationException = CancellationException()

@Throws(IllegalArgumentException::class)
public fun throwIllegalArgumentException() {
    throw IllegalArgumentException("Fancy thrown exception")
}

public fun testThrowAsNSError(throwable: Throwable): NSError {
    return throwable.throwAsNSError(CancellationException::class)
}

public fun testAsThrowable(nsError: NSError): Throwable {
    return nsError.asThrowable()
}
