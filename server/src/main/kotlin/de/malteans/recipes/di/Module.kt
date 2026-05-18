package de.malteans.recipes.di

import de.malteans.recipes.services.ImageService
import de.malteans.recipes.services.RecipeService
import de.malteans.recipes.services.impl.LocalDiskImageService
import de.malteans.recipes.services.impl.RecipeServiceImpl
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.dsl.module

val module = module {
    single<Database> {
        Database.connect(
            url = "jdbc:sqlite:recipes.db",
            driver = "org.sqlite.JDBC",
        )
    }

    single<RecipeService> { RecipeServiceImpl(get()) }
    single<ImageService> { LocalDiskImageService(get()) }
}