plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    explicitApi()

    listOf(
        macosX64(), macosArm64(),
        iosArm64(), iosX64(), iosSimulatorArm64(),
        watchosArm32(), watchosArm64(), watchosX64(), watchosSimulatorArm64(),
        tvosArm64(), tvosX64(), tvosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "NSErrorKtSampleShared"
            export("com.rickclephas.kmp:nserror-kt")
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        appleMain {
            dependencies {
                api("com.rickclephas.kmp:nserror-kt")
            }
        }
    }
}
