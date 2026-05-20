package de.malteans.recipes.di

import de.malteans.recipes.core.data.database.DatabaseFactory
import de.malteans.recipes.core.data.network.ApiConfig
import de.malteans.recipes.core.data.network.DesktopBuildConfig
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<ApiConfig> { ApiConfig(DesktopBuildConfig.API_TOKEN) }

        single<HttpClientEngine> { OkHttp.create() }
        single { DatabaseFactory() }
    }