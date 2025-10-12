package de.malteans.recipes.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.recipes.core.data.database.RecipeDatabase

val RecipeDatabase.Companion.MIGRATION6_7: Migration
    get() = object : Migration(6, 7) {
        override fun migrate(connection: SQLiteConnection) {
            // 1. Create the new RecipeEntity_new table
            connection.execSQL("""
                CREATE TABLE IF NOT EXISTS `RecipeEntity_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `cloudId` INTEGER DEFAULT NULL,
                    `sourceUrl` TEXT DEFAULT NULL,
                    `name` TEXT NOT NULL,
                    `cloudName` TEXT DEFAULT NULL,
                    `description` TEXT NOT NULL,
                    `cloudDescription` TEXT DEFAULT NULL,
                    `imageUrl` TEXT NOT NULL,
                    `cloudImageUrl` TEXT DEFAULT NULL,
                    `workTime` INTEGER,
                    `cloudWorkTime` INTEGER DEFAULT NULL,
                    `totalTime` INTEGER,
                    `cloudTotalTime` INTEGER DEFAULT NULL,
                    `servings` INTEGER,
                    `cloudServings` INTEGER DEFAULT NULL,
                    `rating` INTEGER,
                    `onlineRating` REAL DEFAULT NULL
                )
            """.trimIndent())
            // 2. Copy the data from RecipeEntity to RecipeEntity_new
            connection.execSQL("""
                INSERT INTO RecipeEntity_new (
                    `id`, `cloudId`, `sourceUrl`, `name`, `description`,
                    `imageUrl`, `workTime`, `totalTime`, `servings`, `rating`
                )
                SELECT
                    `id`, `cloudId`, `sourceUrl`, `name`, `description`,
                    `imageUrl`, `workTime`,`totalTime`, `servings`, `rating`
                FROM RecipeEntity
            """.trimIndent())

            // 3. Drop the old RecipeEntity table
            connection.execSQL("DROP TABLE RecipeEntity")

            // 4. Rename the new table to RecipeEntity
            connection.execSQL("ALTER TABLE RecipeEntity_new RENAME TO RecipeEntity")

            // 5. Add the 'isCloudData' column to RecipeIngredientEntity
            connection.execSQL("ALTER TABLE RecipeIngredientEntity ADD COLUMN isCloudData INTEGER NOT NULL DEFAULT 0;")

            // 6. Add the 'isCloudData' column to RecipeStepEntity
            connection.execSQL("ALTER TABLE RecipeStepEntity ADD COLUMN isCloudData INTEGER NOT NULL DEFAULT 0;")
        }
    }