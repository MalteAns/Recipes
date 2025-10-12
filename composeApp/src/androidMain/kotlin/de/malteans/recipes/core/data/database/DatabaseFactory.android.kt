package de.malteans.recipes.core.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.recipes.core.data.database.migrations.*

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<RecipeDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(RecipeDatabase.DB_NAME)
        return Room.databaseBuilder<RecipeDatabase>(
            context = appContext,
            name = dbFile.absolutePath
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
}