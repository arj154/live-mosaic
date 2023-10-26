plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("io.gitlab.arturbosch.detekt")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }

    js(IR) {
        // Adding moduleName as a workaround for this issue: https://youtrack.jetbrains.com/issue/KT-51942
        moduleName = "live-mosaic-common"
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                implementation(kotlin("test"))
                implementation(libs.appyx.navigation)
                api(libs.appyx.components.backstack)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("@js-joda/timezone", "2.3.0"))
            }
        }
    }
}

android {
    namespace = "com.bumble.appyx.livemosaic"
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    defaultConfig {
        minSdk = 30
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.appyx.mutable.ui.processor)
    add("kspAndroid", libs.appyx.mutable.ui.processor)
    add("kspDesktop", libs.appyx.mutable.ui.processor)
    add("kspJs", libs.appyx.mutable.ui.processor)
}

detekt {
    source.setFrom("src")
    config.setFrom("../Detekt.yml")
    buildUponDefaultConfig = true
}
dependencies {
    detektPlugins(libs.detekt.compose.rules)
}
