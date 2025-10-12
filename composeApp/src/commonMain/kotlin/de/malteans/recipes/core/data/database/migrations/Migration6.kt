package de.malteans.recipes.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.recipes.core.data.database.RecipeDatabase

val RecipeDatabase.Companion.MIGRATION5_6: Migration
    get() = object : Migration(5, 6) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS `PlanEntity` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `recipeId` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL,
                        `timeOfDay` TEXT NOT NULL,
                        FOREIGN KEY(`recipeId`) REFERENCES `RecipeEntity`(`id`) ON DELETE CASCADE ON UPDATE NO ACTION
                    )
                """.trimIndent()
            )
            connection.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_PlanEntity_recipeId` ON `PlanEntity` (`recipeId`)"
            )
        }
    }
