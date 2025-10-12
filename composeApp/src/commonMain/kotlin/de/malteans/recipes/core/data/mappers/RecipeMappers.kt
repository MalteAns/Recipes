package de.malteans.recipes.core.data.mappers

import de.malteans.recipes.core.data.database.RecipeWithDetails
import de.malteans.recipes.core.data.database.entities.RecipeEntity
import de.malteans.recipes.core.domain.Recipe

// Mapping from domain Recipe to RecipeEntity.
fun Recipe.toRecipeEntity(): RecipeEntity {
    return RecipeEntity(
        id = this.id,
        cloudId = this.cloudId,  // include the cloud id
        sourceUrl = this.sourceUrl,
        name = this.name,
        cloudName = this.cloudName,
        description = this.description,
        cloudDescription = this.cloudDescription,
        imageUrl = this.imageUrl,
        cloudImageUrl = this.cloudImageUrl,
        workTime = this.workTime,
        cloudWorkTime = this.cloudWorkTime,
        totalTime = this.totalTime,
        cloudTotalTime = this.cloudTotalTime,
        servings = this.servings,
        cloudServings = this.cloudServings,
        rating = this.rating,
        onlineRating = this.onlineRating,
    )
}

// Mapping from RecipeWithDetails (the DB join) to domain Recipe.
fun RecipeWithDetails.toDomain(): Recipe {
    return Recipe(
        id = recipe.id,
        cloudId = recipe.cloudId,  // include cloudId from the database
        sourceUrl = recipe.sourceUrl,
        name = recipe.name,
        cloudName = recipe.cloudName,
        description = recipe.description,
        cloudDescription = recipe.cloudDescription,
        imageUrl = recipe.imageUrl,
        cloudImageUrl = recipe.cloudImageUrl,
        workTime = recipe.workTime,
        cloudWorkTime = recipe.cloudWorkTime,
        totalTime = recipe.totalTime,
        cloudTotalTime = recipe.cloudTotalTime,
        servings = recipe.servings,
        cloudServings = recipe.cloudServings,
        rating = recipe.rating,
        onlineRating = recipe.onlineRating,
        ingredients = recipeIngredients.filter { !it.recipeIngredient.isCloudData }.map {
            it.toDomain()
        },
        cloudIngredients = recipeIngredients.filter { it.recipeIngredient.isCloudData }.map {
            it.toDomain()
        },
        steps = recipeSteps.filter { !it.isCloudData }.sortedBy { it.stepNumber }.map { it.description },
        cloudSteps = recipeSteps.filter { it.isCloudData }.sortedBy { it.stepNumber }.map { it.description },
    )
}

fun Recipe.mapCloudWithLocal(localRecipe: Recipe): Recipe {
    return Recipe(
        id = localRecipe.id,
        cloudId = this.cloudId,
        sourceUrl = this.sourceUrl,
        name = localRecipe.name,
        cloudName = this.cloudName,
        description = localRecipe.description,
        cloudDescription = this.cloudDescription,
        imageUrl = localRecipe.imageUrl,
        cloudImageUrl = this.cloudImageUrl,
        ingredients = localRecipe.ingredients,
        cloudIngredients = this.cloudIngredients,
        steps = localRecipe.steps,
        cloudSteps = this.cloudSteps,
        workTime = localRecipe.workTime,
        cloudWorkTime = this.cloudWorkTime,
        totalTime = localRecipe.totalTime,
        cloudTotalTime = this.cloudTotalTime,
        servings = localRecipe.servings,
        cloudServings = this.cloudServings,
        rating = localRecipe.rating,
        onlineRating = this.onlineRating,
    )
}