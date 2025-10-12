package de.malteans.recipes.core.data.mappers

import de.malteans.recipes.core.data.database.entities.PlanEntity
import de.malteans.recipes.core.domain.PlannedRecipe
import de.malteans.recipes.core.domain.Recipe

fun PlannedRecipe.toEntity() : PlanEntity {
    return PlanEntity(
        id = this.id,
        recipeId = this.recipe.id,
        date = this.date,
        timeOfDay = this.timeOfDay,
    )
}

fun PlanEntity.toDomain(recipe: Recipe) : PlannedRecipe {
    return PlannedRecipe(
        id = this.id,
        recipe = recipe,
        date = this.date,
        timeOfDay = this.timeOfDay,
    )
}