package de.malteans.recipes.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.recipes.core.data.database.RecipeDatabase

val RecipeDatabase.Companion.MIGRATION1_2: Migration
    get() = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        // Create a new table with the updated schema (renamed column 'title' -> 'name')
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS `RecipeEntity_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `imageUrl` TEXT NOT NULL,
                `workTime` INTEGER,
                `totalTime` INTEGER,
                `servings` INTEGER,
                `rating` INTEGER
            )
        """.trimIndent())

        // Copy the data from the old table to the new table, mapping 'title' to 'name'
        connection.execSQL("""
            INSERT INTO `RecipeEntity_new` (`id`,`name`,`description`,`imageUrl`,`workTime`,`totalTime`,`servings`,`rating`)
            SELECT `id`,`title`,`description`,`imageUrl`,`workTime`,`totalTime`,`servings`,`rating` FROM `RecipeEntity`
        """.trimIndent())

        // Remove the old table
        connection.execSQL("DROP TABLE RecipeEntity")

        // Rename the new table to the original table name
        connection.execSQL("ALTER TABLE RecipeEntity_new RENAME TO RecipeEntity")
    }
}
