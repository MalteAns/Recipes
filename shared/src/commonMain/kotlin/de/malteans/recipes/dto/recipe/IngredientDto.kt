package de.malteans.recipes.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val id: Long,
    val name: String,
    val amount: Double?,
    val unit: String?,
)
