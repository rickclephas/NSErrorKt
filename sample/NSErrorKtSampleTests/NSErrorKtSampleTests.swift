//
//  NSErrorKtSampleTests.swift
//  NSErrorKtSampleTests
//
//  Created by Rick Clephas on 09/02/2022.
//

import XCTest
import NSErrorKtSampleShared
@testable import NSErrorKtSample

class NSErrorKtSampleTests: XCTestCase {

    func testFromKotlin() {
        let error = NSErrorTestsKt.testAsNSError() as NSError
        XCTAssertEqual("KotlinException", error.domain)
        XCTAssertEqual(0, error.code)
        XCTAssertTrue(NSErrorKt.isThrowable(error))
        let exception = error.userInfo["KotlinException"] as! KotlinThrowable
        XCTAssertFalse(exception.isNSError)
        let throwable = NSErrorKt.asThrowable(error)
        XCTAssertEqual(exception, throwable)
    }
    
    func testFromSwift() {
        let error = NSError(domain: "Test Domain", code: 1, userInfo: nil)
        XCTAssertFalse(NSErrorKt.isThrowable(error))
        let throwable = NSErrorTestsKt.testAsThrowable(nsError: error)
        XCTAssertTrue(throwable.isNSError)
        let nsError = throwable.asNSError() as NSError
        XCTAssertEqual(error, nsError)
    }
    
    func testThrowNSError() {
        let error = NSError(domain: "Test Domain", code: 1, userInfo: nil)
        let throwable = NSErrorKt.asThrowable(error)
        XCTAssertTrue(throwable.isNSError)
        let nsError = NSErrorTestsKt.testThrowAsNSError(throwable: throwable) as NSError
        XCTAssertFalse(NSErrorKt.isThrowable(nsError))
        XCTAssertEqual(error, nsError)
    }
    
    func testThrowThrowable() {
        let throwable = NSErrorTestsKt.getCancellationException()
        XCTAssertFalse(throwable.isNSError)
        let nsError = NSErrorTestsKt.testThrowAsNSError(throwable: throwable)
        XCTAssertTrue(NSErrorKt.isThrowable(nsError))
    }
    
//    func testThrowTerminatingThrowable() {
//        let error = NSErrorTestsKt.testAsNSError()
//        let throwable = NSErrorKt.asThrowable(error)
//        // This will terminate the program
//        NSErrorTestsKt.testThrowAsNSError(throwable: throwable)
//    }
}
