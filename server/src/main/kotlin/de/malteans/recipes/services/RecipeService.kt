package de.malteans.recipes.services

import de.malteans.recipes.dto.RecipeDto

interface RecipeService {

    suspend fun getAllRecipes(
        query: String = "",
    ): List<RecipeDto>
}