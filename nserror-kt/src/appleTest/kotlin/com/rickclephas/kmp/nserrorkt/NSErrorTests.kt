package com.rickclephas.kmp.nserrorkt

import kotlinx.cinterop.UnsafeNumber
import platform.Foundation.NSError
import kotlin.native.internal.ObjCErrorException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

@OptIn(UnsafeNumber::class)
class NSErrorTests {

    @Test
    fun testAsNSError() {
        val throwable = RuntimeException("Test message")
        val nsError = throwable.asNSError()
        assertEquals("KotlinException", nsError.domain)
        assertEquals(0, nsError.code)
        assertSame(throwable, nsError.userInfo["KotlinException"])
    }

    @Test
    fun testAsThrowable() {
        val nsError = NSError.errorWithDomain("Test domain", 1, null)
        val throwable = nsError.asThrowable()
        assertIs<ObjCErrorException>(throwable)
    }

    @Test
    fun testAsNsErrorAsThrowable() {
        val throwable = RuntimeException("Test message")
        val nsError = throwable.asNSError()
        val actualThrowable = nsError.asThrowable()
        assertSame(throwable, actualThrowable)
    }

    @Test
    fun testAsThrowableAsNSError() {
        val nsError = NSError.errorWithDomain("Test domain", 1, null)
        val throwable = nsError.asThrowable()
        val actualNsError = throwable.asNSError()
        assertEquals(nsError.domain, actualNsError.domain)
        assertEquals(nsError.code, actualNsError.code)
        assertEquals(nsError.userInfo, actualNsError.userInfo)
    }
}