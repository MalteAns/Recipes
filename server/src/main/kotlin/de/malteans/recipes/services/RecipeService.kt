package de.malteans.recipes.services

import de.malteans.recipes.dto.AddRecipeDto
import de.malteans.recipes.dto.RecipeDto

interface RecipeService {

    suspend fun getAllRecipes(
        query: String = "",
    ): List<RecipeDto>

    suspend fun addRecipe(
        recipeDto: AddRecipeDto
    ): Int

    suspend fun updateRecipe(
        id: Int,
        recipeDto: AddRecipeDto
    )
}