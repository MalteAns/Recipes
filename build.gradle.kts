plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.hotReload) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false

    alias(libs.plugins.android.kotlin.multiplatform.library) apply false

    // Only Android (androidApp)
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false

    // Serialization
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false

    // Room (DB)
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}