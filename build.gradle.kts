plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.compose.hotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    alias(libs.plugins.androidMultiplatformLibrary) apply false

    // Only Android (androidApp)
    alias(libs.plugins.androidApplication) apply false

    // Serialization
    alias(libs.plugins.kotlin.serialization) apply false

    // Room (DB)
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}