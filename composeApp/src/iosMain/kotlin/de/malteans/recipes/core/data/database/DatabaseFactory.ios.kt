package de.malteans.recipes.core.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.recipes.core.data.database.migrations.*
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<RecipeDatabase> {
        val dbFilePath = documentDirectory() + "/${RecipeDatabase.DB_NAME}"
        return Room.databaseBuilder<RecipeDatabase>(
            name = dbFilePath,
        )
            .addMigrations(
                RecipeDatabase.MIGRATION1_2,
                RecipeDatabase.MIGRATION2_3,
                RecipeDatabase.MIGRATION3_4,
                RecipeDatabase.MIGRATION4_5,
                RecipeDatabase.MIGRATION5_6,
                RecipeDatabase.MIGRATION6_7,
                RecipeDatabase.MIGRATION7_8,
            )
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}