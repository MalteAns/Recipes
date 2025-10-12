package de.malteans.recipes.core.data.network

import platform.Foundation.NSBundle

actual object ApiConfig {
    actual val apiToken: String
        get() = (NSBundle.mainBundle.objectForInfoDictionaryKey("API_TOKEN") as? String) ?: ""
}
