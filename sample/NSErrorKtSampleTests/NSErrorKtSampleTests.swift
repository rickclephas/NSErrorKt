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
    
    class Test: NSErrorTest {
        
        let nsError: NSError
        
        init(nsError: NSError) {
            self.nsError = nsError
        }
        
        func test(callback: NSErrorCallback) {
            callback.onCompleted(error: nsError)
        }
    }

    func testExample() {
        let testTest = TestTest()
        
        let error = testTest.getException().asNSError() as NSError
//        let error = NSError(domain: "Test NSError", code: 2, userInfo: nil)
        
        let test = Test(nsError: error)
        let errorExpectation = expectation(description: "Waiting for error")
        testTest.test(test: test, crash: false) { error in
            print(error)
            errorExpectation.fulfill()
        }
        wait(for: [errorExpectation], timeout: 2)
    }

}
