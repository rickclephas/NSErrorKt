//
//  ContentView.swift
//  NSErrorKtSample
//
//  Created by Rick Clephas on 09/02/2022.
//

import SwiftUI
import NSErrorKtSampleShared

struct ContentView: View {
    var body: some View {
        Button("Catch Throwable") {
            do {
                try NSErrorTestsKt.throwIllegalArgumentException()
            } catch {
                print(NSErrorKt.asThrowable(error))
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
