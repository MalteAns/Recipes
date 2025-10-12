package de.malteans.recipes.core.data.network

actual object ApiConfig {
    actual val apiToken: String
        get() = DesktopBuildConfig.API_TOKEN
}
