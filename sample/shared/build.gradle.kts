plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    val macosX64 = macosX64()
    val macosArm64 = macosArm64()
    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()
    val watchosArm32 = watchosArm32()
    val watchosArm64 = watchosArm64()
    val watchosX64 = watchosX64()
    val watchosSimulatorArm64 = watchosSimulatorArm64()
    val tvosArm64 = tvosArm64()
    val tvosX64 = tvosX64()
    val tvosSimulatorArm64 = tvosSimulatorArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val appleMain by creating {
            dependsOn(commonMain)
            dependencies {
                api("com.rickclephas.kmp:nserror-kt")
            }
        }
        val appleTest by creating {
            dependsOn(commonTest)
        }
        listOf(
            macosX64, macosArm64,
            iosArm64, iosX64, iosSimulatorArm64,
            watchosArm32, watchosArm64, watchosX64, watchosSimulatorArm64,
            tvosArm64, tvosX64, tvosSimulatorArm64
        ).forEach {
            it.binaries.framework {
                baseName = "NSErrorKtSampleShared"
                export("com.rickclephas.kmp:nserror-kt")
            }
            getByName("${it.targetName}Main") {
                dependsOn(appleMain)
            }
            getByName("${it.targetName}Test") {
                dependsOn(appleTest)
            }
        }
    }
}
