# NSErrorKt

A Kotlin Multiplatform Library to improve `NSError` interop.

> **WARNING:** This is an experiment to try and improve Kotlin's `NSError` interop.  
> To achieve this the library exposes some Kotlin internals that aren't part of any public API!

## Why do we need this?

Kotlin already has `Throwable` to `NSError` interop for straightforward cases as described in 
[the docs](https://kotlinlang.org/docs/native-objc-interop.html#errors-and-exceptions).  
Though currently there is [no way][KT-50539] for application or library code to use this interop directly,
meaning applications and libraries need to create their own instead.

[KT-50539]: https://youtrack.jetbrains.com/issue/KT-50539

### Ktor

The [Ktor](https://ktor.io/) Darwin client does this by [wrapping][ktor-wrapping] a `NSError` in a custom `Exception`:
```kotlin
@OptIn(UnsafeNumber::class)
internal fun handleNSError(requestData: HttpRequestData, error: NSError): Throwable = when (error.code) {
    NSURLErrorTimedOut -> SocketTimeoutException(requestData)
    else -> DarwinHttpRequestException(error)
}
```

[ktor-wrapping]: https://github.com/ktorio/ktor/blob/0877d6a91f3879b91853d412abd167054dccb333/ktor-client/ktor-client-darwin/darwin/src/io/ktor/client/engine/darwin/TimeoutUtils.kt#L27-L31

Which is a great solution for your Kotlin code as it allows you to access the wrapped `NSError`.  
However once these `Exception`s reach Swift/ObjC, Kotlin will convert them to a `NSError`.  
Since Kotlin doesn't know this `Exception` is a wrapped `NSError` it will wrap the `Exception` in a `NSError` again.

This results in hard to log errors like this [one](https://kotlinlang.slack.com/archives/C3PQML5NU/p1640081553385000).

### KMP-NativeCoroutines

[KMP-NativeCoroutines](https://github.com/rickclephas/KMP-NativeCoroutines) has a similar problem 
where it needs to [convert][kmp-nc-convert] `Exception`s to `NSError`s:
```kotlin
@OptIn(UnsafeNumber::class)
internal fun Throwable.asNSError(): NSError {
    val userInfo = mutableMapOf<Any?, Any>()
    userInfo["KotlinException"] = this.freeze()
    val message = message
    if (message != null) {
        userInfo[NSLocalizedDescriptionKey] = message
    }
    return NSError.errorWithDomain("KotlinException", 0.convert(), userInfo)
}
```

[kmp-nc-convert]: https://github.com/rickclephas/KMP-NativeCoroutines/blob/c1b7821bfb822cdc458d5fae1a1939abb3e7b1e0/kmp-nativecoroutines-core/src/appleMain/kotlin/com/rickclephas/kmp/nativecoroutines/NSError.kt#L17-L26

It produces similar `NSError`s to the once Kotlin creates, but it doesn't unwrap an already wrapped `NSError`.  
And in case such an `NSError` reaches Kotlin again it will be wrapped in an `Exception` instead of being unwrapped.

So depending on your code this might result in hard to log errors as well.

## Usage

To solve these issues this library exposes the Kotlin `NSError` interop logic to your application and library code.

Consisting of 3 extension functions:
- [`Throwable.asNSError`](#asnserror)
- [`Throwable.throwAsNSError`](#throwasnserror)
- [`NSError.asThrowable`](#asthrowable)

and 2 extension properties:
- [`Throwable.isNSError`](#isnserror)
- [`NSError.isThrowable`](#isthrowable)

### asNSError

``````kotlin
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
fun Throwable.asNSError(): NSError
``````

### throwAsNSError

``````kotlin
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
fun Throwable.throwAsNSError(vararg exceptionClasses: KClass<out Throwable>): NSError
``````

### asThrowable

``````kotlin
/**
 * Converts `this` [NSError] to a [Throwable].
 *
 * If `this` [NSError] represents a [Throwable], the original [Throwable] is returned.
 * For other [NSError]s an [ObjCErrorException] will be returned.
 *
 * @see asNSError
 */
fun NSError.asThrowable(): Throwable
``````

### isNSError

``````kotlin
/**
 * Indicates if `this` [Throwable] represents a [NSError].
 */
val Throwable.isNSError: Boolean
``````

### isThrowable

``````kotlin
/**
 * Indicates if `this` [NSError] represents a [Throwable].
 */
val NSError.isThrowable: Boolean
``````
