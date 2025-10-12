package de.malteans.recipes.core.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.malteans.recipes.core.data.database.entities.*

@Database(
    entities = [IngredientEntity::class, RecipeEntity::class, RecipeIngredientEntity::class,
        RecipeStepEntity::class, StepIngredientEntity::class, PlanEntity::class],
    version = 8,
    exportSchema = false,
)
@TypeConverters(
    CustomTypeConverter::class
)
@ConstructedBy(RecipeDatabaseConstructor::class)
abstract class RecipeDatabase : RoomDatabase() {

    abstract val recipeDao: RecipeDao

    companion object {
        const val DB_NAME = "recipes.db"
    }
}
