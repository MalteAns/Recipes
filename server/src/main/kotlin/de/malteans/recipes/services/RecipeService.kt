package de.malteans.recipes.services

import de.malteans.recipes.dto.recipe.RecipeDto
import de.malteans.recipes.dto.recipe.add.AddRecipeDto

interface RecipeService {

    suspend fun getAllRecipes(
        query: String = "",
    ): List<RecipeDto>

    suspend fun addRecipe(
        recipeDto: AddRecipeDto
    ): Result<Int>

    suspend fun updateRecipe(
        id: Int,
        recipeDto: AddRecipeDto
    )
}