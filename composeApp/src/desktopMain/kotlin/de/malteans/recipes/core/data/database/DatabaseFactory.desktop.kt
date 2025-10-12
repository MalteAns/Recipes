package de.malteans.recipes.core.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<RecipeDatabase> {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "Recipes")
            os.contains("mac") -> File(userHome, "Library/Application Support/Recipes")
            else -> File(userHome, ".local/share/Recipes")
        }

        if(!appDataDir.exists()) {
            appDataDir.mkdirs()
        }

        val dbFile = File(appDataDir, RecipeDatabase.DB_NAME)
        return Room.databaseBuilder(dbFile.absolutePath)
    }
}