package de.malteans.recipes.dto.recipe.add

import kotlinx.serialization.Serializable

@Serializable
data class AddStepDto(
    val description: String,
    val duration: Int? = null,
)
