package de.malteans.recipes.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.recipes.core.data.database.RecipeDatabase

val RecipeDatabase.Companion.MIGRATION4_5: Migration
    get() = object : Migration(4, 5) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE RecipeEntity ADD COLUMN sourceUrl TEXT DEFAULT NULL")
        }
    }
