package de.malteans.recipes.core.data.network

import de.malteans.recipes.Endpoints
import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import de.malteans.recipes.core.domain.errorHandling.map
import de.malteans.recipes.dto.RecipeDto
import de.malteans.recipes.dto.RecipesResponseDto
import io.ktor.client.*
import io.ktor.client.request.*

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
}
