package de.malteans.recipes.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddRecipeDto (
    val name: String,
    val description: String = "",
    val imageUrl: String,
    val ingredients: List<AddIngredientDto>,
    @SerialName("preparation")
    val steps: List<AddStepDto>,
    val workTime: Int? = null,
    val totalTime: Int? = null,
    val servings: Int? = null,
    val onlineRating: Double? = null,
    val sourceUrl: String? = null,
)