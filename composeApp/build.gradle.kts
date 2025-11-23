import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hotReload)

    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

private val apiToken: String = gradleLocalProperties(rootDir, rootProject.providers)
    .getProperty("API_TOKEN")
    ?: System.getenv("API_TOKEN")
    ?: throw IllegalStateException(
        "Missing API_TOKEN property in local.properties or environment variables"
    )

val generateDesktopBuildConfig = tasks.register("generateDesktopBuildConfig") {
    notCompatibleWithConfigurationCache("Custom script writes file dynamically")

    val outputDir = layout.buildDirectory.dir("generated/buildConfig/desktopMain/kotlin")
    val packagePath = "de/malteans/recipes/core/data/network"
    val outputFile = outputDir.map { it.file("$packagePath/DesktopBuildConfig.kt") }

    inputs.property("apiToken", apiToken)
    outputs.file(outputFile)

    doLast {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package de.malteans.recipes.core.data.network

            object DesktopBuildConfig {
                const val API_TOKEN = "$apiToken"
            }
            """.trimIndent()
        )
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop") {
        compilations["main"].defaultSourceSet {
            kotlin.srcDir(generateDesktopBuildConfig.map {
                it.outputs.files.singleFile.parentFile
            })
        }
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(projects.shared)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended) // More Icons
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Koin (DI)
            implementation(libs.bundles.koin.client)

            // Navigation
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)

            // Room (DB)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            // Datetime
            implementation(libs.kotlinx.datetime)

            // Coil (Image loading)
            implementation(libs.bundles.coil)

            // Ktor (Networking)
            implementation(libs.bundles.ktor.client)

            // Back Handler
            implementation(libs.ui.backhandler)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            // HttpClient
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

android {
    namespace = "de.malteans.recipes"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.projectVersionCode.get().toInt()
        versionName = libs.versions.projectVersionName.get()
        versionNameSuffix = libs.versions.projectVersionNameSuffix.get()
        buildConfigField("String", "API_TOKEN", "\"$apiToken\"")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
}

compose.desktop {
    application {
        mainClass = "de.malteans.recipes.MainKt"

        buildTypes.release.proguard {
            isEnabled.set(false)
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            packageName = libs.versions.applicationName.get()
            packageVersion = libs.versions.projectVersionName.get()
        }
    }
}
