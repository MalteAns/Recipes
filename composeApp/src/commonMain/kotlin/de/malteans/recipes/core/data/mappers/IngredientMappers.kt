package de.malteans.recipes.core.data.mappers

import de.malteans.recipes.core.data.database.RecipeIngredient
import de.malteans.recipes.core.data.database.entities.IngredientEntity
import de.malteans.recipes.core.domain.Ingredient
import de.malteans.recipes.core.domain.RecipeIngredientItem

// Mapping from IngredientEntity to domain Ingredient.
fun IngredientEntity.toDomain(): Ingredient {
    return Ingredient(
        id = this.id,
        name = this.name,
        unit = this.unit
    )
}

fun Ingredient.toIngredientEntity(): IngredientEntity {
    return IngredientEntity(
        id = this.id,
        name = this.name,
        unit = this.unit
    )
}

fun RecipeIngredient.toDomain(): RecipeIngredientItem {
    return RecipeIngredientItem(
        ingredient = this.ingredient.toDomain(),
        amount = this.recipeIngredient.amount,
        overrideUnit = this.recipeIngredient.overrideUnit,
    )
}