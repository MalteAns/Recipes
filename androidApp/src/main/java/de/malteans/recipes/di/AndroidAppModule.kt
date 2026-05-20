package de.malteans.recipes.di

import de.malteans.recipes.BuildConfig
import de.malteans.recipes.core.data.network.ApiConfig
import org.koin.core.module.Module
import org.koin.dsl.module

val androidAppModule: Module
    get() = module {
        single<ApiConfig> { ApiConfig(BuildConfig.API_TOKEN) }
    }