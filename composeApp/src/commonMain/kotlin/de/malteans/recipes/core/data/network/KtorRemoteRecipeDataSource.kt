package de.malteans.recipes.core.data.network

import de.malteans.recipes.Constants
import de.malteans.recipes.Endpoints
import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import de.malteans.recipes.core.domain.errorHandling.map
import de.malteans.recipes.dto.*
import de.malteans.recipes.model.Image
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorRemoteRecipeDataSource(
    private val client: HttpClient,
) : RemoteRecipeDataSource {

    override suspend fun fetchRecipes(query: String): Result<List<RecipeDto>, DataError.Remote> {
        return oldSafeCall<RecipesResponseDto> {
            client.get(Endpoints.Recipes.GetAll.url) {
                header("Authorization", "Bearer ${ApiConfig.apiToken}")
                parameter("query", query)
            }
        }.map { it.recipes }
    }

    override suspend fun uploadRecipe(recipeDto: AddRecipeDto): Result<Long, DataError.Remote> {
        return oldSafeCall<Int> {
            client.post(Endpoints.Recipes.Add.url) {
                header("Authorization", "Bearer ${ApiConfig.apiToken}")
                contentType(ContentType.Application.Json)
                setBody(recipeDto)
            }
        }.map { it.toLong() }
    }

    override suspend fun uploadImage(
        fileName: String,
        mimeType: ContentType,
        imageBytes: ByteArray,
    ): kotlin.Result<Image> {
        val presignResp = safeCall<PresignResp> {
            client.post(Endpoints.Images.Presign.url) {
                header("Authorization", "Bearer ${ApiConfig.apiToken}")
                contentType(ContentType.Application.Json)
                setBody(
                    ImagePresignReq(
                        filename = fileName,
                        mimeType = mimeType.toString(),
                        size = imageBytes.size,
                    )
                )
            }
        }.getOrElse { return kotlin.Result.failure(it) }
        safeCall<Unit> {
            client.put(presignResp.putUrl) {
                header("Authorization", "Bearer ${ApiConfig.apiToken}")
                contentType(mimeType)
                setBody(imageBytes)
            }
        }.onFailure { return kotlin.Result.failure(it) }
        val finalizeResp = safeCall<FinalizeResp> {
            client.post(Endpoints.Images.Finalize(presignResp.id).url) {
                header("Authorization", "Bearer ${ApiConfig.apiToken}")
            }
        }.getOrElse { return kotlin.Result.failure(it) }
        return kotlin.Result.success(
            Image(
                id = presignResp.id,
                filename = fileName,
                publicUrl = Constants.BASE_URL + finalizeResp.publicUrl,
                mimeType = mimeType.toString(),
                byteSize = imageBytes.size,
                width = finalizeResp.width,
                height = finalizeResp.height,
            )
        )
    }

    override suspend fun deleteImage(
        imageId: String
    ) = safeCall<Unit> {
        client.delete(Endpoints.Images.Get(imageId).url) {
            header("Authorization", "Bearer ${ApiConfig.apiToken}")
        }
    }
}
