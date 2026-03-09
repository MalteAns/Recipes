package de.malteans.recipes.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddIngredientDto(
    val name: String,
    val amount: Double? = null,
    val unit: String? = null,
)
