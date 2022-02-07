package com.rickclephas.kmp.nserrorkt

import kotlin.runtime.Kotlin_Any_getTypeInfo
import kotlinx.cinterop.COpaquePointer

internal fun getTypeInfo(obj: Any): COpaquePointer = Kotlin_Any_getTypeInfo(obj.asObjHeaderPtr())!!