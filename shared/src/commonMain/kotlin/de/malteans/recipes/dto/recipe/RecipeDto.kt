package de.malteans.recipes.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto (
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val ingredients: List<IngredientDto>,
    val steps: List<StepDto>,
    val workTime: Int?,
    val totalTime: Int?,
    val servings: Int?,
    val rating: Int?,
    val onlineRating: Double?,
    val sourceUrl: String?,
)