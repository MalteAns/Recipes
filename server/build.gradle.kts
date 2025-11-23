plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    application

    alias(libs.plugins.jetbrains.kotlin.serialization)
}

group = "de.malteans.recipes"
version = libs.versions.projectVersionName.get()
application {
    mainClass.set("de.malteans.recipes.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.bundles.ktor.server)
    implementation(libs.ktor.serialization.kotlinx.json)
    // Koin (DI)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    // Exposed + MariaDB (DB)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time) // DateTime support
    implementation(libs.mariadb.jdbc) // MariaDB
    implementation(libs.sqlite.jdbc) // SQLite
    // Http Client (for status page images),
    implementation(libs.ktor.client.cio)
}