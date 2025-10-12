package de.malteans.recipes.core.data.network

import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import de.malteans.recipes.dto.RecipeDto

interface RemoteRecipeDataSource {

    suspend fun fetchRecipes(query: String): Result<List<RecipeDto>, DataError.Remote>
}