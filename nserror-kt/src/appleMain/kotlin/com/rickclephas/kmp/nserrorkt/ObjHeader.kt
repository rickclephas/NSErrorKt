package com.rickclephas.kmp.nserrorkt

import kotlinx.cinterop.*
import kotlin.runtime.Kotlin_Interop_unwrapKotlinObjectHolder

internal fun Any.asObjHeaderPtr(): COpaquePointer {
    // TODO: Find a better way to get the ObjHeader
    val holder = interpretCPointer<CPointed>(createKotlinObjectHolder(this))
    return Kotlin_Interop_unwrapKotlinObjectHolder(holder)!!
}