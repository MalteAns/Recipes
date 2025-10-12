package de.malteans.recipes.dto

import kotlinx.serialization.Serializable

@Serializable
data class StepDto(
    val id: Long,
    val stepNumber: Int,
    val description: String,
    val duration: Int?,
)
