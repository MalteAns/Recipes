package de.malteans.recipes.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipesResponseDto(
    val recipes: List<RecipeDto>
)
