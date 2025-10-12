package de.malteans.recipes.core.data.database

import androidx.room.Embedded
import androidx.room.Relation
import de.malteans.recipes.core.data.database.entities.IngredientEntity
import de.malteans.recipes.core.data.database.entities.RecipeEntity
import de.malteans.recipes.core.data.database.entities.RecipeIngredientEntity
import de.malteans.recipes.core.data.database.entities.RecipeStepEntity

data class RecipeIngredient(
    @Embedded val recipeIngredient: RecipeIngredientEntity,
    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredient: IngredientEntity
)

data class RecipeWithDetails(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = RecipeIngredientEntity::class
    )
    val recipeIngredients: List<RecipeIngredient>,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val recipeSteps: List<RecipeStepEntity>
)
