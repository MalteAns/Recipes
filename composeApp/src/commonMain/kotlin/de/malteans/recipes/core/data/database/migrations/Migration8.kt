package de.malteans.recipes.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.recipes.core.data.database.RecipeDatabase

val RecipeDatabase.Companion.MIGRATION7_8: Migration
    get() = object : Migration(7, 8) {
        override fun migrate(connection: SQLiteConnection) {
            // Create a new table with the new schema.
            connection.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `RecipeIngredientEntity_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `recipeId` INTEGER NOT NULL,
                    `ingredientId` INTEGER NOT NULL,
                    `amount` REAL,
                    `overrideUnit` TEXT,
                    `isCloudData` INTEGER NOT NULL,
                    FOREIGN KEY(`recipeId`) REFERENCES `RecipeEntity`(`id`) ON DELETE CASCADE,
                    FOREIGN KEY(`ingredientId`) REFERENCES `IngredientEntity`(`id`) ON DELETE CASCADE
                )
                """.trimIndent()
            )

            // Create indexes for the new table.
            connection.execSQL("CREATE INDEX IF NOT EXISTS `index_RecipeIngredientEntity_new_recipeId` ON `RecipeIngredientEntity_new` (`recipeId`)")
            connection.execSQL("CREATE INDEX IF NOT EXISTS `index_RecipeIngredientEntity_new_ingredientId` ON `RecipeIngredientEntity_new` (`ingredientId`)")

            // Copy data from the old table into the new table.
            connection.execSQL(
                """
                INSERT INTO `RecipeIngredientEntity_new` (`recipeId`, `ingredientId`, `amount`, `overrideUnit`, `isCloudData`)
                SELECT `recipeId`, `ingredientId`, `amount`, `overrideUnit`, `isCloudData`
                FROM `RecipeIngredientEntity`
                """.trimIndent()
            )

            // Remove the old table.
            connection.execSQL("DROP TABLE `RecipeIngredientEntity`")

            // Rename the new table to the original table name.
            connection.execSQL("ALTER TABLE `RecipeIngredientEntity_new` RENAME TO `RecipeIngredientEntity`")
        }
    }