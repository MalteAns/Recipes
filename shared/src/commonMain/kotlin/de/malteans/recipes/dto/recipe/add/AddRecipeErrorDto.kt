package de.malteans.recipes.dto.recipe.add

import kotlinx.serialization.Serializable

@Serializable
sealed interface AddRecipeResponseDto {
    @Serializable
    data class AddRecipeSuccessDto(
        val newRecipeCloudId: Long,
    ): AddRecipeResponseDto
    @Serializable
    data class AddRecipeErrorDto(
        val existingRecipeId: Long,
        val existingRecipeName: String,
    ): AddRecipeResponseDto
}


