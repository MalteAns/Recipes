package de.malteans.recipes.core.data.network

import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import de.malteans.recipes.dto.recipe.RecipeDto
import de.malteans.recipes.dto.recipe.add.AddRecipeDto
import de.malteans.recipes.model.Image
import io.ktor.http.*

interface RemoteRecipeDataSource {

    suspend fun fetchRecipes(query: String): Result<List<RecipeDto>, DataError.Remote>

    suspend fun uploadRecipe(recipeDto: AddRecipeDto): kotlin.Result<Long>

    suspend fun uploadImage(
        fileName: String,
        mimeType: ContentType,
        imageBytes: ByteArray,
    ): kotlin.Result<Image>

    suspend fun deleteImage(
        imageId: String
    ): kotlin.Result<Unit>
}