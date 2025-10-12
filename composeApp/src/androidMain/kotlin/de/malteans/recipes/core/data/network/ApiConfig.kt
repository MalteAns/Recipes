package de.malteans.recipes.core.data.network

import de.malteans.recipes.BuildConfig

actual object ApiConfig {
    actual val apiToken: String
        get() = BuildConfig.API_TOKEN
}
