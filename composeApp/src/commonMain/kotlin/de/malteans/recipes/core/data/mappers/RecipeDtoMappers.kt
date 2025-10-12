package de.malteans.recipes.core.data.mappers

import de.malteans.recipes.core.domain.Ingredient
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.domain.RecipeIngredientItem
import de.malteans.recipes.dto.IngredientDto
import de.malteans.recipes.dto.RecipeDto


fun IngredientDto.toDomain(): RecipeIngredientItem {
    return RecipeIngredientItem(
        ingredient = Ingredient(
            name = this.name,
            unit = this.unit ?: ""
        ),
        amount = this.amount,
    )
}

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = 0L,
        cloudId = this.id,
        sourceUrl = this.sourceUrl,
        name = this.name,
        cloudName = this.name,
        description = this.description,
        cloudDescription = this.description,
        imageUrl = this.imageUrl,
        cloudImageUrl = this.imageUrl,
        ingredients = this.ingredients.map { it.toDomain() },
        cloudIngredients = this.ingredients.map { it.toDomain() },
        steps = this.steps.sortedBy { it.stepNumber }.map { it.description },
        cloudSteps = this.steps.sortedBy { it.stepNumber }.map { it.description },
        workTime = this.workTime,
        cloudWorkTime = this.workTime,
        totalTime = this.totalTime,
        cloudTotalTime = this.totalTime,
        servings = this.servings,
        cloudServings = this.servings,
        rating = this.rating,
        onlineRating = this.onlineRating,
    )
}
