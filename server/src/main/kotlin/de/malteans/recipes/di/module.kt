package de.malteans.recipes.di

import de.malteans.recipes.services.ImageService
import de.malteans.recipes.services.RecipeService
import de.malteans.recipes.services.impl.LocalDiskImageService
import de.malteans.recipes.services.impl.RecipeServiceImpl
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val module = module {
    single<Database> {
        Database.connect(
            url = "jdbc:mariadb://192.168.178.53:3306/recipesDb",
            driver = "org.mariadb.jdbc.Driver",
            user = System.getenv("DB_USER"),
            password = System.getenv("DB_PASSWORD"),
        )
    }

    single<RecipeService> { RecipeServiceImpl(get()) }
    single<ImageService> { LocalDiskImageService(get()) }
}