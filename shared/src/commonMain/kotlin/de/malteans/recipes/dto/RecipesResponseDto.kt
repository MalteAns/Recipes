package de.malteans.recipes.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipesResponseDto(
    val recipes: List<RecipeDto>
)
