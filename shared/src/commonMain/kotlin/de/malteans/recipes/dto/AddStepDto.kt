package de.malteans.recipes.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddStepDto(
    val description: String,
    val duration: Int? = null,
)
