package de.malteans.recipes.di

import de.malteans.recipes.core.data.database.DatabaseFactory
import de.malteans.recipes.core.data.network.ApiConfig
import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSBundle

actual val platformModule: Module
    get() = module {
        single<ApiConfig> { ApiConfig((NSBundle.mainBundle.objectForInfoDictionaryKey("API_TOKEN") as? String) ?: "") }

        single<HttpClientEngine> { Darwin.create() }
        single { DatabaseFactory() }
    }