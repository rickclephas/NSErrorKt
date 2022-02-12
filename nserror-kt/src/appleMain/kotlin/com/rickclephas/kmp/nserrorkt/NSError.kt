package com.rickclephas.kmp.nserrorkt

import kotlinx.cinterop.*
import kotlin.runtime.Kotlin_ObjCExport_RethrowExceptionAsNSError
import platform.Foundation.NSError
import platform.darwin.NSInteger
import platform.posix.NULL
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.native.internal.ObjCErrorException
import kotlin.reflect.KClass
import kotlin.runtime.Kotlin_Interop_refToObjC
import kotlin.runtime.Kotlin_ObjCExport_resumeContinuation

/**
 * Converts `this` [Throwable] to a [NSError].
 *
 * If `this` [Throwable] represents a [NSError], the original [NSError] is returned.
 * For other [Throwable]s a `KotlinException` [NSError] is returned:
 * ```
 * NSError.errorWithDomain("KotlinException", 0, mapOf(
 *     "KotlinException" to this,
 *     NSLocalizedDescriptionKey to this.message
 * ))
 * ```
 *
 * @see throwAsNSError
 * @see asThrowable
 */
fun Throwable.asNSError(): NSError = throwAsNSError(true)

/**
 * Tries to convert `this` [Throwable] to a [NSError].
 *
 * If `this` [Throwable] is an instance of one of the [exceptionClasses] or their subclasses,
 * it is converted to a [NSError] in the same way [asNSError] would.
 *
 * Other [Throwable]s are considered unhandled and will cause program termination
 * in the same way a [Throws] annotated function would.
 *
 * @see asNSError
 * @see asThrowable
 * @see Throws
 */
fun Throwable.throwAsNSError(vararg exceptionClasses: KClass<out Throwable>): NSError =
    throwAsNSError(exceptionClasses.any { it.isInstance(this) })

private fun Throwable.throwAsNSError(shouldPropagate: Boolean): NSError = memScoped {
    val objHeader = this@throwAsNSError.asObjHeaderPtr()
    val error = alloc<ObjCObjectVar<NSError>>()
    val types = when (shouldPropagate) {
        true -> allocArray<CPointerVar<*>>(2).apply {
            set(0, getTypeInfo(this@throwAsNSError))
        }
        false -> allocArray(1)
    }
    Kotlin_ObjCExport_RethrowExceptionAsNSError(objHeader, error.ptr, types)
    error.value
}

/**
 * Indicates if `this` [Throwable] represents a [NSError].
 */
val Throwable.isNSError: Boolean
    get() = this is ObjCErrorException

/**
 * Converts `this` [NSError] to a [Throwable].
 *
 * If `this` [NSError] represents a [Throwable], the original [Throwable] is returned.
 * For other [NSError]s an [ObjCErrorException] will be returned.
 *
 * @see asNSError
 */
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

/**
 * Indicates if `this` [NSError] represents a [Throwable].
 */
@OptIn(UnsafeNumber::class)
val NSError.isThrowable: Boolean
    get() = domain == "KotlinException" && code == 0.convert<NSInteger>() && userInfo.containsKey("KotlinException")