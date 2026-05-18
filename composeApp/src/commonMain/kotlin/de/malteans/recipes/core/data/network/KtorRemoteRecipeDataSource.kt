package de.malteans.recipes.core.data.network

import de.malteans.recipes.Endpoints
import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import de.malteans.recipes.core.domain.errorHandling.map
import de.malteans.recipes.dto.RecipeDto
import de.malteans.recipes.dto.RecipesResponseDto
import de.malteans.recipes.dto.AddRecipeDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorRemoteRecipeDataSource(
    private val client: HttpClient,
) : RemoteRecipeDataSource {

    override suspend fun fetchRecipes(query: String): Result<List<RecipeDto>, DataError.Remote> {
        return safeCall<RecipesResponseDto> {
            client.get(Endpoints.Recipes.GetAll.url) {
                header("Authorization", "Bearer ${ApiConfig.apiToken}")
                parameter("query", query)
            }
        }.map { it.recipes }
    }

    override suspend fun uploadRecipe(recipeDto: AddRecipeDto): Result<Long, DataError.Remote> {
        return safeCall<Int> {
            client.post(Endpoints.Recipes.Add.url) {
                header("Authorization", "Bearer ${ApiConfig.apiToken}")
                contentType(ContentType.Application.Json)
                setBody(recipeDto)
            }
        }.map { it.toLong() }
    }
}
