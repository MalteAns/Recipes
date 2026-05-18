package de.malteans.recipes.core.data.mappers

import de.malteans.recipes.core.domain.Ingredient
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.domain.RecipeIngredientItem
import de.malteans.recipes.dto.recipe.IngredientDto
import de.malteans.recipes.dto.recipe.RecipeDto
import de.malteans.recipes.dto.recipe.add.AddIngredientDto
import de.malteans.recipes.dto.recipe.add.AddRecipeDto
import de.malteans.recipes.dto.recipe.add.AddStepDto


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

fun Recipe.toAddRecipeDto(): AddRecipeDto {
    return AddRecipeDto(
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        ingredients = this.ingredients.map { item ->
            AddIngredientDto(
                name = item.ingredient.name,
                amount = item.amount,
                unit = item.ingredient.unit.takeIf { it.isNotBlank() }
            )
        },
        steps = this.steps.map { AddStepDto(description = it) },
        workTime = this.workTime,
        totalTime = this.totalTime,
        servings = this.servings,
        onlineRating = this.onlineRating,
        sourceUrl = this.sourceUrl
    )
}
