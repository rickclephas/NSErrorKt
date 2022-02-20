package com.rickclephas.kmp.nserrorkt

import kotlinx.cinterop.*
import platform.Foundation.NSError
import platform.darwin.NSInteger
import kotlin.native.internal.GCUnsafeCall
import kotlin.native.internal.ObjCErrorException
import kotlin.reflect.KClass

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
    val error = alloc<ObjCObjectVar<NSError>>()
    val types = when (shouldPropagate) {
        true -> allocArray<CPointerVar<*>>(2).apply {
            set(0, interpretCPointer<CPointed>(getTypeInfo(this@throwAsNSError)))
        }
        false -> allocArray(1)
    }
    rethrowExceptionAsNSError(this@throwAsNSError, error.ptr, types)
    error.value
}

@GCUnsafeCall("Kotlin_Any_getTypeInfo")
private external fun getTypeInfo(obj: Any): NativePtr

@GCUnsafeCall("NSErrorKt_Kotlin_ObjCExport_RethrowExceptionAsNSError")
private external fun rethrowExceptionAsNSError(
    exception: Throwable,
    error: CPointer<ObjCObjectVar<NSError>>,
    types: CArrayPointer<CPointerVar<*>>
)

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
fun NSError.asThrowable(): Throwable = nsErrorAsException(this.objcPtr())

@GCUnsafeCall("NSErrorKt_Kotlin_ObjCExport_NSErrorAsException")
private external fun nsErrorAsException(error: NativePtr): Throwable

/**
 * Indicates if `this` [NSError] represents a [Throwable].
 */
@OptIn(UnsafeNumber::class)
val NSError.isThrowable: Boolean
    get() = domain == "KotlinException" && code == 0.convert<NSInteger>() && userInfo.containsKey("KotlinException")