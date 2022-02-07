package com.rickclephas.kmp.nserrorkt

import kotlinx.cinterop.*
import kotlin.runtime.Kotlin_ObjCExport_RethrowExceptionAsNSError
import platform.Foundation.NSError
import platform.posix.NULL
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
import kotlin.runtime.Kotlin_Interop_refToObjC
import kotlin.runtime.Kotlin_ObjCExport_resumeContinuation

fun Throwable.asNSError(): NSError = throwAsNSError(this::class)

fun Throwable.throwAsNSError(vararg exceptionClasses: KClass<out Throwable>): NSError {
    val shouldPropagate = exceptionClasses.any { it.isInstance(this) }
    return memScoped {
        val objHeader = this@throwAsNSError.asObjHeaderPtr()
        val error = alloc<ObjCObjectVar<NSError>>()
        // TODO: Find a way to get the type info of the classes instead
        val types = when (shouldPropagate) {
            true -> allocArray<CPointerVar<*>>(2).apply {
                set(0, getTypeInfo(this@throwAsNSError))
            }
            false -> allocArray(1)
        }
        Kotlin_ObjCExport_RethrowExceptionAsNSError(objHeader, error.ptr, types)
        error.value
    }
}

fun NSError.asThrowable(): Throwable {
    // TODO: Find a better way to get the error pointer
    val objHeader = Kotlin_Interop_refToObjC(this.asObjHeaderPtr())
    // TODO: Find a way to call Kotlin_ObjCExport_NSErrorAsException directly
    var throwable: Throwable? = null
    val continuation = Continuation<Any?>(EmptyCoroutineContext) {
        throwable = it.exceptionOrNull()
    }
    Kotlin_ObjCExport_resumeContinuation(continuation.asObjHeaderPtr(), NULL, objHeader)
    return throwable!!
}